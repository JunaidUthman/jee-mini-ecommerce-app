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
import java.util.UUID;

@WebServlet(name = "CommandeServlet", urlPatterns = {"/commande"})
public class CommandeServlet extends HttpServlet {

    @EJB
    private CommandeService commandeService;

    @EJB
    private LigneCommandeService ligneCommandeService;

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

        String action = request.getParameter("action");

        if ("mes_commandes".equals(action)) {
            afficherMesCommandes(request, response);
        } else if ("details".equals(action)) {
            afficherDetailsCommande(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
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

        if ("passer_commande".equals(action)) {
            passerCommande(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    /**
     * Passer une commande à partir du panier
     */
    private void passerCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String userEmail = (String) session.getAttribute("userEmail");
        Client client = clientService.getClientByEmail(userEmail);

        if (client == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer le panier
        String idPanierParam = request.getParameter("id_panier");
        if (idPanierParam == null) {
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        Long idPanier = Long.parseLong(idPanierParam);
        Panier panier = panierService.findById(idPanier);

        // Vérifier que le panier appartient bien au client connecté
        if (panier == null || !panier.getClient().getId().equals(client.getId())) {
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        // Vérifier que le panier n'est pas vide
        if (panier.getLignesPanier() == null || panier.getLignesPanier().isEmpty()) {
            session.setAttribute("errorMessage", "Votre panier est vide !");
            response.sendRedirect(request.getContextPath() + "/panier");
            return;
        }

        // Récupérer l'adresse de livraison depuis le formulaire
        String adresseLivraison = request.getParameter("adresse_livraison");

        // Si l'adresse n'est pas fournie, utiliser une adresse par défaut ou rediriger
        if (adresseLivraison == null || adresseLivraison.trim().isEmpty()) {
            // Option 1: Utiliser l'adresse du client si elle existe
            // adresseLivraison = client.getAdresse();

            // Option 2: Rediriger vers une page pour saisir l'adresse
            session.setAttribute("errorMessage", "Veuillez fournir une adresse de livraison !");
            response.sendRedirect(request.getContextPath() + "/panier");
            return;

            // Option 3: Utiliser une valeur par défaut temporaire
            // adresseLivraison = "Adresse à définir";
        }

        try {
            // 1. Créer la commande
            Commande commande = new Commande();
            commande.setClient(client);
            commande.setNumeroCommande(genererNumeroCommande());
            commande.setDateCommande(new Date());
            commande.setStatut("EN_ATTENTE"); // ou "CONFIRMEE", selon votre logique
            commande.setMontantTotal(panier.getMontantTotal());
            commande.setAdresseLivraison(adresseLivraison); // ✅ AJOUTÉ

            // Sauvegarder la commande
            commandeService.create(commande);

            // 2. Transformer chaque ligne du panier en ligne de commande
            for (LignePanier lignePanier : panier.getLignesPanier()) {
                LigneCommande ligneCommande = new LigneCommande();
                ligneCommande.setCommande(commande);
                ligneCommande.setProduit(lignePanier.getProduit());
                ligneCommande.setQuantite(lignePanier.getQuantite());
                ligneCommande.setPrixUnitaire(lignePanier.getPrixUnitaire());
                ligneCommande.setSousTotal(lignePanier.getSousTotal());

                // Sauvegarder la ligne de commande
                ligneCommandeService.save(ligneCommande);

                // Optionnel : Mettre à jour le stock du produit
                Produit produit = lignePanier.getProduit();
                int nouveauStock = produit.getStock() - lignePanier.getQuantite();
                if (nouveauStock < 0) {
                    throw new RuntimeException("Stock insuffisant pour le produit: " + produit.getNom());
                }
                produit.setStock(nouveauStock);
                produitService.update(produit);
            }

            // 3. Vider le panier
            for (LignePanier lignePanier : panier.getLignesPanier()) {
                lignePanierService.delete(lignePanier.getId());
            }

            // Mettre à jour le montant total du panier à 0
            panier.setMontantTotal(BigDecimal.ZERO);
            panierService.update(panier);

            // 4. Message de succès et redirection
            session.setAttribute("successMessage", "Commande passée avec succès ! Numéro: " + commande.getNumeroCommande());
            session.setAttribute("numeroCommande", commande.getNumeroCommande());

            // Rediriger vers la page de confirmation
            response.sendRedirect(request.getContextPath() + "/commande?action=confirmation&numero=" + commande.getNumeroCommande());

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur lors du passage de la commande: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/panier");
        }
    }

    /**
     * Afficher les commandes du client
     */
    private void afficherMesCommandes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String userEmail = (String) session.getAttribute("userEmail");
        Client client = clientService.getClientByEmail(userEmail);

        if (client == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer toutes les commandes du client (vous devrez ajouter cette méthode au service)
        // List<Commande> commandes = commandeService.findByClientId(client.getId());
        // request.setAttribute("commandes", commandes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/mes_commandes.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Afficher les détails d'une commande
     */
    private void afficherDetailsCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String numeroCommande = request.getParameter("numero");
        if (numeroCommande == null) {
            response.sendRedirect(request.getContextPath() + "/commande?action=mes_commandes");
            return;
        }

        Commande commande = commandeService.findByNumero(numeroCommande);

        if (commande == null) {
            response.sendRedirect(request.getContextPath() + "/commande?action=mes_commandes");
            return;
        }

        request.setAttribute("commande", commande);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/authViews/details_commande.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Générer un numéro de commande unique
     */
    private String genererNumeroCommande() {
        // Format: CMD-TIMESTAMP-RANDOM
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CMD-" + timestamp + "-" + random;
    }
}