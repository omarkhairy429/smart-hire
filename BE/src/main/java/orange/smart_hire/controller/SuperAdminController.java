package orange.smart_hire.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.model.User;
import orange.smart_hire.service.SuperAdminService;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

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
}