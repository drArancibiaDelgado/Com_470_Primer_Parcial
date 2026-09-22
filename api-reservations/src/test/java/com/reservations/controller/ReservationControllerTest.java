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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
        mockMvc.perform(get("/reservation/{id}", id).contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(peticion))).andExpect(status().isCreated());
    }

    @Test
    void testGetReservations_Retorna200Ok() throws Exception {
        // Prueba: Obtener la lista de todas las reservas
        mockMvc.perform(get("/reservation").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void testSave_PeticionSinCuerpo_Retorna400BadRequest() throws Exception {
        // Prueba: Enviar un POST completamente vacío para comprobar que el @Valid lo rechaza
        mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReservationById_IdInvalido_Retorna400BadRequest() throws Exception {
        // Prueba: Enviar letras en lugar de un ID numérico en la URL
        mockMvc.perform(get("/reservation/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSave_CuerpoVacio_Retorna400BadRequest() throws Exception {
        // Prueba: Enviar un JSON vacío "{}" que no cumple con el modelo
        mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSave_TipoContenidoIncorrecto_Retorna415UnsupportedMediaType() throws Exception {
        // Prueba: Intentar enviar texto plano en lugar de JSON
        mockMvc.perform(post("/reservation").contentType(MediaType.TEXT_PLAIN).content("esto no es un json"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testGet_RutaInexistente_Retorna404NotFound() throws Exception {
        // Prueba: Consultar un endpoint que no existe en el controlador
        mockMvc.perform(get("/reservation/endpoint/inventado").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMetodoNoPermitido_Retorna405MethodNotAllowed() throws Exception {
        // Prueba: Intentar hacer una petición PATCH a una ruta que solo acepta GET/POST
        mockMvc.perform(patch("/reservation").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testGetReservations_VerificaFormatoRespuesta() throws Exception {
        // Prueba: Asegurar que la respuesta de las reservas vuelva específicamente en formato JSON
        mockMvc.perform(get("/reservation").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdate_Retorna200Ok() throws Exception {
        // Prueba: Actualizar una reserva existente (PUT).
        // Usamos un JSON válido para pasar la validación @Valid del controlador.
        String jsonValido = "{\n"
                + "  \"passengers\": [{\"firstName\": \"Diego\", \"lastName\": \"Arancibia\", \"documentNumber\": \"87654321\", \"documentType\": \"CI\", \"birthday\": \"1998-05-15\"}],\n"
                + "  \"itinerary\": {\"segment\": [{\"origin\": \"LPB\", \"destination\": \"VVI\", \"departure\": \"2026-10-15\", \"arrival\": \"2026-10-16\", \"carrier\": \"BOA\"}]}\n"
                + "}";

        mockMvc.perform(put("/reservation/1").contentType(MediaType.APPLICATION_JSON).content(jsonValido))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_Retorna200Ok() throws Exception {
        // Prueba: Eliminar una reserva existente (DELETE)
        mockMvc.perform(delete("/reservation/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}