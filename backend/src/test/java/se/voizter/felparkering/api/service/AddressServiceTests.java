package se.voizter.felparkering.api.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

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
        // TODO: Write test
    }

    @Test
    void returnsEmptyListWhenRepositoryReturnsEmptyList() {
        // TODO: Write test
    }
}
