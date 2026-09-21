import com.reservations.connector.CatalogConnector;

import com.reservations.model.Reservation;
import com.reservations.repository.ReservationRepository;
import com.reservations.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    //Definir comportamiento de los mocks y probar los métodos del servicio
    @DisplayName("Test getReservations method")
    @Test
    void getReservations() {
        //Given

        //When

        //Then


    }
    //Definir comportamiento de los mocks y probar los métodos del servicio
    @DisplayName("Test getReservationById method")
    @Test
    void getReservationById() {
        //Given
        ReservationService service = new ReservationService(repository, conversionService, catalogConnector);

        Reservation reservationModel = getReservation(1L, "EZE", "MIA");
        when(repository.getReservationById(1L)).thenReturn(Optional.of(reservationModel));

        ReservationDTO reservationDTO = getReservationDTO();
        when(conversionService.convert(reservationModel, ReservationDTO.class)).thenReturn(reservationDTO);
        //When
        ReservationDTO result = service.getReservationById(6L);
        //Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, "EZE","MIA")
        );

    }

    //Definir comportamiento de los mocks y probar los métodos del servicio
    @DisplayName("Test getReservationById method")
    @Test
    void getNotReservationById() {
        //Given
        ReservationService service = new ReservationService(repository, conversionService, catalogConnector);
        when(repository.getReservationById(6L)).thenReturn(Optional.empty());
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



}