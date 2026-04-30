package se.voizter.felparkering.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.address;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import se.voizter.felparkering.api.dto.AddressSuggestionDto;
import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.repository.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTests {
    @Mock
    AddressRepository addressRepository;

    @Mock
    RestClient.Builder restClientBuilder;

    @Mock
    RestClient restClient;

    AddressService addressService;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.baseUrl("http://test-api"))
            .thenReturn(restClientBuilder);
        when(restClientBuilder.build())
            .thenReturn(restClient);

        addressService = new AddressService(
            restClientBuilder,
            "http://test-api",
            "test-api-key",
            addressRepository
        );
    }

    @Test
    void returnsSuggestionsFromMatchingAddresses() {
        Address address = address(1L, "Kungsgatan", List.of("1", "3"), "Goteborg");

        when(addressRepository.searchByStreet("kung"))
            .thenReturn(List.of(address));

        List<AddressSuggestionDto> result = addressService.getAddresses("kung");

        assertEquals(2, result.size());
        assertEquals(new AddressSuggestionDto(1L, "Kungsgatan", "Goteborg", "1"), result.get(0));
        assertEquals(new AddressSuggestionDto(1L, "Kungsgatan", "Goteborg", "3"), result.get(1));
    }

    @Test
    void returnsEmptyListWhenRepositoryReturnsEmptyList() {
        when(addressRepository.searchByStreet("missing"))
            .thenReturn(List.of());

        List<AddressSuggestionDto> result = addressService.getAddresses("missing");

        assertTrue(result.isEmpty());
    }
}
