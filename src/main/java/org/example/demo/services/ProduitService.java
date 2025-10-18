package org.example.demo.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.demo.entities.Produit;

import java.util.List;

@Stateless
@Transactional
public class ProduitService {

    @PersistenceContext(unitName = "ecommerce-pu")
    private EntityManager em;


    public void save(Produit produit) {
        em.persist(produit);
    }


    public Produit findById(Long id) {
        return em.find(Produit.class, id);
    }


    public List<Produit> findAll() {
        return em.createQuery("SELECT p FROM Produit p", Produit.class)
                .getResultList();
    }


    public Produit update(Produit produit) {
        return em.merge(produit);
    }


    public void delete(Long id) {
        Produit produit = em.find(Produit.class, id);
        if (produit != null) {
            em.remove(produit);
        }
    }


    public List<Produit> findByCategorie(String categorie) {
        return em.createQuery("SELECT p FROM Produit p WHERE p.categorie = :cat", Produit.class)
                .setParameter("cat", categorie)
                .getResultList();
    }


    public List<Produit> findInStock() {
        return em.createQuery("SELECT p FROM Produit p WHERE p.stock > 0", Produit.class)
                .getResultList();
    }
}
