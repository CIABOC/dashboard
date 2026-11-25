window.vmrView = (() => {
  const number = new Intl.NumberFormat("en-LK");
  const date = new Intl.DateTimeFormat("en-LK", { dateStyle: "medium", timeZone: "Asia/Colombo" });
  const time = new Intl.DateTimeFormat("en-LK", {
    dateStyle: "medium", timeStyle: "short", timeZone: "Asia/Colombo",
  });

  const icons = {
    visitor: '<circle cx="12" cy="8" r="3"/><path d="M5 20v-2a4 4 0 0 1 4-4h6a4 4 0 0 1 4 4v2Z"/>',
    vehicle: '<path d="m5 11 2-6h10l2 6M5 18H3v-5l2-2h14l2 2v5h-2m-14 0h14M5 18v2m14-2v2M7 15h2m6 0h2"/>',
    premises: '<path d="M4 21V7h8v14m0-10h8v10M2 21h20M7 10h2m-2 4h2m-2 4h2m6-4h2m-2 4h2"/>',
    shield: '<path d="M12 22s7-3 7-10V6l-7-3-7 3v6c0 7 7 10 7 10Z"/><path d="M12 8v4m0 4h.01"/>',
    clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    badge: '<rect x="4" y="7" width="16" height="13" rx="2"/><path d="M10 7V4h4v3M8 12h3m-3 4h8m0-4h1"/>',
    filter: '<path d="M4 7h16M4 17h16"/><circle cx="9" cy="7" r="2" fill="white"/><circle cx="15" cy="17" r="2" fill="white"/>',
    refresh: '<path d="M20 7v5h-5M4 17v-5h5m-4-2a8 8 0 0 1 13-3l2 5M4 12l2 5a8 8 0 0 0 13-3"/>',
  };

  const metrics = [
    ["registered", "Visits registered", "visits", "total"],
    ["completed", "Visits completed", "visits", "completed"],
    ["declined", "Visits declined", "visits", "declined"],
    ["cancelled", "Visits cancelled", "visits", "cancelled"],
    ["entries", "Vehicle entries", "vehicles", "entries"],
    ["exits", "Vehicle exits", "vehicles", "exits"],
    ["denials", "Vehicle denials", "vehicles", "denials"],
  ];

  const tableColumns = [
    ["registered", "Visits", "visits", "total"],
    ["completed", "Completed", "visits", "completed"],
    ["entries", "Vehicle entries", "vehicles", "entries"],
    ["exits", "Vehicle exits", "vehicles", "exits"],
    ["denials", "Vehicle denials", "vehicles", "denials"],
  ];

  function icon(name) {
    return `<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${icons[name]}</svg>`;
  }

  const format = (value) => value == null ? "—" : number.format(value);
  const dayLabel = (day) => date.format(new Date(day + "T00:00:00Z"));

  function summarize(report, include) {
    return Object.fromEntries(metrics.map(([key, , group, field]) => [
      key,
      report[group].reduce((sum, row) => include(row.day) ? sum + row[field] : sum, 0),
    ]));
  }

  function readouts(report) {
    if (!report) return {};
    return {
      selected: summarize(report, (day) => day >= report.from),
      today: summarize(report, (day) => day === report.today),
      prior: report.previousFrom === report.from ? null : summarize(report, (day) => day < report.from),
    };
  }

  function delta(value, previous) {
    if (value == null || previous == null) return "";
    const change = value - previous;
    return `<span class="vmr-delta">${change > 0 ? "+" : ""}${format(change)} vs previous period</span>`;
  }

  function statusPill(text, tone = "") {
    return `<span class="vmr-pill ${tone}">${text}</span>`;
  }

  function detailButton(key, text) {
    return `<button class="vmr-link" type="button" data-vmr-detail="${key}">${text}</button>`;
  }

  function featuredCard({ title, glyph, tone = "", value, context, foot, detail, action, comparison = "" }) {
    return `<article class="vmr-feature ${tone}">
      <div class="vmr-feature-heading"><h3>${title}</h3><span class="vmr-icon">${icon(glyph)}</span></div>
      <div class="vmr-feature-readout"><strong>${format(value)}</strong>${comparison}</div>
      <p class="vmr-feature-context">${context}</p>
      <footer><span>${foot}</span>${detailButton(detail, action)}</footer>
    </article>`;
  }

  function featureCards({ selected, prior }) {
    const movements = selected ? selected.entries + selected.exits : null;
    const previousMovements = prior ? prior.entries + prior.exits : null;

    return `<section class="vmr-features" aria-label="Key indicators">
      ${featuredCard({
        title: "Total visits", glyph: "visitor", value: selected?.registered,
        context: "Visit registrations in the selected period", foot: "Unique visitors —",
        detail: "visitors", action: "Details", comparison: delta(selected?.registered, prior?.registered),
      })}
      ${featuredCard({
        title: "Vehicle movements", glyph: "vehicle", value: movements,
        context: `<b>${format(selected?.entries)}</b> Entries <span>·</span> <b>${format(selected?.exits)}</b> Exits`,
        foot: "Checkpoint count —", detail: "vehicles", action: "Inspect",
        comparison: delta(movements, previousMovements),
      })}
      ${featuredCard({
        title: "On premises now", glyph: "premises", tone: "vmr-amber",
        context: "Visitor and vehicle occupancy", foot: "Source not connected", detail: "premises", action: "Live roster",
      })}
      ${featuredCard({
        title: "Security denials", glyph: "shield", tone: "vmr-critical", value: selected?.denials,
        context: "Vehicle denials in the selected period", foot: "Watch-list feed —",
        detail: "denials", action: "Audit log", comparison: delta(selected?.denials, prior?.denials),
      })}
      ${featuredCard({
        title: "Host SLA rate", glyph: "clock", tone: "vmr-green",
        context: "Host responses within the SLA", foot: "Average response —", detail: "sla", action: "By division",
      })}
    </section>`;
  }

  function panelHeading(title, summary, glyph, extra = "") {
    return `<div class="vmr-panel-heading">
      <span class="vmr-icon">${icon(glyph)}</span>
      <div><h3>${title}</h3><p>${summary}</p></div>
      ${extra}
    </div>`;
  }

  function unavailableMetrics(items) {
    return `<div class="vmr-mini-grid">${items.map(([title, description]) => `<div class="vmr-mini">
      <h4>${title}</h4><strong>—</strong><p>${description}</p>
    </div>`).join("")}</div>`;
  }

  function emptyWidget(title, detail) {
    return `<section class="vmr-widget"><h4>${title}</h4>
      <div class="vmr-widget-empty"><span>Source not connected</span><span>${detail}</span></div>
    </section>`;
  }

  function operations() {
    return `<div class="vmr-operations">
      <section class="vmr-panel vmr-operations-panel">
        ${panelHeading("Reception & Visitor Operations", "Visit arrivals, check-ins and desk activity.", "badge", statusPill("Desks active —"))}
        ${unavailableMetrics([
          ["Fulfillment", "Scheduled visits"], ["Avg dwell time", "Time on premises"], ["Held badges / passes", "Pending desk return"],
        ])}
        ${emptyWidget("Hourly visitor influx (today)", "Hourly arrival data is unavailable.")}
        ${emptyWidget("Visitor demographics breakdown", "Visitor categories are unavailable.")}
        <footer class="vmr-panel-footer"><span>Biometric verification accuracy <b>—</b></span>${detailButton("visitors", "Visitor details")}</footer>
      </section>
      <section class="vmr-panel vmr-operations-panel vmr-gate">
        ${panelHeading("Fleet & Gate Checkpoint Activity", "Gate traffic, parking occupancy and vehicle checks.", "vehicle", statusPill("Checkpoints open —"))}
        ${unavailableMetrics([
          ["Gate volume", "Entries and exits by gate"], ["Parking saturation", "Available bays"], ["Overstay flags", "Vehicle stay limits"],
        ])}
        ${emptyWidget("Checkpoint traffic volume", "Gate-level traffic is unavailable.")}
        ${emptyWidget("Vehicle classification distribution", "Vehicle types are unavailable.")}
        <footer class="vmr-panel-footer"><span>Security scan clearance <b>—</b></span>${detailButton("vehicles", "Vehicle details")}</footer>
      </section>
    </div>`;
  }

  function exceptions() {
    return `<section class="vmr-panel vmr-table-panel vmr-audit">
      ${panelHeading("Priority Security Exceptions & Audit Log", "Flagged events and follow-up actions.", "shield", statusPill("Source not connected"))}
      <div class="vmr-table-scroll"><table class="vmr-table">
        <thead><tr><th scope="col">Time</th><th scope="col">Location / gate</th><th scope="col">Identifier</th><th scope="col">Trigger reason</th><th scope="col">Status</th></tr></thead>
        <tbody><tr><td colspan="5" class="vmr-empty-row">The audit and exceptions feed is not connected.</td></tr></tbody>
      </table></div>
      <footer class="vmr-panel-footer"><span>Shift report export</span><button class="vmr-button" type="button" disabled>Download shift report (PDF)</button></footer>
    </section>`;
  }

  function totalsSection(report, { selected, today, prior }) {
    return `<section class="vmr-panel vmr-totals">
      ${panelHeading("Reporting totals", report ? `${dayLabel(report.from)} – ${dayLabel(report.today)} · Today: ${dayLabel(report.today)}` : "Selected period and today", "visitor")}
      <div class="vmr-totals-grid">${metrics.map(([key, label]) => `<article class="vmr-total">
        <h4>${label}</h4><div><strong>${format(selected?.[key])}</strong>${delta(selected?.[key], prior?.[key])}</div>
        <p>Today <b>${format(today?.[key])}</b></p>
      </article>`).join("")}</div>
    </section>`;
  }

  function activityTable(report, columns = tableColumns) {
    let rows = "";
    if (report) {
      const visits = new Map(report.visits.map((row) => [row.day, row]));
      const vehicles = new Map(report.vehicles.map((row) => [row.day, row]));
      const firstDay = new Date(report.today + "T00:00:00Z");
      firstDay.setUTCDate(firstDay.getUTCDate() - 13);
      const from = firstDay.toISOString().slice(0, 10);
      const daily = { visits, vehicles };
      const groups = new Set(columns.map(([, , group]) => group));
      const days = [...new Set([...groups].flatMap((group) => [...daily[group].keys()]))]
        .filter((day) => day >= from && day <= report.today).sort().reverse();
      rows = days.map((day) => `<tr><th scope="row">${dayLabel(day)}</th>${
        columns.map(([, , group, field]) => `<td>${format(daily[group].get(day)?.[field])}</td>`).join("")
      }</tr>`).join("");
    }
    const message = report ? "No activity recorded in the last 14 days." : "Daily activity will appear when the report loads.";
    return `<div class="vmr-table-scroll"><table class="vmr-table">
      <thead><tr><th scope="col">Date</th>${columns.map(([, label]) => `<th scope="col">${label}</th>`).join("")}</tr></thead>
      <tbody>${rows || `<tr><td colspan="${columns.length + 1}" class="vmr-empty-row">${message}</td></tr>`}</tbody>
    </table></div>`;
  }

  const details = {
    visitors: {
      title: "Visitor triage & visit details", keys: ["registered", "completed", "declined", "cancelled"],
      missing: "Unique visitor counts, verified check-ins and visitor rosters",
    },
    vehicles: {
      title: "Vehicle movements & gate telemetry", keys: ["entries", "exits", "denials"],
      missing: "Checkpoint breakdowns, plate scans and parking occupancy",
    },
    premises: {
      title: "Premises occupancy & live roster",
      missing: "Current occupants, active passes and parked vehicles",
    },
    denials: {
      title: "Security denials & exceptions", keys: ["denials"],
      missing: "Watch-list alerts, exceptions and audit records",
    },
    sla: {
      title: "Host response SLA by division",
      missing: "Host response times, SLA rates and division-level figures",
    },
  };

  function detail(key, report) {
    const item = details[key];
    const summary = readouts(report).selected;
    const values = metrics.filter(([metric]) => item.keys?.includes(metric));
    const columns = tableColumns.filter(([metric]) => item.keys?.includes(metric));
    return `<header class="vmr-drawer-head"><div><h3 id="vmr-drawer-title">${item.title}</h3>
      <p>${report && values.length ? "Selected-period totals and daily aggregate activity." : "Reporting source not connected."}</p></div>
      <button class="vmr-button" type="button" data-vmr-close>Close</button></header>
      <div class="vmr-drawer-body">
        ${values.length ? `<dl class="vmr-detail-values">${values.map(([metric, label]) =>
          `<div><dt>${label}</dt><dd>${format(summary?.[metric])}</dd></div>`).join("")}</dl>
          <h4>Daily activity · last 14 days</h4>${activityTable(report, columns)}` : ""}
        ${emptyWidget(item.missing, "These details are unavailable.")}
      </div>`;
  }

  function filters(options, open) {
    return `<details class="vmr-filters" data-vmr-filters ${open ? "open" : ""}>
      <summary class="vmr-button">${icon("filter")} Filter View</summary>
      <div class="vmr-filter-fields">
        <label for="vmr-period">Reporting period</label>
        <select id="vmr-period" data-a="per">
          ${[3, 6, 12].map((months) => `<option value="${months}" ${months === options.months ? "selected" : ""}>Last ${months} months</option>`).join("")}
        </select>
        <label class="vmr-check"><input type="checkbox" data-a="cmp" ${options.compare ? "checked" : ""}> Compare to previous</label>
      </div>
    </details>`;
  }

  function board(report, options, status) {
    const availability = status.issue ? statusPill("Report unavailable", "vmr-pill-warning")
      : report ? statusPill("Live · Atlas", "vmr-pill-live") : statusPill("Connecting to the reporting service");
    const updated = report ? `<span>Last fetched ${time.format(new Date(report.fetchedAt))} SLST</span>` : "";
    const totals = readouts(report);
    const hasActivity = report && Object.values(totals.selected).some(Boolean);

    return `<header class="vmr-hero">
      <div><h2>Perimeter & Reception Dashboard</h2><p>Aggregate reception and vehicle activity in Sri Lanka time.</p>
        <div class="vmr-source" role="status">${availability}${updated}</div>
      </div>
      <div class="vmr-toolbar">
        ${status.canRefresh ? `<span class="vmr-cadence">Auto-refreshes every ${status.refreshSeconds}s</span>` : ""}
        ${status.signInRequired ? '<a class="vmr-button" href="/login" target="_top">Sign in</a>' : status.canRefresh ?
          `<button class="vmr-button" type="button" data-a="vmrrefresh" ${status.pending ? 'aria-disabled="true"' : ""}>${icon("refresh")} ${status.pending ? "Refreshing…" : "Refresh"}</button>` : ""}
        ${filters(options, status.filtersOpen)}
      </div>
    </header>
    ${status.issue ? '<p class="vmr-notice" role="status" data-vmr-status></p>' : ""}
    ${report && !hasActivity ? '<p class="vmr-notice vmr-notice-neutral" role="status">No visit or vehicle activity was recorded in the selected period.</p>' : ""}
    ${featureCards(totals)}
    ${operations()}
    ${exceptions()}
    ${totalsSection(report, totals)}
    <section class="vmr-panel vmr-table-panel">
      ${panelHeading("Daily activity", "Last 14 days in Sri Lanka time.", "clock")}
      ${activityTable(report)}
    </section>`;
  }

  return { board, detail };
})();
