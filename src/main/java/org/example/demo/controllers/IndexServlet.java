package org.example.demo.controllers;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import org.example.demo.entities.Client;
import org.example.demo.entities.Panier;
import org.example.demo.entities.Produit;
import org.example.demo.entities.LignePanier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.services.ClientService;
import org.example.demo.services.LignePanierService;
import org.example.demo.services.PanierService;
import org.example.demo.services.ProduitService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet contrôleur pour la page d'accueil
 * Pattern MVC2 : Une servlet avec plusieurs actions
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/index"})
public class IndexServlet extends HttpServlet {

    @Inject
    private ProduitService produitService;

    @Inject
    private ClientService  clientService;

    @Inject
    private PanierService  panierService;

    @Inject
    private LignePanierService lignePanierService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer l'action demandée (par défaut : accueil)
        String action = request.getParameter("action");

        if (action == null) {
            action = "accueil";
        }

        // Router vers la bonne méthode selon l'action
        switch (action) {
            case "accueil":
                afficherAccueil(request, response);
                break;
            case "panier" : savePanier(request, response);
            case "afficher_panier":
                // 1. Vérifier si l'utilisateur est connecté
                HttpSession sessionPanier = request.getSession(false);
                if (sessionPanier == null || sessionPanier.getAttribute("loggedIn") == null ||
                        !(Boolean) sessionPanier.getAttribute("loggedIn")) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=login");
                    return;
                }

                // 2. Récupérer le client connecté
                String emailPanier = (String) sessionPanier.getAttribute("userEmail");
                Client clientPanier = clientService.getClientByEmail(emailPanier);

                if (clientPanier == null) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=login");
                    return;
                }

                // 3. Récupérer le panier du client
                Panier panierClient = panierService.findByClientId(clientPanier.getId());

                // 4. Si le panier n'existe pas, en créer un vide
                if (panierClient == null) {
                    panierClient = new Panier();
                    panierClient.setClient(clientPanier);
                    panierClient.setDateCreation(new Date());
                    panierClient.setMontantTotal(BigDecimal.ZERO);
                    panierService.save(panierClient);
                }

                // 5. Charger les lignes du panier (normalement déjà chargées avec EAGER fetch)
                // Mais on peut aussi les récupérer explicitement si besoin
                List<LignePanier> lignes = lignePanierService.findByPanierId(panierClient.getId());

                // 6. Mettre à jour le montant total du panier
                panierService.updateMontantTotal(panierClient);

                // 7. Rafraîchir le panier pour avoir les données à jour
                panierClient = panierService.findById(panierClient.getId());

                // 8. Passer les données au JSP
                request.setAttribute("panier", panierClient);
                request.setAttribute("lignes", lignes);

                // 9. Forward vers la page panier
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/panier.jsp");
                dispatcher.forward(request, response);
                break;
            default:
                afficherAccueil(request, response);
                break;

        }
    }

    public void savePanier(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Get the current session (do not create a new one)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null || !(Boolean) session.getAttribute("loggedIn")) {
            // User not logged in → redirect to login
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // 2. Get user email from session
        String userEmail = (String) session.getAttribute("userEmail");
        Client client = clientService.getClientByEmail(userEmail); // fetch the full Client object
        if (client == null) {
            // Session corrupted or user not found → force login
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        System.out.println("savePanier !!!!!!!!!!!!!!!");

        // 3. Get the product ID from request
        String idParam = request.getParameter("id_produit");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }
        Long idProduit = Long.parseLong(idParam);

        // 4. Find the product
        Produit produit = produitService.findById(idProduit);
        if (produit == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // 5. Find or create the client's panier
        Panier panier = panierService.findByClientId(client.getId());
        if (panier == null) {
            panier = new Panier();
            panier.setClient(client);
            panier.setDateCreation(new Date());
            panier.setMontantTotal(BigDecimal.ZERO);
            panierService.save(panier);
        }

        // 6. Add product to LignePanier
        LignePanier ligne = new LignePanier();
        ligne.setPanier(panier);
        ligne.setProduit(produit);
        ligne.setQuantite(1);
        ligne.setPrixUnitaire(produit.getPrix());  // Fixed: changed from getPrixUnitaire to setPrixUnitaire
        ligne.setSousTotal(produit.getPrix().multiply(BigDecimal.valueOf(1)));  // Added: calculate subtotal
        lignePanierService.save(ligne);

        // 7. Redirect to panier page
        response.sendRedirect(request.getContextPath() + "/index?action=afficher_panier");
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Affiche la page d'accueil avec les produits
     */
    private void afficherAccueil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Créer une liste de produits hardcodés pour le moment
        List<Produit> produits = produitService.findAll();

        // Passer les données à la JSP
        request.setAttribute("produits", produits);
        request.setAttribute("nombreProduits", produits.size());

        // Forward vers la JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }





}