package org.example.demo.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.demo.entities.LignePanier;

import java.util.List;

@Stateless
@Transactional
public class LignePanierService {

    @PersistenceContext(unitName = "ecommerce-pu")
    private EntityManager em;


    public void save(LignePanier lignePanier) {
        em.persist(lignePanier);
    }


    public LignePanier findById(Long id) {
        return em.find(LignePanier.class, id);
    }


    public List<LignePanier> findAll() {
        return em.createQuery("SELECT l FROM LignePanier l", LignePanier.class)
                .getResultList();
    }


    public LignePanier update(LignePanier lignePanier) {
        return em.merge(lignePanier);
    }


    public void delete(Long id) {
        LignePanier lignePanier = em.find(LignePanier.class, id);
        if (lignePanier != null) {
            em.remove(lignePanier);
        }
    }


    public List<LignePanier> findByPanierId(Long panierId) {
        return em.createQuery("SELECT l FROM LignePanier l WHERE l.panier.id = :id", LignePanier.class)
                .setParameter("id", panierId)
                .getResultList();
    }
}
