# Component Inventory

## Shared components identified in the management prototype

| Component | Intended reuse | Wireframe state | Backend-independent behavior |
|---|---|---|---|
| App shell | All authenticated dashboard screens | Existing | Sidebar state, language, role and page routing |
| Sidebar | All authenticated dashboard screens | Existing | Collapse, active navigation, role filtering, alert badge |
| Header | All authenticated dashboard screens | Existing | Breadcrumb/title, search, period, compare, language, notifications, profile |
| KPI card / KPI row | Executive and module screens | Existing | Formatted value, trend marker, click-to-breakdown modal |
| Card container | Charts, notices and operational panels | Existing | Grid span and header/subtitle layout |
| Chart container | Line, bar, donut, progress and heat-map views | Existing | Tooltip/title, legend and drill action |
| Filter bar | Complaints, VMR, data tables and admin views | Existing | Period/division/direction/status controls |
| Search field | Header and tables | Existing | Client filtering prototype |
| Status chip | Modules, alerts and source/integration screens | Existing | Status mapping |
| Data table | Tables across most modules | Existing | Search, sorting, 6-row pagination |
| Tabs | User management and operational modules | Existing | Tab selection |
| Notification popover | Header | Existing | New-alert list |
| Profile/role popover | Header | Existing | Role switch and account controls |
| KPI breakdown modal | KPI and chart drill-down | Existing | Aggregate breakdown only |
| Alert drawer | Alerts | Existing | Acknowledge, assign, escalate and closure evidence |
| Source-system drawer | Integration/health | Existing | Source details and sync action |
| Confirmation/toast | Mutating prototype actions | Existing | Temporary success/error feedback |
| Loading skeleton | Screen navigation | Existing | Placeholder card loading |
| Permission-denied state | Role-restricted modules | Existing | Deny without disclosing unauthorized module data |
| Freshness status | Module page headers | Static label/seed data | Must derive from successful refresh timestamps |
| Sample-data disclosure | Page headers | Existing | Remove or control for production |

## Shared components identified in RVM/VMM

- 252 px application sidebar and topbar.
- Global search, notification, live-status and user identity controls.
- Page header/crumb/action strip.
- Primary, ghost, gold and danger buttons.
- Card header/body, KPI cards and status pills.
- Stepper and lifecycle rail.
- Tables, filters, segmented controls and form fields.
- Alerts, timeline, checklists, bar charts and SVG charts.
- UI/wireframe mode toggle and prototype control bar.
- Audit-recorded export/search/transition actions.

The RVM/VMM CSS explicitly includes responsive behavior for tablet/mobile and a wireframe variant for layout annotation.

## Shared components identified in HRMS

- 248 px establishment sidebar and module navigation.
- Header with breadcrumb, search, notifications and user context.
- Page title/action strip.
- KPI cards, cards, segmented tabs and filter grids.
- Data tables, select/date/search inputs, text areas and action buttons.
- Status chips, alerts and effective-date workflows.
- Approval/routing, version-history and audit-history patterns.

## Shared components absent or incomplete

- Real login/identity integration is missing; the prototype only advances locally.
- Production error boundary and API error-state components are not defined.
- No reusable authentication/session-expiry screen exists.
- No export confirmation/audit screen exists.
- No small-group suppression warning is implemented.
- No KPI semantic/version/freshness metadata component is implemented.
- No formal source-ownership/onboarding-status component is implemented beyond sample source cards.
- No database-backed pagination, server sorting or server filtering exists.
- No official CIABOC logo/crest is available in the dashboard prototype asset set.
