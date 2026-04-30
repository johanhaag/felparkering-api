package se.voizter.felparkering.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import se.voizter.felparkering.api.dto.AttendantGroupDetailDto;
import se.voizter.felparkering.api.dto.UserAdminDetailDto;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AdminService;
import se.voizter.felparkering.api.testsupport.TestDataFactory;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AdminService adminService;
    @MockitoBean JwtProvider jwtProvider;

    @Test
    void allUsersReturnsUsersForAdmin() throws Exception {
        when(adminService.getAllUsers())
            .thenReturn(List.of(new UserAdminDetailDto(1L, Role.ADMIN)));

        mockMvc.perform(get("/admin/users")
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users[0].id").value(1))
            .andExpect(jsonPath("$.users[0].role").value("ADMIN"));
    }

    @Test
    void allUsersReturnsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/admin/users")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void allUsersReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createAttendantReturnsOkForAdmin() throws Exception {
        when(adminService.createAttendant(any(User.class)))
            .thenReturn(new UserAdminDetailDto(7L, Role.ATTENDANT));

        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.attendantUser()))
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user").value("User with id: 7 was created."));
    }

    @Test
    void createAttendantCallsService() throws Exception {
        when(adminService.createAttendant(any(User.class)))
            .thenReturn(new UserAdminDetailDto(7L, Role.ATTENDANT));

        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.attendantUser()))
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk());

        verify(adminService).createAttendant(any(User.class));
    }

    @Test
    void deleteUserReturnsOkForAdmin() throws Exception {
        mockMvc.perform(delete("/admin/users/7")
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User with id: 7 was deleted."));
    }

    @Test
    void deleteUserCallsServiceWithPathId() throws Exception {
        mockMvc.perform(delete("/admin/users/7")
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk());

        verify(adminService).deleteUser(7L);
    }

    @Test
    void allAttendantGroupsReturnsGroupsForAdmin() throws Exception {
        when(adminService.getAllAttendantGroups())
            .thenReturn(List.of(new AttendantGroupDetailDto("Testgruppen", List.of())));

        mockMvc.perform(get("/admin/attendants")
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attendantGroups[0].name").value("Testgruppen"));
    }

    private static UsernamePasswordAuthenticationToken auth(Long id, String role) {
        return new UsernamePasswordAuthenticationToken(
            id,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );
    }
}
