package se.voizter.felparkering.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import se.voizter.felparkering.api.dto.ApiResponse;
import se.voizter.felparkering.api.dto.AttendantGroupDetailDto;
import se.voizter.felparkering.api.dto.DeletedUserResponse;
import se.voizter.felparkering.api.dto.UserAdminDetailDto;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserAdminDetailDto>>> allUsers() {
        List<UserAdminDetailDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(
            new ApiResponse<>(
                users, 
                Message.ADMIN_USERS_FETCHED.toString()
            )
        );
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserAdminDetailDto>> createAttendant(@Valid @RequestBody User attendant) {
        UserAdminDetailDto user = adminService.createAttendant(attendant);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(
                    user, 
                    Message.USER_CREATED.toString()
                )
            );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<DeletedUserResponse>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(
            new ApiResponse<>(
                new DeletedUserResponse(id),
                Message.USER_DELETED.toString()
            )
        );
    }
    
    @GetMapping("/attendants")
    public ResponseEntity<ApiResponse<List<AttendantGroupDetailDto>>> allAttendantGroups() {
        List<AttendantGroupDetailDto> groups = adminService.getAllAttendantGroups();
        return ResponseEntity.ok(
            new ApiResponse<>(
                groups,
                Message.ADMIN_GROUPS_FETCHED.toString()
            )
        );
    }
}
