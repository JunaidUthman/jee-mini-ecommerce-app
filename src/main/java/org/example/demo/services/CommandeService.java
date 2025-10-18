package org.example.demo.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.example.demo.entities.Commande;

import java.util.List;

@Stateless
@Transactional
public class CommandeService {

    @PersistenceContext(unitName = "ecommerce-pu") // replace with your persistence-unit name
    private EntityManager em;


    public void create(Commande commande) {
        em.persist(commande);
    }


    public Commande findById(Long id) {
        return em.find(Commande.class, id);
    }


    public List<Commande> findAll() {
        TypedQuery<Commande> query = em.createQuery("SELECT c FROM Commande c", Commande.class);
        return query.getResultList();
    }


    public void update(Commande commande) {
        em.merge(commande);
    }


    public void delete(Long id) {
        Commande commande = em.find(Commande.class, id);
        if (commande != null) {
            em.remove(commande);
        }
    }


    public Commande findByNumero(String numeroCommande) {
        TypedQuery<Commande> query = em.createQuery(
                "SELECT c FROM Commande c WHERE c.numeroCommande = :num", Commande.class);
        query.setParameter("num", numeroCommande);
        List<Commande> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Commande> findByClientId(Long clientId) {
        return em.createQuery("SELECT c FROM Commande c WHERE c.client.id = :clientId ORDER BY c.dateCommande DESC", Commande.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }
}
