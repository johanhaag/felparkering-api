package se.voizter.felparkering.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AddressService;

@WebMvcTest(AddressController.class)
public class AddressControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AddressService addressService;

    @MockitoBean JwtProvider jwtProvider;
    
    @Test
    void searchReturnsAddressSuggestions() {
        // TODO: Write test
    }

    @Test
    void searchPassesQueryToService() {
        // TODO: Write test
    }

    @Test
    void searchReturnsUnauthorizedWithoutAuthentication() {
        // TODO: Write test
    }

    @Test
    void routeReturnsRouteResponseForAttendant() {
        // TODO: Write test
    }

    @Test
    void routeReturnsForbiddenForCustomer() {
        // TODO: Write test
    }

    @Test
    void routeReturnsUnauthorizedWithoutAuthentication() {
        // TODO: Write test
    }
}
