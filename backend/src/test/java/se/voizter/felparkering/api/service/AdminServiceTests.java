package se.voizter.felparkering.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.voizter.felparkering.api.repository.AttendantGroupRepository;
import se.voizter.felparkering.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTests {

    @Mock
    UserRepository userRepository;

    @Mock
    AttendantGroupRepository groupRepository;

    @InjectMocks
    AdminService adminService;

    @Test
    void getAllUsersReturnsUserDTOsForAllUsers() {
        // TODO: Write test
    }

    @Test
    void createAttendantSavesUser() {
        // TODO: Write test
    }

    @Test
    void createAttendantReturnsDTOWithIdAndRole() {
        // TODO: Write test
    }

    @Test
    void deleteUserDeletesById() {
        // TODO: Write test
    }

    @Test
    void getAllAttendantGroupsReturnsGroupDTOs() {
        // TODO: Write test
    }
}
