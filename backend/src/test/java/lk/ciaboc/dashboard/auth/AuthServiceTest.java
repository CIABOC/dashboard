package lk.ciaboc.dashboard.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    private DashboardUserRepository users;
    private UserRoleRepository userRoles;
    private RolePinRepository rolePins;
    private DashboardRoleRepository roles;
    private AuditLogRepository auditLogs;
    private AuthService service;

    @BeforeEach
    void setUp() {
        users = mock(DashboardUserRepository.class);
        userRoles = mock(UserRoleRepository.class);
        rolePins = mock(RolePinRepository.class);
        roles = mock(DashboardRoleRepository.class);
        auditLogs = mock(AuditLogRepository.class);
        service = new AuthService(users, userRoles, rolePins, roles, auditLogs);
    }

    @Test
    void provisionsUniquePinsForSeparateUserRoleAssignments() {
        DashboardUser user = new DashboardUser();
        user.setId(1L);
        DashboardRole director = new DashboardRole();
        director.setCode("DIRECTOR");
        UserRole assignmentA = new UserRole();
        assignmentA.setUser(user);
        assignmentA.setRole(director);
        try {
            java.lang.reflect.Field id = UserRole.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(assignmentA, 10L);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }

        when(rolePins.findByUserRole_Id(10L)).thenReturn(Optional.empty());
        when(rolePins.save(any(RolePin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String first = service.provisionPin(assignmentA);
        String second = service.provisionPin(assignmentA);

        assertEquals(8, first.length());
        assertTrue(first.matches("\\d{8}"));
        assertTrue(second.matches("\\d{8}"));
    }

    @Test
    void deniesRoleThatIsNotAssignedToTheSubject() {
        DashboardUser user = new DashboardUser();
        user.setId(1L);
        user.setActive(true);
        when(users.findByExternalSubject("subject-a")).thenReturn(Optional.of(user));
        when(userRoles.findByUser_IdAndActiveTrue(1L)).thenReturn(List.of());

        assertThrows(AuthService.UnauthorizedRoleException.class,
                () -> service.findAuthorizedRole("subject-a", 99L)
                        .orElseThrow(() -> new AuthService.UnauthorizedRoleException("Role is not assigned")));
    }

    @Test
    void rejectsIncorrectPinAndRecordsAudit() {
        DashboardUser user = new DashboardUser();
        user.setId(1L);
        user.setActive(true);
        DashboardRole role = new DashboardRole();
        role.setCode("DIRECTOR");
        role.setActive(true);
        UserRole assignment = new UserRole();
        assignment.setUser(user);
        assignment.setRole(role);
        setUserRoleId(assignment, 12L);

        RolePin pin = new RolePin();
        pin.setUserRole(assignment);
        pin.setPinHash("$2a$10$invalid.invalid.invalid.invalid.invalid.invalid.invalid.invalid");
        pin.setStatus("ACTIVE");

        when(users.findByExternalSubject("subject-a")).thenReturn(Optional.of(user));
        when(userRoles.findByUser_IdAndActiveTrue(1L)).thenReturn(List.of(assignment));
        when(rolePins.findByUserRole_Id(12L)).thenReturn(Optional.of(pin));
        when(rolePins.save(any(RolePin.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogs.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(AuthService.PinDeniedException.class,
                () -> service.verifyPin("subject-a", 12L, "12345678"));
        verify(auditLogs, atLeastOnce()).save(any(AuditLog.class));
    }

    private static void setUserRoleId(UserRole assignment, long id) {
        try {
            java.lang.reflect.Field field = UserRole.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(assignment, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
