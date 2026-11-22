# Screen Functionality Mapping

## Legend

- **Static**: sample or hard-coded display only.
- **Local interactive**: changes browser memory/client state only.
- **Backend required**: needs authenticated REST APIs, reporting data or source integration.

## Management dashboard

| Screen/component | Current state | Expected API / operation |
|---|---|---|
| Login / SSO | Static local navigation | OIDC authorization-code + PKCE start, callback, session, logout, MFA policy |
| Sidebar / role-aware navigation | Local role switch | `GET /me`, permissions and module authorization |
| Header period/compare/language | Local state | KPI period, language resource and reporting snapshot endpoints |
| Global search | Static input | Search index/allowed indicator/report metadata; must not search confidential records |
| Notifications | Seed data | `GET /alerts?state=new`, read/acknowledge and deep-link operations |
| Profile/role switch | Demo-only | CIABOC Account claims, deprovisioning and dashboard role assignment |
| Executive KPIs and charts | Static seed calculations | `GET /executive/overview`, KPI semantic/version/freshness metadata |
| Executive management attention | Static links | Alert/risk queries and drill-through to approved module |
| Complaint filter/trends/status/stages | Local division multiplier | `GET /complaints/summary`, stage/age/time-series and row-suppression rules |
| e-ALD progress/category/agency | Static | `GET /eald/submissions` aggregate feed only; no declaration content |
| HR workforce/vacancies/leave/training | Static | `GET /hr/summary` approved aggregate service only |
| Finance budget/expenditure/variance | Static; source flagged delayed | `GET /finance/summary`, accounting-period reconciliation and freshness |
| Asset category/verification/stock | Static | `GET /assets/summary` and exception query |
| Planning targets/milestones | Static | `GET /planning/targets`, milestone status and evidence references |
| Digital traffic/adoption/channel | Static | `GET /digital/services/summary`, approved usage and availability telemetry |
| ICT incidents/uptime/recovery | Static | `GET /ict/health`, incident and backup service from monitoring sources |
| LKEH classification/quality/mapping | Static | `GET /reference/institutions`, quality checks and crosswalk status |
| Administration visitor/fleet/records | Static | `GET /administration/summary` from approved aggregate operational feeds |
| VMR/KPI heat map/exception tables | Static seed data | `GET /vmr/summary`, aggregate-only visits/vehicles; no identities or PII |
| KPI chart drill modal | Local aggregate split | Policy-checked drill endpoint, source authorization and small-group suppression |
| Alerts table/detail | Local mutation | Alert rule, lifecycle, assignment, escalation and audit REST operations |
| Integration source cards | Static plus local sync simulation | Refresh-run, retry, source-health and quarantine APIs |
| User table/role matrix | Local role mutations | User, role, permission, session and access-review APIs |
| Access history/audit log | Static seed data | Tamper-evident audit-event query and export with audit |
| Export aggregate report | Local toast only | Server-side aggregate export, approval, limits and audit |

## RVM/VMM wireframe

| Screen/component | Static/local | Backend required |
|---|---|---|
| SSO sign-in | Static | OIDC session, MFA, role and workstation authorization |
| Reception dashboard | Static | Visit/appointment/queue aggregates and operational read APIs |
| Register visitor/profile/duplicates | Static form content | Duplicate search, protected identity validation and secure visit creation |
| Identity verification | Static | Document capture policy, secure document storage and verification result |
| Signature capture | Static | Consent text version, signature token, secure attachment and audit |
| Live queue | Static | Queue state stream/polling, transition authorization and public-token rules |
| Visit detail/entry approval/items | Static | State-machine transition, host response, item custody and audit |
| Host officer mobile | Static | Authenticated host identity, accept/delay/redirect/decline operations |
| Appointments | Static calendar | Host availability, conflict check, appointment lifecycle |
| Gate console | Static | Plate lookup, repeat-vehicle recognition, admission/exit commands |
| Gate/security dashboard | Static | Refreshed vehicle movements and exception approval |
| Device custody | Static | Custody issue/return, token uniqueness and chain-of-custody audit |
| RVM management dashboard | Static aggregate | Approved management feed; must not expose protected visitor details |
| Master data | Static | Effective-dated configuration, role mapping and approval |
| Audit log | Static | Tamper-evident immutable event stream and audited export |

## HRMS wireframe

| Screen/component | Static/local | Backend required |
|---|---|---|
| Establishment dashboard | Static | Aggregated cadre/position/alert APIs |
| Cadre management/change request | Static form/table | Effective-dated cadre, approval workflow and authority documents |
| Position categorization | Static | Position category CRUD and assignment |
| Vacancies and alerts | Static | Vacancy calculation and configurable alert rules |
| Organization/workflow engine | Static | Organizational hierarchy and workflow configuration |
| Organization chart | Static | Position/employee aggregate or role-authorized organization view |
| Duty-time policies | Static | Versioned effective-dated policies and employee resolution |
| Salary scales/schemes | Static | Approved salary and deduction configuration |
| Inherited liabilities | Static | HR/liability state and controlled payroll feed |
| Employee insurance | Static | Employee coverage and provider integration |
| SOR/publishing | Static | Recruitment workflow and external publishing integration |
| Exam coordination emails | Static | Organization contacts, templates, approval and mail provider integration |
| Attendance/no-pay | Static | Device-log import, leave validation, period close and payroll calculation |
| Performance evaluation | Static | Weighted evaluation, approval stages, history and reports |

## Portal

The portal is a static/bundled launch surface. Backend work should include authenticated application catalogue, availability/roadmap status, launch URLs and deep-link entitlements. Application availability must not be inferred from the current prototype.
