package se.voizter.felparkering.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AddressRepositoryTests {
    @Autowired
    AddressRepository addressRepository;

    @Test
    void canSaveAndFindById() {
        // TODO: Write test
    }

    @Test
    void searchByStreetPartialMatch() {
        // TODO: Write test
    }

    @Test
    void searchByStreetCaseInsensitive() {
        // TODO: Write test
    }

    @Test
    void searchByStreetEmptyListWhenNoMatch() {
        // TODO: Write test
    }

    @Test
    void existsByStreetIgnoreCase() {
        // TODO: Write test
    }

}