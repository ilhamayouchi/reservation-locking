package com.example.service;

import com.example.model.Reservation;

import javax.persistence.*;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservationPU");

    @Override
    public Reservation creerReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            return reservation;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation modifierReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reservation managed = em.merge(reservation); // compare la version détachée avec la base
            em.getTransaction().commit();
            return managed;
        } catch (PersistenceException e) { // OptimisticLockException ou RollbackException
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof OptimisticLockException || e.getCause() instanceof OptimisticLockException) {
                throw new OptimisticLockException("Conflit de version sur la réservation " + reservation.getId(), e);
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation trouverParId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> listerReservations() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
        } finally {
            em.close();
        }
    }
}