# Wireframe Mapping

## Reference inventory

| Reference | Location | Status / relationship |
|---|---|---|
| Dashboard Concept Note v1.0 | `C:\Users\Ranuda Perera\Downloads\CIABOC_Dashboard_Concept_Note_v1.0.pdf` | Functional and management requirements reference |
| System Architecture Document v1.0 | `C:\Users\Ranuda Perera\Downloads\CIABOC_Dashboard_System_Architecture_Document_v1.0.pdf` | Target architecture and technical constraints |
| Management dashboard prototype | `C:\Users\Ranuda Perera\Downloads\CIABOC Management Dashboard – Prototype (1).html` | Tagged dashboard wireframe; copied to `public/prototype.html` |
| Management dashboard duplicate | `C:\Users\Ranuda Perera\Downloads\CIABOC Management Dashboard – Prototype.html` | Near-duplicate prototype; retained only as a source reference, not as a second implementation |
| Reception / Visitor / Vehicle prototype | `C:\Users\Ranuda Perera\Downloads\CIABOC-RVM-VMM-HTML\CIABOC-RVM-VMM-HTML\` | Separate operational wireframe bundle: 15 screens, shared CSS, previews and fonts |
| HRMS prototype | `C:\Users\Ranuda Perera\Downloads\CIABOC_HRMS.html` | Separate establishment, recruitment and workforce wireframe bundle |
| Application Portal prototype | `C:\Users\Ranuda Perera\Downloads\CIABOC Portal.html` | Separate application-launch portal, bundled React/JavaScript |

The management dashboard prototype is the controlling wireframe for the dashboard application. The RVM/VMM, HRMS and Portal files are reference prototypes for source applications or potential integrations; they must not be redesigned or combined into the dashboard without a separate approved requirement.

## Management dashboard: `public/prototype.html`

| Screen / nav destination | Dashboard module | Main sections | Reusable components |
|---|---|---|---|
| Login | Authentication shell | Institutional brand, SSO action, MFA explanation, prototype disclaimer | Login card, primary button, notice panel |
| Executive Overview | Executive Overview | 6 KPI cards, monthly complaint trend, case status, e-ALD progress, budget utilization, performance KPI progress, annual trend, management attention | Sidebar, header, KPI row, card, bar/line/donut/progress charts, attention panel |
| Vehicle Movement & Reception | Administration / Reception | Current-period KPIs, visit trends/outcomes/vehicles, hourly heat map, category/division/owner breakdowns, exception table, target table | KPI row, line chart, heat map, donut/progress charts, table, drawer |
| Complaints & Cases | Complaints and Cases | Division filter, complaint KPI cards, monthly trend, status distribution, stage distribution, age bands | Filter bar, KPI row, line/donut/progress/bar charts, table |
| e-ALD | e-ALD | Confidentiality notice, submission KPIs, cumulative trend, category progress, agency coverage table | Notice, KPI row, line/progress chart, searchable/sortable table |
| Human Resources | Human Resources | HR notice, cadre KPIs, workforce trend, vacancy progress, leave mix, training bars | Notice, KPI row, line/progress/donut/bar chart |
| Finance | Finance | Budget KPIs, expenditure trend, department budget comparison, share, variance table | KPI row, line/pair/donut chart, table |
| Assets & Inventory | Assets and Inventory | Asset KPIs, category mix, verification progress, lifecycle bars, stock exceptions table | KPI row, donut/progress/bar chart, table |
| Planning & Performance | Planning and Performance | Plan KPIs, plan state mix, target vs actual, divisional performance | KPI row, donut/pair/progress chart |
| Digital Services | Digital Services | Usage KPIs, traffic trend, channel mix, monthly comparison, availability | KPI row, line/donut/bar/progress chart |
| ICT & Cybersecurity | ICT and Cybersecurity | Availability KPIs, incidents, uptime, backup readiness, system health tiles | KPI row, bar/progress chart, status tiles |
| Institutional Reference Data (LKEH) | Institutional Reference Data | Institution KPIs, classification, quality indicators, incomplete mapping table | KPI row, donut/progress chart, table |
| Administration | Administration | Visitor/fleet/records KPIs, 14-day visitors, visitor trend, fleet utilization, records workflow, response time | KPI row, bar/line/progress/donut chart |
| Data Integration & System Health | Data Integration and System Health | Failed-source warning, source health KPIs, source cards, refresh history | Notice, KPI row, source status card, table, system drawer |
| User & Access Management | User and Access Management | User tabs, role/permission matrix, access history, audit logs | Tabs, table, role dropdown, status chips |
| Alerts & Decision Support | Alerts and Decision Support | Severity filters, alert table, status/owner/escalation fields | Filter chips, table, alert drawer, acknowledgement/assign/escalate actions |

### Shared management-dashboard structure

- Fixed or collapsible 260/68 px sidebar.
- Brand block, ordered navigation, nested administration items, alert badge and user footer.
- Sticky header with breadcrumb, page title, global search, period, comparison, language, notification and profile controls.
- Page action strip with freshness/status chip, sample-data label and export action.
- Six-column responsive grid with `s2`, `s3`, and `s6` spans.
- KPI row, card header, chart/visualization container, status chip, filters, table, pagination and loading skeleton.
- Notification/profile popovers, KPI breakdown modal, alert/source drawer and toast feedback.

## RVM/VMM HTML bundle

| HTML file | Page name | Main sections / reusable components |
|---|---|---|
| `01-sso-sign-in.html` | SSO sign-in | SSO-only sign-in card, institutional hero panel |
| `02-reception-dashboard.html` | Reception dashboard | Today KPIs, operational queues, appointments, category/division bars, arrivals chart |
| `03-register-visitor.html` | Register visitor | Multi-step header, profile search/duplicates, visitor category, purpose, host and visit details |
| `04-identity-verification.html` | Identity verification | Stored document scan, front/back view, scan-new flow, no-document exception, visit summary |
| `05-signature-capture.html` | Signature capture | Signature canvas area, visitor declaration text, language controls, visit summary |
| `06-live-queue.html` | Live queue board | Public neutral queue tokens, status columns/cards, waiting-time KPIs |
| `07-visit-detail.html` | Visit detail | Lifecycle rail, entry approval, identity/signature summary, item check, notifications, audited timeline |
| `08-host-officer-mobile.html` | Host officer mobile | Mobile visit requests, accept/delay/redirect/decline actions, appointment context |
| `09-appointments.html` | Appointments | Calendar/schedule, appointment table/card state, overlap checks, create appointment controls |
| `10-gate-console.html` | Gate console | Plate lookup, open movement, at-gate list, record exit, minimum-detail movement linking |
| `11-gate-dashboard.html` | Gate and security dashboard | Gate KPIs, vehicles needing attention, movement chart, inside-by-category bars, approvals |
| `12-device-custody.html` | Device custody | Custody register, token search, ready-for-return state, handover form |
| `13-management-dashboard.html` | Management dashboard | Two-week trend, outcomes, post-validation KPIs, category/division breakdowns, waiting-time chart |
| `14-admin-master-data.html` | Admin master data | Time limits/cutoffs, acknowledgement text versions, SSO role mapping, effective date |
| `15-audit-log.html` | Audit log | Filters/search, read-only event table, export action and integrity check |

The RVM bundle uses a separate shared stylesheet (`assets/css/rvm.css`), its own 252 px sidebar/topbar pattern and 15 associated JPG previews. It declares both UI and wireframe modes and is a direct RVM/VMM operational prototype, not part of the central executive wireframe.

## HRMS HTML prototype

| Route/page | Main sections / reusable components |
|---|---|
| Establishment Dashboard | Workforce KPI cards, alerts, establishment indicators and action areas |
| Dynamic Cadre Management | Filters, cadre-vs-actual table, cadre change request, effective date and approval workflow |
| Position Categorization | Position category management and assignment |
| Vacancies and Alerts | Vacancy register and configurable due/expiring alerts |
| Org and Workflow Engine | Organization/workflow configuration |
| Organization Chart | Hierarchical organization and position categorization |
| Duty Time Policies | Policy table/editor, fixed/flexible/shift configuration, effective policy lookup |
| Salary Scales and Schemes | Salary scales, deductions and configurable pay schemes |
| Inherited Liabilities | Historical payroll/liability handling |
| Employee Insurance | Insurance policy and employee coverage management |
| SOR and Publishing | Statement of requirement, approval and publishing workflow |
| Exam Coordination Emails | Exam details, template, generated recipient emails and contact directory |
| Attendance and No-Pay | Period filters, attendance register, monthly summary and no-pay outcomes |
| Performance Evaluation | Rating scale, employee evaluation, weighted score, history and reports |

The HRMS prototype is a 14-route SPA-style HTML wireframe with a shared sidebar/header and card/table/form components. It includes operational HR transactions and protected-personal-data contexts; only approved aggregate feeds should enter the central dashboard.

## Application Portal HTML prototype

The portal is a bundled React/JavaScript HTML file. Visible application cards include:

- Assets and Liabilities / e-ALD — available
- Complaint Tracking — available
- Service Support — available
- Complaint Management — roadmap
- CIABOC HR — roadmap
- Finance — roadmap
- Assets Management — roadmap
- Digital Records — roadmap

The portal remains a separate application-launch portal. It is not a dashboard screen and should not be used as a source of management data beyond approved links/navigation.
