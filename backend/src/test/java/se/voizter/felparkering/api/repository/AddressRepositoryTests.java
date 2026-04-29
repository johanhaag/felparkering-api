package se.voizter.felparkering.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.testsupport.TestDataFactory;

@DataJpaTest
public class AddressRepositoryTests {
    @Autowired
    AddressRepository addressRepository;

    @Test
    void canSaveAndFindById() {
        Address address = TestDataFactory.address();

        Address saved = addressRepository.save(address);
        Optional<Address> result = addressRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
        assertEquals(saved.getStreet(), result.get().getStreet());
        assertEquals(saved.getCity(), result.get().getCity());
        assertEquals(saved.getHouseNumbers(), result.get().getHouseNumbers());
    }

    @Test
    void searchByStreetPartialMatch() {
        Address matchingAddress = TestDataFactory.address("Testgatan", "2", "Teststad");
        Address otherAddress = TestDataFactory.address("Annan gata", "3", "Teststad");

        addressRepository.save(matchingAddress);
        addressRepository.save(otherAddress);

        List<Address> result = addressRepository.searchByStreet("Test");

        assertEquals(1, result.size());
        assertEquals("Testgatan", result.get(0).getStreet());
    }

    @Test
    void searchByStreetCaseInsensitive() {
        Address address = TestDataFactory.address("Testgatan", "2", "Teststad");
        addressRepository.save(address);

        List<Address> result = addressRepository.searchByStreet("testGATAN");

        assertEquals(1, result.size());
        assertEquals("Testgatan", result.get(0).getStreet());
    }

    @Test
    void searchByStreetEmptyListWhenNoMatch() {
        Address address = TestDataFactory.address("Testgatan", "2", "Teststad");
        addressRepository.save(address);

        List<Address> result = addressRepository.searchByStreet("Missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByStreetIgnoreCase() {
        Address address = TestDataFactory.address("Testgatan", "2", "Teststad");
        addressRepository.save(address);

        assertTrue(addressRepository.existsByStreetIgnoreCase("testgatan"));
        assertFalse(addressRepository.existsByStreetIgnoreCase("missing-gata"));
    }

}