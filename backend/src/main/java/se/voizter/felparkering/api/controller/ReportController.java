package se.voizter.felparkering.api.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import se.voizter.felparkering.api.dto.PagedReportResponse;
import se.voizter.felparkering.api.dto.ReportCreatedResponse;
import se.voizter.felparkering.api.dto.ReportDetailDto;
import se.voizter.felparkering.api.dto.ReportRequest;
import se.voizter.felparkering.api.dto.ReportUpdatedResponse;
import se.voizter.felparkering.api.dto.UpdateStatusRequest;
import se.voizter.felparkering.api.dto.UserRequest;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.service.ReportService;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.enums.Status;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        Long id;

        if (principal instanceof Long) {
            id = (Long) principal;
        } else if (principal instanceof String) {
            id = Long.parseLong((String) principal);
        } else {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    @GetMapping
    public ResponseEntity<?> all(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdOn") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Status status,
        @RequestParam(required = false) UserRequest assignedToId
        ) {
        Page<ReportDetailDto> reports = reportService.getAll(page, size, sortBy, sortDir, search, currentUser(), status, assignedToId);
        
        return ResponseEntity.ok(
            new PagedReportResponse(
                reports.getContent(), 
                reports.getNumber(), 
                reports.getSize(), 
                reports.getTotalElements(), 
                reports.getTotalPages()
            )
        );
    }

    @PostMapping
    public ResponseEntity<?> createReport(@Valid @RequestBody ReportRequest request) {
        ReportDetailDto report = reportService.create(currentUser(), request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ReportCreatedResponse(Message.REPORT_CREATED_SUCCESSFULLY.toString(), report));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        ReportDetailDto report = reportService.get(currentUser(), id);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusRequest request, @PathVariable Long id) {
        ReportDetailDto report = reportService.update(currentUser(), request.status(), id);
        return ResponseEntity.ok(new ReportUpdatedResponse(Message.REPORT_UPDATED_SUCCESSFULLY.toString(), report));
    }
}
