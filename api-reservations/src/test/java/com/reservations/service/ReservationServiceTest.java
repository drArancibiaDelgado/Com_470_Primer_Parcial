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
import java.util.List;

//---- SER06
import com.reservations.dto.ItineraryDTO;
import com.reservations.dto.SegmentDTO;
import com.reservations.connector.response.CityDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // llamar el mockito para cada test
class ReservationServiceTest {

    @Mock // crea un repo simulado
    private ReservationRepository repository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private CatalogConnector catalogConnector;

    @InjectMocks
    private ReservationService service;

    @Test // indica que ese metodo es una prueba
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

    // ------ 2do
    @Test
    @DisplayName("Devuelve la lista de reservas convertidas")
    void getReservationsConResultados() {
        // Preparar una reserva y su DTO
        Reservation modelo = new Reservation();
        ReservationDTO dto = new ReservationDTO();

        List<Reservation> modelos = List.of(modelo);
        List<ReservationDTO> esperados = List.of(dto);

        when(repository.getReservations()).thenReturn(modelos);
        when(conversionService.convert(modelos, List.class)).thenReturn(esperados);

        // Ejecutar el servicio
        List<ReservationDTO> resultado = service.getReservations();

        // Comprobar el contenido y las llamadas
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(dto, resultado.get(0));

        verify(repository).getReservations();
        verify(conversionService).convert(modelos, List.class);
        verifyNoInteractions(catalogConnector);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay reservas")
    void getReservationsSinResultados() {
        // Preparar las respuestas vacías
        List<Reservation> modelos = List.of();
        List<ReservationDTO> esperados = List.of();

        when(repository.getReservations()).thenReturn(modelos);
        when(conversionService.convert(modelos, List.class)).thenReturn(esperados);

        // Ejecutar el servicio
        List<ReservationDTO> resultado = service.getReservations();

        // Comprobar que devuelve una lista vacía, no null
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(repository).getReservations();
        verify(conversionService).convert(modelos, List.class);
        verifyNoInteractions(catalogConnector);
    }

    @Test
    @DisplayName("Rechaza crear una reserva que ya tiene ID")
    void saveConIdDebeLanzarExcepcion() {
        // Preparar una solicitud que ya contiene un ID
        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setId(1L);

        // Ejecutar el servicio y comprobar que rechaza la solicitud
        assertThrows(ErrException.class, () -> service.save(solicitud));

        // Comprobar que se detiene antes de consultar o guardar
        verifyNoInteractions(repository, conversionService, catalogConnector);
    }

    @Test
    @DisplayName("Guarda una reserva sin ID con ciudades reconocidas")
    void saveReservaCorrecta() {
        // 1. Preparar la solicitud sin ID
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        // 2. Preparar las ciudades que devolverá el catálogo simulado
        CityDTO origen = new CityDTO();
        origen.setCode("EZE");
        origen.setName("Buenos Aires");

        CityDTO destino = new CityDTO();
        destino.setCode("MIA");
        destino.setName("Miami");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        // 3. Simular las conversiones y el guardado
        Reservation modelo = new Reservation();
        Reservation guardada = new Reservation();

        ReservationDTO esperado = new ReservationDTO();
        esperado.setId(10L);

        when(conversionService.convert(solicitud, Reservation.class)).thenReturn(modelo);

        when(repository.save(modelo)).thenReturn(guardada);

        when(conversionService.convert(guardada, ReservationDTO.class)).thenReturn(esperado);

        // 4. Ejecutar el método real
        ReservationDTO resultado = service.save(solicitud);

        // 5. Comprobar el resultado
        assertSame(esperado, resultado);
        assertEquals(Long.valueOf(10L), resultado.getId());

        // 6. Comprobar las llamadas a las dependencias
        verify(catalogConnector).getCity("EZE");
        verify(catalogConnector).getCity("MIA");
        verify(conversionService).convert(solicitud, Reservation.class);
        verify(repository).save(modelo);
        verify(conversionService).convert(guardada, ReservationDTO.class);
    }

    @Test
    @DisplayName("Rechaza una reserva cuando el origen no existe")
    void saveConOrigenInexistente() {
        // Preparar una reserva sin ID
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("XXX");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        // El destino existe, pero el origen no
        CityDTO destino = new CityDTO();
        destino.setName("Miami");

        when(catalogConnector.getCity("XXX")).thenReturn(null);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        // Comprobar que el servicio rechaza la reserva
        assertThrows(ErrException.class, () -> service.save(solicitud));

        // No debe convertir ni guardar la reserva
        verifyNoInteractions(conversionService, repository);
    }

    @Test
    @DisplayName("Rechaza una reserva cuando el destino no existe")
    void saveConDestinoInexistente() {
        // Preparar una reserva sin ID
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("XXX");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        // El origen existe, pero el destino no
        CityDTO origen = new CityDTO();
        origen.setName("Buenos Aires");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("XXX")).thenReturn(null);

        // Comprobar que el servicio rechaza la reserva
        assertThrows(ErrException.class, () -> service.save(solicitud));

        // No debe convertir ni guardar la reserva
        verifyNoInteractions(conversionService, repository);
    }
}