package com.reservations.repository;

import com.reservations.exception.ErrException;
import com.reservations.model.*;
import com.reservations.service.ReservationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.*;

class ParamReservationRepositoryTest {
    ReservationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ReservationRepository();

        // Cada ejecución empieza con la lista vacía
        ReservationRepository.reservations.clear();
    }

    @AfterEach
    void tearDown() {
        // Limpiar lo que guardó esta ejecución
        ReservationRepository.reservations.clear();
    }

    @DisplayName("Guardar una nueva reservacion correctamente op ValueSource")
    // @Test
    @ParameterizedTest
    @ValueSource(strings = { "AEP", "MIA", "BUE" })
    void save_should_return_the_information_param(String origin) {
        // Given

        // When
        Reservation result = repository.save(getReservation(null, origin, "AEP"));

        // Then
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(origin, result.getItinerary().getSegment().get(0).getOrigin()),
                () -> assertEquals("AEP", result.getItinerary().getSegment().get(0).getDestination()));
        // Debe haber una sola reserva almacenada
        assertEquals(1, repository.getReservations().size());
    }

    @DisplayName("Guardar una nueva reservacion correctamente op 2 ")
    // @Test
    @ParameterizedTest
    @CsvSource({ "MIA,AEP", "BUE,SCL", "BUE,MIA" })
    void save_should_return_the_information_paramCSV(String origin, String destination) {
        // Given

        // When
        Reservation result = repository.save(getReservation(null, origin, destination));

        // Then
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(origin, result.getItinerary().getSegment().get(0).getOrigin()),
                () -> assertEquals(destination, result.getItinerary().getSegment().get(0).getDestination()));
        // La primera reserva debe recibir el ID 1
        assertEquals(Long.valueOf(1L), result.getId());
    }

    @DisplayName("Guardar una nueva reservacion correctamente op 3 file ")
    // @Test
    @ParameterizedTest
    @CsvFileSource(resources = "/save-repository.csv")
    void save_should_return_the_information_paramCSVFile(String origin, String destination) {
        // Given

        // When
        Reservation result = repository.save(getReservation(null, origin, destination));

        // Then
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(origin, result.getItinerary().getSegment().get(0).getOrigin()),
                () -> assertEquals(destination, result.getItinerary().getSegment().get(0).getDestination()));

        // Lo devuelto debe ser el objeto que quedó almacenado
        assertSame(result, repository.getReservations().get(0));
    }

    private Reservation getReservation(Long id, String origin, String destination) {
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
        segment.setOrigin(origin);
        segment.setDestination(destination);
        segment.setCarrier("AA");
        segment.setId(1L);

        Itinerary itinerary = new Itinerary();
        itinerary.setId(1L);
        itinerary.setPrice(price);
        itinerary.setSegment(List.of(segment));

        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setPassengers(List.of(passenger));
        reservation.setItinerary(itinerary);
        return reservation;
    }
}