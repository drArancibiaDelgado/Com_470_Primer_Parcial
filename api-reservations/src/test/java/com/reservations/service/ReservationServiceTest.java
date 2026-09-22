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

    // Test 1: Obtener reserva por ID existente
    @Test
    @DisplayName("Devuelve la reserva cuando el ID existe")
    void getReservationByIdExistente() {
        Long id = 1L;
        Reservation modelo = new Reservation();
        ReservationDTO esperado = new ReservationDTO();

        when(repository.getReservationById(id)).thenReturn(Optional.of(modelo));
        when(conversionService.convert(modelo, ReservationDTO.class)).thenReturn(esperado);

        ReservationDTO resultado = service.getReservationById(id);

        assertSame(esperado, resultado);
        verify(repository).getReservationById(id);
        verify(conversionService).convert(modelo, ReservationDTO.class);
        verifyNoInteractions(catalogConnector);
    }

    // Test 2: Error al obtener reserva por ID inexistente
    @Test
    @DisplayName("Lanza una excepción cuando el ID no existe")
    void getReservationByIdInexistente() {
        Long id = 6L;
        when(repository.getReservationById(id)).thenReturn(Optional.empty());

        assertThrows(ErrException.class, () -> service.getReservationById(id));

        verify(repository).getReservationById(id);
        verifyNoInteractions(conversionService, catalogConnector);
    }

    // Test 3: Obtener lista de reservas con resultados
    @Test
    @DisplayName("Devuelve la lista de reservas convertidas")
    void getReservationsConResultados() {
        Reservation modelo = new Reservation();
        ReservationDTO dto = new ReservationDTO();
        List<Reservation> modelos = List.of(modelo);
        List<ReservationDTO> esperados = List.of(dto);

        when(repository.getReservations()).thenReturn(modelos);
        when(conversionService.convert(modelos, List.class)).thenReturn(esperados);

        List<ReservationDTO> resultado = service.getReservations();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(dto, resultado.get(0));
        verify(repository).getReservations();
        verify(conversionService).convert(modelos, List.class);
        verifyNoInteractions(catalogConnector);
    }

    // Test 4: Obtener lista de reservas vacía
    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay reservas")
    void getReservationsSinResultados() {
        List<Reservation> modelos = List.of();
        List<ReservationDTO> esperados = List.of();

        when(repository.getReservations()).thenReturn(modelos);
        when(conversionService.convert(modelos, List.class)).thenReturn(esperados);

        List<ReservationDTO> resultado = service.getReservations();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repository).getReservations();
        verify(conversionService).convert(modelos, List.class);
        verifyNoInteractions(catalogConnector);
    }

    // Test 5: Error al guardar reserva que ya tiene ID
    @Test
    @DisplayName("Rechaza crear una reserva que ya tiene ID")
    void saveConIdDebeLanzarExcepcion() {
        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setId(1L);

        assertThrows(ErrException.class, () -> service.save(solicitud));

        verifyNoInteractions(repository, conversionService, catalogConnector);
    }

    // Test 6: Guardar reserva correctamente con ciudades válidas
    @Test
    @DisplayName("Guarda una reserva sin ID con ciudades reconocidas")
    void saveReservaCorrecta() {
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO origen = new CityDTO();
        origen.setCode("EZE");
        origen.setName("Buenos Aires");

        CityDTO destino = new CityDTO();
        destino.setCode("MIA");
        destino.setName("Miami");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        Reservation modelo = new Reservation();
        Reservation guardada = new Reservation();
        ReservationDTO esperado = new ReservationDTO();
        esperado.setId(10L);

        when(conversionService.convert(solicitud, Reservation.class)).thenReturn(modelo);
        when(repository.save(modelo)).thenReturn(guardada);
        when(conversionService.convert(guardada, ReservationDTO.class)).thenReturn(esperado);

        ReservationDTO resultado = service.save(solicitud);

        assertSame(esperado, resultado);
        assertEquals(Long.valueOf(10L), resultado.getId());
        verify(catalogConnector).getCity("EZE");
        verify(catalogConnector).getCity("MIA");
        verify(conversionService).convert(solicitud, Reservation.class);
        verify(repository).save(modelo);
        verify(conversionService).convert(guardada, ReservationDTO.class);
    }

    // Test 7: Error al guardar reserva con ciudad de origen inexistente
    @Test
    @DisplayName("Rechaza una reserva cuando el origen no existe")
    void saveConOrigenInexistente() {
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("XXX");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO destino = new CityDTO();
        destino.setName("Miami");

        when(catalogConnector.getCity("XXX")).thenReturn(null);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        assertThrows(ErrException.class, () -> service.save(solicitud));

        verifyNoInteractions(conversionService, repository);
    }

    // Test 8: Error al guardar reserva con ciudad de destino inexistente
    @Test
    @DisplayName("Rechaza una reserva cuando el destino no existe")
    void saveConDestinoInexistente() {
        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("XXX");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO origen = new CityDTO();
        origen.setName("Buenos Aires");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("XXX")).thenReturn(null);

        assertThrows(ErrException.class, () -> service.save(solicitud));

        verifyNoInteractions(conversionService, repository);
    }

    // Test 9: Error al actualizar reserva que no existe
    @Test
    @DisplayName("Rechaza actualizar una reserva que no existe")
    void updateReservaInexistente() {
        Long id = 99L;
        ReservationDTO solicitud = new ReservationDTO();

        when(repository.getReservationById(id)).thenReturn(Optional.empty());

        assertThrows(ErrException.class, () -> service.update(id, solicitud));

        verify(repository).getReservationById(id);
        verify(repository, never()).update(anyLong(), any(Reservation.class));
        verifyNoInteractions(conversionService, catalogConnector);
    }

    // Test 10: Actualizar reserva correctamente con ciudades válidas
    @Test
    @DisplayName("Actualiza una reserva existente con ciudades reconocidas")
    void updateReservaCorrecta() {
        Long id = 1L;

        Reservation existente = new Reservation();
        ReservationDTO existenteDTO = new ReservationDTO();
        existenteDTO.setId(id);

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(existenteDTO);

        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO origen = new CityDTO();
        origen.setName("Buenos Aires");
        CityDTO destino = new CityDTO();
        destino.setName("Miami");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        Reservation nuevosDatos = new Reservation();
        Reservation actualizada = new Reservation();
        ReservationDTO esperado = new ReservationDTO();
        esperado.setId(id);
        esperado.setItinerary(itinerario);

        when(conversionService.convert(solicitud, Reservation.class)).thenReturn(nuevosDatos);
        when(repository.update(id, nuevosDatos)).thenReturn(actualizada);
        when(conversionService.convert(actualizada, ReservationDTO.class)).thenReturn(esperado);

        ReservationDTO resultado = service.update(id, solicitud);

        assertSame(esperado, resultado);
        assertEquals(id, resultado.getId());
        verify(repository).getReservationById(id);
        verify(catalogConnector).getCity("EZE");
        verify(catalogConnector).getCity("MIA");
        verify(conversionService).convert(solicitud, Reservation.class);
        verify(repository).update(id, nuevosDatos);
        verify(conversionService).convert(same(existente), eq(ReservationDTO.class));
        verify(conversionService).convert(same(actualizada), eq(ReservationDTO.class));
    }

    // Test 11: Error al actualizar reserva con origen inexistente
    @Test
    @DisplayName("Rechaza actualizar cuando el origen no existe")
    void updateConOrigenInexistente() {
        Long id = 1L;
        Reservation existente = new Reservation();
        ReservationDTO existenteDTO = new ReservationDTO();
        existenteDTO.setId(id);

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(existenteDTO);

        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("XXX");
        segmento.setDestination("MIA");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO destino = new CityDTO();
        destino.setName("Miami");

        when(catalogConnector.getCity("XXX")).thenReturn(null);
        when(catalogConnector.getCity("MIA")).thenReturn(destino);

        assertThrows(ErrException.class, () -> service.update(id, solicitud));

        verify(repository).getReservationById(id);
        verify(catalogConnector).getCity("XXX");
        verify(catalogConnector).getCity("MIA");
        verify(repository, never()).update(anyLong(), any(Reservation.class));
        verify(conversionService, never()).convert(solicitud, Reservation.class);
    }

    // Test 12: Error al actualizar reserva con destino inexistente
    @Test
    @DisplayName("Rechaza actualizar cuando el destino no existe")
    void updateConDestinoInexistente() {
        Long id = 1L;
        Reservation existente = new Reservation();
        ReservationDTO existenteDTO = new ReservationDTO();
        existenteDTO.setId(id);

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(existenteDTO);

        SegmentDTO segmento = new SegmentDTO();
        segmento.setOrigin("EZE");
        segmento.setDestination("XXX");

        ItineraryDTO itinerario = new ItineraryDTO();
        itinerario.setSegment(List.of(segmento));

        ReservationDTO solicitud = new ReservationDTO();
        solicitud.setItinerary(itinerario);

        CityDTO origen = new CityDTO();
        origen.setName("Buenos Aires");

        when(catalogConnector.getCity("EZE")).thenReturn(origen);
        when(catalogConnector.getCity("XXX")).thenReturn(null);

        assertThrows(ErrException.class, () -> service.update(id, solicitud));

        verify(repository).getReservationById(id);
        verify(catalogConnector).getCity("EZE");
        verify(catalogConnector).getCity("XXX");
        verify(repository, never()).update(anyLong(), any(Reservation.class));
        verify(conversionService, never()).convert(solicitud, Reservation.class);
    }

    // Test 13: Eliminar reserva existente correctamente
    @Test
    @DisplayName("Elimina una reserva existente")
    void deleteReservaExistente() {
        Long id = 1L;
        Reservation existente = new Reservation();
        ReservationDTO existenteDTO = new ReservationDTO();
        existenteDTO.setId(id);

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(existenteDTO);

        assertDoesNotThrow(() -> service.delete(id));

        verify(repository).getReservationById(id);
        verify(repository).delete(id);
        verifyNoInteractions(catalogConnector);
    }

    // Test 14: Error al eliminar reserva inexistente
    @Test
    @DisplayName("Rechaza eliminar una reserva que no existe")
    void deleteReservaInexistente() {
        Long id = 99L;
        when(repository.getReservationById(id)).thenReturn(Optional.empty());

        assertThrows(ErrException.class, () -> service.delete(id));

        verify(repository).getReservationById(id);
        verify(repository, never()).delete(anyLong());
        verifyNoInteractions(conversionService, catalogConnector);
    }

    // Test 15: Error al actualizar cuando la conversión devuelve null
    @Test
    @DisplayName("Rechaza actualizar cuando la conversion devuelve null")
    void updateConConversionNula() {
        Long id = 1L;
        Reservation existente = new Reservation();
        ReservationDTO solicitud = new ReservationDTO();

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(null);

        assertThrows(ErrException.class, () -> service.update(id, solicitud));

        verify(repository).getReservationById(id);
        verify(conversionService).convert(existente, ReservationDTO.class);
        verify(repository, never()).update(anyLong(), any(Reservation.class));
        verify(conversionService, never()).convert(solicitud, Reservation.class);
        verifyNoInteractions(catalogConnector);
    }

    // Test 16: Error al eliminar cuando la conversión devuelve null
    @Test
    @DisplayName("Rechaza eliminar cuando la conversion devuelve null")
    void deleteConConversionNula() {
        Long id = 1L;
        Reservation existente = new Reservation();

        when(repository.getReservationById(id)).thenReturn(Optional.of(existente));
        when(conversionService.convert(existente, ReservationDTO.class)).thenReturn(null);

        assertThrows(ErrException.class, () -> service.delete(id));

        verify(repository).getReservationById(id);
        verify(conversionService).convert(existente, ReservationDTO.class);
        verify(repository, never()).delete(anyLong());
        verifyNoInteractions(catalogConnector);
    }
}
