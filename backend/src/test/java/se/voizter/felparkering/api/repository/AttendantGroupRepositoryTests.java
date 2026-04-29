package se.voizter.felparkering.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.testsupport.TestDataFactory;


@DataJpaTest
public class AttendantGroupRepositoryTests {
    @Autowired
    AttendantGroupRepository groupRepository;

    @Test
    void canSaveAndFindByName() {
        AttendantGroup attendantGroup = groupRepository.save(
            TestDataFactory.attendantGroup()
        );

        Optional<AttendantGroup> result = groupRepository.findByName(attendantGroup.getName());

        assertTrue(result.isPresent());
        assertEquals(attendantGroup.getName(), result.get().getName());
    }

    @Test
    void findByNameReturnsEmptyWhenNotFound() {
        Optional<AttendantGroup> result = groupRepository.findByName("missing-group");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByName() {
        AttendantGroup attendantGroup = groupRepository.save(
            TestDataFactory.attendantGroup()
        );

        assertTrue(groupRepository.existsByName(attendantGroup.getName()));

        assertFalse(groupRepository.existsByName("no-group"));
    }

    @Test
    void cannotSaveAttendantGroupWithDuplicateName() {
        groupRepository.save(
            TestDataFactory.attendantGroup()
        );

        AttendantGroup attendantGroup2 = TestDataFactory.attendantGroup();

        assertThrows(DataIntegrityViolationException.class, () -> {
            groupRepository.saveAndFlush(attendantGroup2);
        });
    }

    @Test
    void shouldThrowExceptionWhenMissingRequiredField() {
        AttendantGroup attendantGroup = new AttendantGroup();
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            groupRepository.saveAndFlush(attendantGroup);
        });
    }

}
