package com.reservations.service;

import com.reservations.connector.CatalogConnector;
import com.reservations.dto.ReservationDTO;
import com.reservations.exception.ErrException;
import com.reservations.model.Reservation;
import com.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private CatalogConnector catalogConnector;

    @InjectMocks
    private ReservationService service;

    @Test
    @DisplayName("Devuelve la reserva cuando el ID existe")
    void getReservationByIdExistente() {
        // 1. Preparar los datos y las respuestas simuladas
        Long id = 1L;
        Reservation modelo = new Reservation();
        ReservationDTO esperado = new ReservationDTO();

        when(repository.getReservationById(id)).thenReturn(Optional.of(modelo));

        when(conversionService.convert(modelo, ReservationDTO.class)).thenReturn(esperado);

        // 2. Ejecutar el método real del servicio
        ReservationDTO resultado = service.getReservationById(id);

        // 3. Comprobar el resultado y las llamadas realizadas
        assertSame(esperado, resultado);

        verify(repository).getReservationById(id);
        verify(conversionService).convert(modelo, ReservationDTO.class);
        verifyNoInteractions(catalogConnector);
    }

    @Test
    @DisplayName("Lanza una excepción cuando el ID no existe")
    void getReservationByIdInexistente() {
        // 1. Simular que el repositorio no encuentra la reserva
        Long id = 6L;

        when(repository.getReservationById(id)).thenReturn(Optional.empty());

        // 2. Ejecutar y comprobar la excepción esperada
        assertThrows(ErrException.class, () -> service.getReservationById(id));

        // 3. Comprobar que no se intenta convertir una reserva inexistente
        verify(repository).getReservationById(id);
        verifyNoInteractions(conversionService, catalogConnector);
    }
}