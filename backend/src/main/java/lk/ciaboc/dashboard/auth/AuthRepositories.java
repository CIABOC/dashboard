package lk.ciaboc.dashboard.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

interface DashboardUserRepository extends JpaRepository<DashboardUser, Long> {
    Optional<DashboardUser> findByExternalSubject(String externalSubject);
}

interface DashboardRoleRepository extends JpaRepository<DashboardRole, Long> {
    Optional<DashboardRole> findByCode(String code);
}

interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    java.util.List<UserRole> findByUser_IdAndActiveTrue(Long userId);
}

interface RolePinRepository extends JpaRepository<RolePin, Long> {
    Optional<RolePin> findByUserRole_Id(Long userRoleId);
}

interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
