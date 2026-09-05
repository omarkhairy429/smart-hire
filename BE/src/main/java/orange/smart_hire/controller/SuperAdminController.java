package orange.smart_hire.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.AuditLogResponse;
import orange.smart_hire.dto.PlatformStatsResponse;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.model.User;
import orange.smart_hire.service.AuditLogService;
import orange.smart_hire.service.SuperAdminService;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final AuditLogService auditLogService;

    @PostMapping("/staff")
    public ResponseEntity<StaffResponse> createStaffMember(@Valid @RequestBody RegisterRequest request) {
        User createdUser = superAdminService.createStaffMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(StaffResponse.fromEntity(createdUser));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        List<StaffResponse> staff = superAdminService.getAllStaff();
        return ResponseEntity.ok(staff);
    }

    @PatchMapping("/staff/{id}/deactivate")
    public StaffResponse deactivateStaff(@PathVariable UUID id) {
        return superAdminService.deactivateStaff(id);
    }

    @PatchMapping("/staff/{id}/reactivate")
    public StaffResponse reactivateStaff(@PathVariable UUID id) {
        return superAdminService.reactivateStaff(id);
    }

    @GetMapping("/audit-logs")
    public Page<AuditLogResponse> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return auditLogService.findAll(action, PageRequest.of(page, size));
    }
    @GetMapping("/stats")
    public PlatformStatsResponse getPlatformStats() {
        return superAdminService.getPlatformStats();
    }
}