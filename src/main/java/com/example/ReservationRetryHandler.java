package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ReservationRetryHandler {

    private static final int MAX_RETRIES = 3;
    private ReservationService service;

    public ReservationRetryHandler(ReservationService service) {
        this.service = service;
    }
    public Reservation modifierAvecRetry(Reservation reservation) {
        // On mémorise ce que l'utilisateur veut réellement modifier
        final Long id = reservation.getId();
        final String salleVoulue = reservation.getSalle();
        final LocalDateTime debutVoulu = reservation.getDateDebut();
        final LocalDateTime finVoulue = reservation.getDateFin();

        Reservation courante = reservation;
        for (int tentative = 1; tentative <= MAX_RETRIES; tentative++) {
            try {
                return service.modifierReservation(courante);
            } catch (OptimisticLockException e) {
                System.out.println("Conflit détecté, tentative de retry " + tentative);
                courante = service.trouverParId(id);      // dernière version
                courante.setSalle(salleVoulue);           // on réapplique le changement voulu
                courante.setDateDebut(debutVoulu);
                courante.setDateFin(finVoulue);
            }
        }
        throw new OptimisticLockException("Échec après " + MAX_RETRIES + " tentatives");
    }

}