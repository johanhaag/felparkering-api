package se.voizter.felparkering.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.testsupport.TestDataFactory;
import se.voizter.felparkering.api.enums.Role;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AttendantGroupRepository groupRepository;

    @Test
    void canSaveAndFindByEmail() {
        User user = TestDataFactory.adminUser("test-user@example.com", "123abc");
        user.setAttendantGroup(null);
        
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail(user.getEmail());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals("test-user@example.com", result.get().getEmail());
        assertEquals("123abc", result.get().getPassword());
        assertEquals(Role.ADMIN, result.get().getRole());
        assertNull(result.get().getAttendantGroup());
    }

    @Test
    void findByEmailReturnsEmptyWhenNotFound() {
        Optional<User> result = userRepository.findByEmail("test-user@example.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail() {
        // TODO: Write test
    }

    @Test
    void canSaveMultipleUsersWithDifferentRoles() {
        User admin = TestDataFactory.adminUser("adminr@example.com", "admin123");
        userRepository.save(admin);

        User attendant = TestDataFactory.attendantUser("attendant@example.com", "attendant123");
        userRepository.save(attendant);

        User customer = TestDataFactory.customerUser("customer@example.com", "customer123");
        userRepository.save(customer);

        assertTrue(userRepository.findByEmail("adminr@example.com").isPresent());
        assertTrue(userRepository.findByEmail("attendant@example.com").isPresent());
        assertTrue(userRepository.findByEmail("customer@example.com").isPresent());
    }

    @Test
    void cannotSaveUserWithDuplicateEmail() {
        User user1 = TestDataFactory.customerUser();
        userRepository.save(user1);

        User user2 = TestDataFactory.customerUser();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    void shouldThrowExceptionWhenMissingRequiredField() {
        User user1 = new User();
        user1.setEmail("1@example.com");
        user1.setRole(Role.CUSTOMER);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

        User user2 = new User();
        user2.setPassword("123");
        user2.setRole(Role.CUSTOMER);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });

        User user3 = new User();
        user3.setEmail("1@example.com");
        user3.setPassword("123");
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user3);
        });
    }

    @Test 
    void canSaveUserWithAttendantGroup() {
        AttendantGroup group = TestDataFactory.attendantGroup("Testgruppen");
        AttendantGroup savedGroup = groupRepository.save(group);

        User user = TestDataFactory.attendantUser("assigned@example.com", "secure");
        user.setAttendantGroup(savedGroup);
        User savedUser = userRepository.save(user);

        assertEquals("Testgruppen", savedUser.getAttendantGroup().getName());
    }
}


