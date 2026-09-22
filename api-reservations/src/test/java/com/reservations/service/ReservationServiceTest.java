package com.reservations.service;

import com.reservations.connector.CatalogConnector;
import com.reservations.dto.ReservationDTO; // Importación agregada
import com.reservations.model.Reservation;
import com.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    @Mock
    ReservationRepository repository;

    @Mock
    ConversionService conversionService;

    @Mock
    CatalogConnector catalogConnector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Test getReservations method")
    @Test
    void getReservations() {
        // Por implementar
    }

    @DisplayName("Test getReservationById method")
    @Test
    void getReservationById() {
        // Given
        ReservationService service = new ReservationService(repository, conversionService, catalogConnector);

        Reservation reservationModel = getReservation(1L, "EZE", "MIA");
        when(repository.getReservationById(1L)).thenReturn(Optional.of(reservationModel));

        ReservationDTO reservationDTO = getReservationDTO();
        when(conversionService.convert(reservationModel, ReservationDTO.class)).thenReturn(reservationDTO);

        // When (Cambiado a 1L para que coincida con el mock)
        ReservationDTO result = service.getReservationById(1L);

        // Then
        assertAll(
                () -> assertNotNull(result, "El resultado no debe ser nulo")
                // Si tu DTO tiene un método getId(), descomenta la siguiente línea:
                // () -> assertEquals(1L, result.getId())
        );
    }

    @DisplayName("Test getNotReservationById method")
    @Test
    void getNotReservationById() {
        // Given
        ReservationService service = new ReservationService(repository, conversionService, catalogConnector);
        when(repository.getReservationById(6L)).thenReturn(Optional.empty());

        // Aquí debes agregar el Assert para verificar que lance una excepción o retorne null
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    // --- MÉTODOS AUXILIARES FALTANTES ---

    private Reservation getReservation(Long id, String origin, String destination) {
        Reservation reservation = new Reservation();
        // Configura los atributos de tu entidad real. Por ejemplo:
        // reservation.setId(id);
        return reservation;
    }

    private ReservationDTO getReservationDTO() {
        ReservationDTO dto = new ReservationDTO();
        // dto.setId(1L);
        return dto;
    }
}