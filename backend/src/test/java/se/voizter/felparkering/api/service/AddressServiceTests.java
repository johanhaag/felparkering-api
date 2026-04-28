package se.voizter.felparkering.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.voizter.felparkering.api.repository.AddressRepository;
import se.voizter.felparkering.api.security.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTests {
    @Mock
    AddressRepository addressRepository;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    AddressService addressService;

    @Test
    void returnsSuggestionsFromMatchingAddresses() {
        // TODO: Write test
    }

    @Test
    void returnsEmptyListWhenRepositoryReturnsEmptyList() {
        // TODO: Write test
    }
}
