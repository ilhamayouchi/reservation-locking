package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ConcurrentReservationSimulator {

    public static void main(String[] args) {
        ReservationService service = new ReservationServiceImpl();

        // Création initiale d'une réservation
        Reservation reservation = new Reservation("Salle A", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));
        reservation = service.creerReservation(reservation);

        // Simulation de deux transactions concurrentes
        // Transaction 1 : récupère la réservation
        Reservation tx1 = service.trouverParId(reservation.getId());

        // Transaction 2 : récupère également la réservation
        Reservation tx2 = service.trouverParId(reservation.getId());

        // Transaction 1 modifie la réservation
        tx1.setSalle("Salle B");
        service.modifierReservation(tx1);
        System.out.println("Transaction 1 : modification réussie.");

        // Transaction 2 tente de modifier la réservation avec l'ancienne version
        try {
            tx2.setSalle("Salle C");
            service.modifierReservation(tx2);
            System.out.println("Transaction 2 : modification réussie.");
        } catch (OptimisticLockException e) {
            System.err.println("Transaction 2 : conflit détecté - " + e.getMessage());
        }
    }
}