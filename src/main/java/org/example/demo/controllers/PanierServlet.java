package org.example.demo.controllers;

import jakarta.ejb.EJB;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.demo.entities.*;
import org.example.demo.services.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@WebServlet(name = "PanierServlet", urlPatterns = {"/panier"})
public class PanierServlet extends HttpServlet {

    @EJB
    private PanierService panierService;

    @EJB
    private LignePanierService lignePanierService;

    @EJB
    private ClientService clientService;

    @EJB
    private ProduitService produitService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier si l'utilisateur est connecté
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null ||
                !(Boolean) session.getAttribute("loggedIn")) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer le client connecté
        String userEmail = (String) session.getAttribute("userEmail");
        Client client = clientService.getClientByEmail(userEmail);

        if (client == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer le panier du client
        Panier panier = panierService.findByClientId(client.getId());

        // Si le panier n'existe pas, en créer un vide
        if (panier == null) {
            panier = new Panier();
            panier.setClient(client);
            panier.setDateCreation(new Date());
            panier.setMontantTotal(BigDecimal.ZERO);
            panierService.save(panier);
        }

        // Mettre à jour le montant total
        panierService.updateMontantTotal(panier);

        // Rafraîchir pour avoir les dernières données
        panier = panierService.findById(panier.getId());

        // Passer le panier à la JSP
        request.setAttribute("panier", panier);

        // Forward vers la page panier.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/authViews/panier.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier si l'utilisateur est connecté
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null ||
                !(Boolean) session.getAttribute("loggedIn")) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        switch (action) {
            case "add":
                ajouterProduit(request, response);
                break;
            case "update_quantity":
                updateQuantite(request, response);
                break;
            case "delete":
                supprimerLigne(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/panier");
                break;
        }
    }

    /**
     * Ajouter un produit au panier
     */
    private void ajouterProduit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        String userEmail = (String) session.getAttribute("userEmail");
        Client client = clientService.getClientByEmail(userEmail);

        String idProduitParam = request.getParameter("id_produit");
        if (idProduitParam == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        Long idProduit = Long.parseLong(idProduitParam);
        Produit produit = produitService.findById(idProduit);

        if (produit == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // Récupérer ou créer le panier
        Panier panier = panierService.findByClientId(client.getId());
        if (panier == null) {
            panier = new Panier();
            panier.setClient(client);
            panier.setDateCreation(new Date());
            panier.setMontantTotal(BigDecimal.ZERO);
            panierService.save(panier);
        }

        // Vérifier si le produit existe déjà dans le panier
        LignePanier ligneExistante = panier.getLignesPanier().stream()
                .filter(l -> l.getProduit().getId().equals(idProduit))
                .findFirst()
                .orElse(null);

        if (ligneExistante != null) {
            // Augmenter la quantité
            ligneExistante.setQuantite(ligneExistante.getQuantite() + 1);
            ligneExistante.setSousTotal(
                    ligneExistante.getPrixUnitaire().multiply(
                            BigDecimal.valueOf(ligneExistante.getQuantite())
                    )
            );
            lignePanierService.update(ligneExistante);
        } else {
            // Créer une nouvelle ligne
            LignePanier nouvelleLigne = new LignePanier();
            nouvelleLigne.setPanier(panier);
            nouvelleLigne.setProduit(produit);
            nouvelleLigne.setQuantite(1);
            nouvelleLigne.setPrixUnitaire(produit.getPrix());
            nouvelleLigne.setSousTotal(produit.getPrix());
            lignePanierService.save(nouvelleLigne);
        }

        // Mettre à jour le montant total
        panierService.updateMontantTotal(panier);

        // Rediriger vers le panier
        response.sendRedirect(request.getContextPath() + "/panier");
    }

    /**
     * Mettre à jour la quantité d'une ligne du panier
     */
    private void updateQuantite(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idLigneParam = request.getParameter("id_ligne");
        String quantiteParam = request.getParameter("quantite");

        if (idLigneParam == null || quantiteParam == null) {
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        try {
            Long idLigne = Long.parseLong(idLigneParam);
            int nouvelleQuantite = Integer.parseInt(quantiteParam);

            // Vérifier que la quantité est valide
            if (nouvelleQuantite <= 0) {
                // Si quantité <= 0, supprimer la ligne
                supprimerLigne(idLigne);
                response.sendRedirect(request.getContextPath() + "/panier");
                return;
            }

            // Récupérer la ligne
            LignePanier ligne = lignePanierService.findById(idLigne);
            if (ligne == null) {
                response.sendRedirect(request.getContextPath() + "/panier");
                return;
            }

            // Mettre à jour la quantité et le sous-total
            ligne.setQuantite(nouvelleQuantite);
            ligne.setSousTotal(
                    ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(nouvelleQuantite))
            );
            lignePanierService.update(ligne);

            // Mettre à jour le montant total du panier
            Panier panier = ligne.getPanier();
            panierService.updateMontantTotal(panier);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/panier");
    }

    /**
     * Supprimer une ligne du panier
     */
    private void supprimerLigne(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idLigneParam = request.getParameter("id_ligne");

        if (idLigneParam == null) {
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        try {
            Long idLigne = Long.parseLong(idLigneParam);
            supprimerLigne(idLigne);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/panier");
    }

    /**
     * Méthode utilitaire pour supprimer une ligne
     */
    private void supprimerLigne(Long idLigne) {
        LignePanier ligne = lignePanierService.findById(idLigne);
        if (ligne != null) {
            Panier panier = ligne.getPanier();
            lignePanierService.delete(idLigne);

            // Mettre à jour le montant total du panier
            panierService.updateMontantTotal(panier);
        }
    }
}