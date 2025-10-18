package org.example.demo.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.demo.entities.Panier;

import java.math.BigDecimal;
import java.util.List;

@Stateless
@Transactional
public class PanierService {

    @PersistenceContext(unitName = "ecommerce-pu")
    private EntityManager em;


    public void save(Panier panier) {
        em.persist(panier);
    }


    public Panier findById(Long id) {
        return em.find(Panier.class, id);
    }


    public List<Panier> findAll() {
        return em.createQuery("SELECT p FROM Panier p", Panier.class)
                .getResultList();
    }


    public Panier update(Panier panier) {
        return em.merge(panier);
    }


    public void delete(Long id) {
        Panier panier = em.find(Panier.class, id);
        if (panier != null) {
            em.remove(panier);
        }
    }


    public Panier findByClientId(Long clientId) {
        try {
            return em.createQuery("SELECT p FROM Panier p WHERE p.client.id = :clientId", Panier.class)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // no panier found
        }
    }


    public void updateMontantTotal(Panier panier) {
        if (panier.getLignesPanier() != null && !panier.getLignesPanier().isEmpty()) {
            var total = panier.getLignesPanier()
                    .stream()
                    .map(l -> l.getSousTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            panier.setMontantTotal(total);
            em.merge(panier);
        }
    }
}
