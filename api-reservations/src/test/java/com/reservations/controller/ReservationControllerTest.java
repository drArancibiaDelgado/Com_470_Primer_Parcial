package com.reservations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservations.dto.ReservationDTO;
import com.reservations.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir objetos Java a JSON

    @MockBean
    private ReservationService service; // Aislamos la capa de servicio

    @Test
    void testGetReservationById_Retorna200Ok() throws Exception {
        // Arrange: Preparamos el mock para el método getReservationById
        Long id = 1L;
        ReservationDTO mockRespuesta = new ReservationDTO();

        when(service.getReservationById(id)).thenReturn(mockRespuesta);

        // Act & Assert: Simulamos una petición GET a /reservation/{id}
        mockMvc.perform(get("/reservation/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Esperamos un HTTP 200 OK
    }
    @Test
    void testSave_Retorna201Created() throws Exception {
        // Arrange
        ReservationDTO peticion = new ReservationDTO();
        peticion.setId(1L);

        // 1. Creamos un pasajero ficticio con sus datos obligatorios
        com.reservations.dto.PassengerDTO pasajero = new com.reservations.dto.PassengerDTO();
        pasajero.setFirstName("Diego"); // <-- Dato obligatorio agregado
        pasajero.setLastName("Arancibia"); // <-- Dato obligatorio agregado

        // 2. Asignamos la lista con el pasajero a la petición
        peticion.setPassengers(java.util.List.of(pasajero));

        // 3. Creamos un itinerario (recomendado porque tiene @Valid)
        com.reservations.dto.ItineraryDTO itinerario = new com.reservations.dto.ItineraryDTO();
        peticion.setItinerary(itinerario);

        ReservationDTO respuestaMock = new ReservationDTO();

        when(service.save(any(ReservationDTO.class))).thenReturn(respuestaMock);

        // Act & Assert
        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peticion)))
                .andExpect(status().isCreated());
    }
}