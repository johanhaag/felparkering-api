package se.voizter.felparkering.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.adminUserWithId;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.attendantGroup;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.attendantUserWithId;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.voizter.felparkering.api.dto.AttendantGroupDetailDto;
import se.voizter.felparkering.api.dto.UserAdminDetailDto;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.model.User;
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
        when(userRepository.findAll())
            .thenReturn(List.of(adminUserWithId(1L), attendantUserWithId(2L)));

        List<UserAdminDetailDto> result = adminService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(new UserAdminDetailDto(1L, Role.ADMIN), result.get(0));
        assertEquals(new UserAdminDetailDto(2L, Role.ATTENDANT), result.get(1));
    }

    @Test
    void createAttendantSavesUser() {
        User attendant = attendantUserWithId(3L);

        adminService.createAttendant(attendant);

        verify(userRepository).save(attendant);
    }

    @Test
    void createAttendantReturnsDTOWithIdAndRole() {
        User attendant = attendantUserWithId(3L);

        UserAdminDetailDto result = adminService.createAttendant(attendant);

        assertEquals(3L, result.id());
        assertEquals(Role.ATTENDANT, result.role());
    }

    @Test
    void deleteUserDeletesById() {
        adminService.deleteUser(9L);

        verify(userRepository).deleteById(9L);
    }

    @Test
    void getAllAttendantGroupsReturnsGroupDTOs() {
        when(groupRepository.findAll())
            .thenReturn(List.of(attendantGroup(1L, "Goteborg"), attendantGroup(2L, "Stockholm")));

        List<AttendantGroupDetailDto> result = adminService.getAllAttendantGroups();

        assertEquals(2, result.size());
        assertEquals("Goteborg", result.get(0).name());
        assertNull(result.get(0).attendants());
        assertEquals("Stockholm", result.get(1).name());
        assertNull(result.get(1).attendants());
    }
}
