package se.voizter.felparkering.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import se.voizter.felparkering.api.dto.AttendantGroupDetailDto;
import se.voizter.felparkering.api.dto.UserAdminDetailDto;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.AttendantGroupRepository;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> allUsers() {
        List<UserAdminDetailDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createAttendant(@Valid @RequestBody User attendant) {
        UserAdminDetailDto user = adminService.createAttendant(attendant);
        return ResponseEntity.ok(Map.of("user", "User with id: " + user.id() + " was created."));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User with id: " + id + " was deleted."));
    }
    
    @GetMapping("/attendants")
    public ResponseEntity<?> allAttendantGroups() {
        List<AttendantGroupDetailDto> groups = adminService.getAllAttendantGroups();
        return ResponseEntity.ok(Map.of("attendantGroups", groups));
    }
}
