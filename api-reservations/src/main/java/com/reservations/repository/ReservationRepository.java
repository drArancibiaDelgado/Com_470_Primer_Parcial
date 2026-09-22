package com.reservations.repository;

import com.reservations.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class ReservationRepository {

    static List<Reservation> reservations = new ArrayList<>();

    static {
        Passenger passenger = new Passenger();
        passenger.setFirstName("Juan");
        passenger.setLastName("Bergman");
        passenger.setId(1L);
        passenger.setDocumentType("DNI");
        passenger.setDocumentNumber("12345678");
        passenger.setBirthday(LocalDate.of(1985, 1, 1));

        Price price = new Price();
        price.setBasePrice(BigDecimal.ONE);
        price.setTotalTax(BigDecimal.ZERO);
        price.setTotalPrice(BigDecimal.ONE);

        Segment segment = new Segment();
        segment.setArrival("2025-01-01");
        segment.setDeparture("2024-12-31");
        segment.setOrigin("EZE");
        segment.setDestination("MIA");
        segment.setCarrier("AA");
        segment.setId(1L);

        Itinerary itinerary = new Itinerary();
        itinerary.setId(1L);
        itinerary.setPrice(price);
        itinerary.setSegment(List.of(segment));

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setPassengers(List.of(passenger));
        reservation.setItinerary(itinerary);

        reservations.add(reservation);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservations.stream()
                .filter(reservation -> Objects.equals(reservation.getId(), id))
                .findFirst();
    }

    public Reservation save(Reservation reservation) {
        // Asigna un ID automático seguro basado en el tamaño actual + 1
        long nextId = reservations.isEmpty() ? 1L : reservations.stream()
                .mapToLong(r -> r.getId() != null ? r.getId() : 0L)
                .max().orElse(0L) + 1;

        reservation.setId(nextId);
        reservations.add(reservation);
        return reservation;
    }

    public Reservation update(Long id, Reservation reservation) {
        Optional<Reservation> existingOpt = getReservationById(id);
        if (existingOpt.isPresent()) {
            Reservation existing = existingOpt.get();
            existing.setItinerary(reservation.getItinerary());
            existing.setPassengers(reservation.getPassengers());
            return existing;
        }
        return null;
    }

    public void delete(Long id) {
        // Usa removeIf con Objects.equals para evitar NullPointerException de forma segura
        reservations.removeIf(reservation -> Objects.equals(reservation.getId(), id));
    }
}