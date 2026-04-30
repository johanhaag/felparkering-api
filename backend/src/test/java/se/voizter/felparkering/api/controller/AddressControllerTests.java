package se.voizter.felparkering.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.configuration.SecurityConfig;
import se.voizter.felparkering.api.dto.AddressSuggestionDto;
import se.voizter.felparkering.api.dto.RouteRequest;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AddressService;
import se.voizter.felparkering.api.testsupport.OpenApiValidation;
import se.voizter.felparkering.api.testsupport.TestDataFactory;

@WebMvcTest(AddressController.class)
@Import(SecurityConfig.class)
public class AddressControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AddressService addressService;
    @MockitoBean JwtProvider jwtProvider;

    @Test
    void searchReturnsAddressSuggestions() throws Exception {
        when(addressService.getAddresses("test"))
            .thenReturn(List.of(new AddressSuggestionDto(1L, "Testgatan", "Teststad", "2")));

        mockMvc.perform(get("/addresses/search")
                .param("query", "test")
                .header("Authorization", "Bearer test-token")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].street").value("Testgatan"))
            .andExpect(jsonPath("$.data[0].city").value("Teststad"))
            .andExpect(jsonPath("$.data[0].houseNumber").value("2"))
            .andExpect(jsonPath("$.message").value(Message.ADDRESSES_FETCHED.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void searchPassesQueryToService() throws Exception {
        when(addressService.getAddresses("gatan")).thenReturn(List.of());

        mockMvc.perform(get("/addresses/search")
                .param("query", "gatan")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isOk());

        verify(addressService).getAddresses("gatan");
    }

    @Test
    void searchReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/addresses/search")
                .param("query", "test"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void routeReturnsRouteResponseForAttendant() throws Exception {
        RouteRequest request = TestDataFactory.routeRequest();
        Map<?, ?> routeResponse = Map.of("type", "FeatureCollection");

        doReturn(routeResponse)
            .when(addressService)
            .getRoute(any(double[].class), any(double[].class));

        mockMvc.perform(post("/addresses/route")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token")
                .content(objectMapper.writeValueAsString(request))
            .with(authentication(auth(1L, "ROLE_ATTENDANT"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.type").value("FeatureCollection"))
            .andExpect(jsonPath("$.message").value(Message.ROUTE_FETCHED.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());

        verify(addressService).getRoute(
            argThat(start -> Arrays.equals(start, request.start())),
            argThat(end -> Arrays.equals(end, request.end()))
        );
    }

    @Test
    void routeReturnsForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/addresses/route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.routeRequest()))
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void routeReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/addresses/route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.routeRequest())))
            .andExpect(status().isUnauthorized());
    }

    private static UsernamePasswordAuthenticationToken auth(Long id, String role) {
        return new UsernamePasswordAuthenticationToken(
            id,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );
    }
}
