package se.voizter.felparkering.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import se.voizter.felparkering.api.dto.AttendantGroupDetailDto;
import se.voizter.felparkering.api.dto.UserAdminDetailDto;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.AttendantGroupRepository;
import se.voizter.felparkering.api.repository.UserRepository;

//TODO: Not fully implemented yet, only skeleton code.

@Service
public class AdminService {
    
    private final UserRepository userRepository;
    private final AttendantGroupRepository groupRepository;

    public AdminService(UserRepository userRepository, AttendantGroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public List<UserAdminDetailDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(this::toUserDetailDto).toList();
    }

    @Transactional
    public UserAdminDetailDto createAttendant(User attendant) {
        userRepository.save(attendant);
        return toUserDetailDto(attendant);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public List<AttendantGroupDetailDto> getAllAttendantGroups() {
        List<AttendantGroup> allGroups = groupRepository.findAll();
        return allGroups.stream().map(this::toGroupDetailDto).toList();
    }

    private UserAdminDetailDto toUserDetailDto(User user) {
        return new UserAdminDetailDto(user.getId(), user.getRole());
    }

    private AttendantGroupDetailDto toGroupDetailDto(AttendantGroup group) {
        // Implement AttendantGroup repofunction that retrieves all users that is allocated to that AttendantGroup
        return new AttendantGroupDetailDto(group.getName(), null);
    }
}
