package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import javax.persistence.OptimisticLockException;

public class ReservationRetryHandler {

    private static final int MAX_RETRIES = 3;
    private ReservationService service;

    public ReservationRetryHandler(ReservationService service) {
        this.service = service;
    }

    public Reservation modifierAvecRetry(Reservation reservation) {
        int tentative = 0;
        while (tentative < MAX_RETRIES) {
            try {
                return service.modifierReservation(reservation);
            } catch (OptimisticLockException e) {
                tentative++;
                System.out.println("Conflit détecté, tentative de retry " + tentative);
                // Recharger la réservation pour obtenir la dernière version
                reservation = service.trouverParId(reservation.getId());
                // Appliquer les modifications nécessaires (exemple : réappliquer les changements)
                // Ici on suppose que la modification est toujours la même
                reservation.setSalle(reservation.getSalle());
            }
        }
        throw new OptimisticLockException("Échec après " + MAX_RETRIES + " tentatives");
    }
}