package com.reservations.repository;

import com.reservations.enums.APIError;
import com.reservations.exception.ErrException;
import com.reservations.model.*;
import com.reservations.service.ReservationService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.in;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Tags(@Tag("repository"))
class ReservationRepositoryTest {
    ReservationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ReservationRepository();
        System.out.println("antes de cada prueba");
    }

    @AfterEach
    void tearDown() {
        System.out.println("despues de cada prueba");
    }


    @DisplayName("Obtener todas las reservaciones existentes")
    @Test
    void getReservations() {
        // When
        List<Reservation> result = repository.getReservations();

        // Then
        assertAll(() -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).getId()));
    }

    @DisplayName("Obtener todas las reservaciones existentes usando matchers")
    @Test
    void getReservationsHamcres() {
        // When
        List<Reservation> result = repository.getReservations();

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertThat(result,hasSize(1)),
                () -> assertThat(result.get(0),hasProperty("id")),
                () -> assertThat(result.get(0).getPassengers().get(0).getFirstName(),stringContainsInOrder("J","n")),
                () -> assertThat(result.get(0).getPassengers().get(0).getFirstName(),matchesRegex("[a-zA-Z]+"))


        );
    }


    // hamcres
    @Disabled
    @DisplayName("Obtener todas las reservaciones existentes H")
    @Test
    void getReservationsHamcrest() {
        // When
        List<Reservation> result = repository.getReservations();

        // Then
        assertAll(() -> assertNotNull(result), () -> assertThat(result, hasSize(1)),
                () -> assertThat(result.get(0), hasProperty("id")),
                () -> assertThat(result.get(0).getPassengers().get(0).getFirstName(), stringContainsInOrder("J", "a")),
                () -> assertThat(result.get(0).getPassengers().get(0).getFirstName(), matchesRegex("[a-zA-Z]+")));
    }

    //@Tag("")
    @DisplayName("Reservacion que deberia retornar la informacion")
    @Test
    void get_Reservation_Return_By_Id() {
        // When
        Optional<Reservation> result = repository.getReservationById(1L);

        // Then
        assertAll(() -> assertNotNull(result), () -> assertTrue(result.isPresent()),
                () -> assertEquals(1L, result.get().getId()),
                // comprobacion del objeto en su conjunto
                () -> assertEquals(getReservation(1L, "EZE", "MIA"), result.get()));
    }

    @Tag("error-case")
    @DisplayName("Reservacion que no deberia retornar la informacion")
    @Test
    void getReservationNotReturnById() {
        // When
        Optional<Reservation> result = repository.getReservationById(6L);

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()));
    }

    @DisplayName("Guardar una nueva reservacion correctamente")
    @Test
    void save_should_return_the_information() {
        // Given

        // When
        Reservation result = repository.save(getReservation(null, "MIA", "AEP"));

        // Then
        assertAll(() -> assertNotNull(result),
                () -> assertEquals("MIA", result.getItinerary().getSegment().get(0).getOrigin()),
                () -> assertEquals("AEP", result.getItinerary().getSegment().get(0).getDestination()));
    }



    @Tag("success-case")
    @DisplayName("Actualizar los datos de una reservacion existente")
    @Test
    void update() {
        // Given

        // When

        // Then

    }

    @Tag("success-case")
    @DisplayName("Eliminar una reservacion por su ID")
    @Test
    void delete() {
        // When
        // Then
    }

    @DisplayName("Excepcion")
    @Test
    void getReservationNotReturnByIdExcepcion() {
        // Given
        ReservationService service = new ReservationService(repository, null, null);
        // when
        ErrException excepcion = assertThrows(ErrException.class, () -> {
            service.getReservationById(6L);
        });

        // Then
        assertAll(() -> assertNotNull(excepcion)

        );
    }

    @Disabled
    @DisplayName("Reservacion   no quiero que se ejecute")
    @Test
    void get_Reservation_Return_By_Id_no_eje() {
        // When
        Optional<Reservation> result = repository.getReservationById(1L);

        // Then
        assertAll(() -> assertNotNull(result), () -> assertTrue(result.isPresent()),
                () -> assertEquals(1L, result.get().getId()),
                () -> assertEquals(getReservation(1L, "EZE", "MIA"), result.get()));
    }

    private Reservation getReservation(Long id, String origin, String destination) {
        Passenger passenger = new Passenger();
        passenger.setFirstName("Juan11");
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