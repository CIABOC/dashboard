# Initial Project Assessment

## Executive summary

The available project has a valid Next.js 16/React 19/TypeScript/Tailwind scaffold and the management dashboard prototype copied into `public/prototype.html`. The prototype is currently served through an iframe in `app/page.tsx`. It is visual reference implementation, not production architecture.

No Spring Boot application, PostgreSQL schema, Flyway migrations, authentication integration or protected API exists in the repository. The Concept Note and SAD require those to be discovery/approval-gated, so they must not be assumed from the prototype.

## Repository assessment

| Area | Finding |
|---|---|
| Project structure | New Next.js App Router project; `app/layout.tsx`, `app/page.tsx`, `app/globals.css`, `public/` |
| Framework | Next.js 16.4.6, React 19.3.0, TypeScript 5, Tailwind 4.3.3 |
| Existing implementation | Prototype mounted in iframe; no reusable React dashboard components |
| Database | None in repository |
| Backend | None in repository |
| Authentication | None; prototype has a visual demo login only |
| Tests | No application test suite or test script |
| Build | Production build passed during discovery |
| Git | Repository initialized; current implementation changes are not a formal architecture baseline |
| Assets | Prototype uses remote Google Fonts and inline SVG/CSS; RVM assets are external and not copied |

The RVM/VMM, HRMS and Portal HTML files are in `Downloads`, not in the repository. The dashboard prototype is the only uploaded wireframe currently incorporated into the project.

## Requirements status

### Confirmed from reference documents

- Separate dashboard application and operational source systems.
- Aggregate/de-identified information by default.
- OIDC/CIABOC Account readiness, not live identity assumption.
- Role/division/purpose/classification authorization.
- KPI governance with owner, formula, grain, source, version and freshness.
- Central reporting store for approved observations and snapshots.
- Source-system autonomy, read-only integration and stale-data behavior.
- Modular Spring Boot backend and PostgreSQL proposed baseline.
- Trilingual executive experience and accessibility requirements.
- Management modules, at least as representative proposed modules.

### Not approved or not provided

- Live CIABOC Account/Keycloak deployment.
- Source-system interfaces and signed data-sharing agreements.
- LKEH deployment/crosswalk.
- KPI definitions and numeric targets.
- Hosting, network zones, RPO/RTO and SLA values.
- Production data, source credentials or database schema.
- Management decision to implement RVM/VMM/HRMS/Portal in the dashboard.
- Final source ownership and data classification matrix.
- Any production status for the systems shown in prototype integration cards.

## Missing screens and navigation

The management prototype has one login and 15 navigation destinations. It does not define:

- Dedicated KPI catalogue/semantic governance administration.
- Signed export request/approval workflow.
- Data-quality quarantine/reconciliation review.
- Source onboarding/interface contract screen.
- Identity/session management and access-review screen beyond the user/roles prototype.
- Privacy/small-group suppression review.
- Operational incident runbook screens.
- Formal source outage/degraded-mode screen beyond integration health.
- A production login error, account-disabled or SSO-unavailable state.
- Empty, partial, stale and unauthorized states in reusable components.

The RVM/VMM bundle has more detailed operational screens, but it is not the central dashboard wireframe and would require a separate integration scope decision.

## Missing assets

- Official CIABOC logo/crest for the dashboard. The prototype uses a lettermark placeholder.
- Dashboard-native assets: only inline icons and remote Google fonts.
- RVM standalone assets are not in the repository.
- RVM 15 JPG previews, CSS and local font files exist only in `Downloads`.
- Portal source assets are bundled in the minified HTML.
- Concept/SAD PDFs are external references and should not be treated as repository deliverables.

## Interactions requiring backend approval or clarification

- All role switching in the prototype is illustrative and must be replaced by server-validated claims.
- “Sync now” and “Retry sync” must not perform real writes until source contracts are approved.
- Alert acknowledgement, assignment, escalation and closure require workflow authorization and audit.
- Export must enforce aggregate-only policy, limits and audit before any production use.
- VMR detail/modal/exception actions contain sensitive identity/operational concepts and need explicit purpose and source-system authorization.
- HR wireframe transaction capabilities must remain in HRMS or a source-specific system unless explicitly approved for the central dashboard.
- RVM status transitions, audit logs and custody operations are operational workflows, not management KPI-only features.
- The trilingual prototype requires a formal translation/terminology review and persistent language resources.

## Architectural assessment

The target architecture should remain:

1. Next.js role-aware web application.
2. Spring Boot modular API (UI/API, authorization, KPI/semantic, ingestion and reporting modules).
3. PostgreSQL reporting store with Flyway and versioned KPI facts.
4. CIABOC Account OIDC integration once approved and deployed.
5. Source connectors/pollers using read-only aggregate contracts.
6. Stale snapshot, quarantine and refresh-run observability.
7. Audit and monitoring across authentication, API, export and ingestion events.

Do not introduce microservices, Kafka, Kubernetes or a BI embedding layer until measured requirements justify them.

## Immediate discovery gaps

1. Confirm the authoritative ICT roadmap period.
2. Appoint a product owner, KPI owners and data stewards.
3. Define the MVP scope and 2–3 low-risk sources.
4. Obtain source-interface, owner and entitlement information.
5. Decide whether RVM/VMM and HRMS wireframes are source-system references or in-dashboard operational integrations.
6. Confirm identity readiness and role/division mappings.
7. Obtain official assets and multilingual review.
8. Approve hosting, retention, RPO/RTO and privacy classifications.

## Phase 00 conclusion

The project is ready for a requirements and architecture baseline, but not for production implementation. The only implementation present is a prototype shell and duplicated reference HTML. All sensitive, operational and external-source behavior must remain behind approved discovery and policy gates.

No Phase 01 work has started.
