package org.example.demo.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.demo.entities.LigneCommande;

import java.util.List;

@Stateless
@Transactional
public class LigneCommandeService {

    @PersistenceContext(unitName = "ecommerce-pu")
    private EntityManager em;


    public void save(LigneCommande ligneCommande) {
        em.persist(ligneCommande);
    }


    public LigneCommande findById(Long id) {
        return em.find(LigneCommande.class, id);
    }


    public List<LigneCommande> findAll() {
        return em.createQuery("SELECT l FROM LigneCommande l", LigneCommande.class)
                .getResultList();
    }


    public LigneCommande update(LigneCommande ligneCommande) {
        return em.merge(ligneCommande);
    }


    public void delete(Long id) {
        LigneCommande ligneCommande = em.find(LigneCommande.class, id);
        if (ligneCommande != null) {
            em.remove(ligneCommande);
        }
    }
}
