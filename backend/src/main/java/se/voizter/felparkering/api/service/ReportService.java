package se.voizter.felparkering.api.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import se.voizter.felparkering.api.dto.ReportDetailDto;
import se.voizter.felparkering.api.dto.ReportRequest;
import se.voizter.felparkering.api.dto.UserRequest;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.enums.Status;
import se.voizter.felparkering.api.exception.exceptions.AlreadyAssignedException;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.Report;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.AddressRepository;
import se.voizter.felparkering.api.repository.AttendantGroupRepository;
import se.voizter.felparkering.api.repository.ReportRepository;
import se.voizter.felparkering.api.repository.UserRepository;

@Service
public class ReportService {
    
    private final AddressRepository addressRepository;
    private final ReportRepository reportRepository;
    private final AttendantGroupRepository groupRepository;
    private final UserRepository userRepository;

    public ReportService(AddressRepository addressRepository, ReportRepository reportRepository, AttendantGroupRepository groupRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.reportRepository = reportRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Page<ReportDetailDto> getAll(
        int page, 
        int size, 
        String sortBy, 
        String sortDir, 
        String search, 
        User user, 
        @Nullable Status status, 
        @Nullable UserRequest assignedTo
    ) {
        Role role = user.getRole();

        User attendant = assignedTo != null ? userRepository.findById(assignedTo.id())
            .orElseThrow(() -> new NotFoundException(Message.USER_NOT_FOUND.toString())) : null;

        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Report> reports;

        switch (role) {
            case ADMIN -> {
                reports = reportRepository.findByFilters(status, search, pageable);
            }
            case ATTENDANT -> {
                reports = reportRepository.findByFiltersInGroup(status, attendant, user.getAttendantGroup(), search, pageable);
            }
            case CUSTOMER -> {
                reports = reportRepository.findByFiltersCreatedBy(status, user, search, pageable);
            }
            default -> throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
        }
        
        return reports.map(this::toDetailDto);
    }

    @Transactional
    public ReportDetailDto create(User user, ReportRequest request) {
        if (user.getRole() == Role.ATTENDANT) {
            throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
        }

        Address address = resolveAddress(request);
        Report report = new Report();
        report.setAddress(address);
        report.setLicensePlate(request.licensePlate().toUpperCase());
        report.setCategory(request.category());
        report.setCreatedBy(user);
        report.setStatus(Status.NEW);

        AttendantGroup attendantGroup = groupRepository.findByName(address.getCity())
            .orElseThrow(() -> new NotFoundException(Message.ATTENDANT_GROUP_NOT_FOUND.toString()));

        report.setAttendantGroup(attendantGroup);

        reportRepository.save(report);

        return toDetailDto(report);
    }

    @Transactional
    public ReportDetailDto get(User user, Long id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Message.REPORT_NOT_FOUND.toString()));
        
        if (canAccess(user, report)) {
            return toDetailDto(report); 
        } else {
            throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
        }
    }

    @Transactional
    public ReportDetailDto update(User user, Status status, Long id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Message.REPORT_NOT_FOUND.toString()));

        if (!canAccess(user, report)) {
            throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
        }

        Role role = user.getRole();

        if (role == Role.CUSTOMER) {
            if (status == Status.CANCELLED && (report.getStatus() == Status.NEW || report.getStatus() == Status.ASSIGNED)) {
                report.setStatus(Status.CANCELLED);
            } else {
                throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
            }
            return toDetailDto(report);
        }

        if (role == Role.ADMIN) {
            report.setStatus(status);
            return toDetailDto(report);
        }

        //if (role == Role.ATTENDANT)
        Long me = user.getId();
        Long assigneeId = report.getAssignedTo() != null ? report.getAssignedTo().getId() : null;
        boolean assignedToMe = assigneeId != null && assigneeId.equals(me);
        boolean assignedToOther = assigneeId != null && !assigneeId.equals(me);

        if (assignedToOther) {
            throw new AlreadyAssignedException(Message.REPORT_ALREADY_ASSIGNED.toString());
        }

        switch (status) {
            case ASSIGNED -> {
                if (report.getStatus() != Status.NEW || assigneeId != null) {
                    throw new AlreadyAssignedException(Message.REPORT_ALREADY_ASSIGNED.toString());
                }
                report.setStatus(Status.ASSIGNED);
                report.setAssignedTo(user);
            }
            case NEW -> {
                if (!assignedToMe || report.getStatus() != Status.ASSIGNED) {
                    throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
                }
                report.setStatus(Status.NEW);
                report.setAssignedTo(null);
            }
            case RESOLVED -> {
                if (!assignedToMe) {
                    throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
                }
                report.setStatus(Status.RESOLVED);
            }
            default -> throw new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString());
        }
        return toDetailDto(report);
    }

    private ReportDetailDto toDetailDto(Report report) {
        Long assigneeId = report.getAssignedTo() != null ? report.getAssignedTo().getId() : null;
        return new ReportDetailDto(
            report.getId(),
            report.getAddress(),
            report.getLicensePlate(),
            report.getCategory(),
            report.getAttendantGroup(),
            assigneeId,
            report.getCreatedOn(),
            report.getUpdatedOn(),
            report.getStatus()
        );
    }

    private Address resolveAddress(ReportRequest request) {
        Address address = addressRepository.findById(request.id())
            .orElseThrow(() -> new NotFoundException(Message.ADDRESS_NOT_FOUND.toString()));;

        if (request.houseNumber() != null && !request.houseNumber().isBlank()) {
            address.setHouseNumbers(List.of(request.houseNumber()));
        } else {
            address.setHouseNumbers(List.of());
        }

        return address;
    }

    private boolean canAccess(User user, Report report) {
        return switch (user.getRole()) {
            case ADMIN -> true;
            case ATTENDANT -> {
                AttendantGroup ug = user.getAttendantGroup();
                AttendantGroup rg = report.getAttendantGroup();
                yield ug != null && rg != null && Objects.equals(ug.getId(), rg.getId());
            }
            case CUSTOMER -> {
                User creator = report.getCreatedBy();
                yield creator != null && Objects.equals(creator.getId(), user.getId());
            }
            default -> false;
        };
    }
}
