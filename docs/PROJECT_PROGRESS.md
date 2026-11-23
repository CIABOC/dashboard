# CIABOC Dashboard Project Progress

## Phase 02 - Role-Based Authentication and Authorization

Status: Implemented; external OIDC configuration and final policy approval remain pending.

### Completed

- Added PostgreSQL RBAC entities and Flyway migration:
  - organizational units
  - users
  - roles
  - permissions
  - user-role assignments
  - role permissions
  - role-specific PIN credentials
  - audit logs
- Added initial dashboard role seed records.
- Added unique BCrypt-hashed PIN provisioning and reset operations.
- Added PIN verification, failed-attempt lockout and verification rate limiting.
- Added PIN revoke and role-context revocation behavior.
- Added role selection and verified role context sessions.
- Added SSO/OIDC readiness and an existing SSO success handler.
- Added direct API protection requiring a verified role session.
- Added the supplied-style login page with CIABOC Account SSO entry and role/PIN steps.
- Added audit records for authentication, role selection, PIN verification and PIN administration.

### Database

Migration `V2__create_rbac_tables.sql` creates the Phase 02 RBAC and credential schema. PIN values are never stored; only BCrypt hashes are persisted.

### Authentication Flow

1. The user starts CIABOC Account OIDC authentication.
2. The backend validates the OIDC response.
3. The user's external subject is matched to an active PostgreSQL account.
4. Assigned active roles are retrieved.
5. The user selects an assigned role.
6. The backend verifies the assignment and the role-specific PIN.
7. A 30-minute selected-role session context is established.
8. Protected API requests require this verified role context.

### Automated Tests

- Backend context test passed.
- Unique role-PIN provisioning test passed.
- Unauthorized role-selection test passed.
- Incorrect PIN test passed.
- Total: 4 tests passed, 0 failed.
- Next.js production build passed.

### Pending External Configuration

- Approve and configure CIABOC OIDC issuer, client ID and client secret.
- Provide approved role-to-permission mappings.
- Approve organizational-unit and sensitive-data policies.
- Provision initial users and user-role assignments.
- Provision approved role-specific PINs through the authorized administrator process.
- Configure production session cookie security and reverse-proxy settings.

### Not Implemented as Final Policy

No final role-to-module access matrix was invented. Permission mappings remain configurable in PostgreSQL.
