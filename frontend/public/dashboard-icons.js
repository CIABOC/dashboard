(() => {
  const paths = {
    brand: '<path d="M12 22s7-3.4 7-10V6l-7-3-7 3v6c0 6.6 7 10 7 10Z"/><path d="m9 12 2 2 4-4"/>',
    overview: '<rect x="3.5" y="3.5" width="7" height="7" rx="1.25"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.25"/><rect x="3.5" y="13.5" width="7" height="7" rx="1.25"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.25"/>',
    vmr: '<path d="m5 11 2-6h10l2 6M5 18H3v-5l2-2h14l2 2v5h-2m-14 0h14M5 18v2m14-2v2M7 15h2m6 0h2"/>',
    complaints: '<path d="M8 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V9l-5-5h-2m2 0v5h5M8 12h8m-8 4h5"/>',
    eald: '<rect x="5" y="5" width="14" height="16" rx="2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="m9 14 2 2 4-4"/>',
    hr: '<circle cx="9" cy="7.5" r="3"/><path d="M3 20v-2.5A3.5 3.5 0 0 1 6.5 14h5a3.5 3.5 0 0 1 3.5 3.5V20m2-15a3 3 0 0 1 0 6m4 9v-2.5a3.5 3.5 0 0 0-3-3.4"/>',
    finance: '<path d="M4 7.5V6a2 2 0 0 1 2-2h12v3.5M4 7.5h14a2 2 0 0 1 2 2V11m0 5v2a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7.5Z"/><path d="M21 11h-5a2.5 2.5 0 0 0 0 5h5Zm-4.5 2.5h.01"/>',
    assets: '<path d="m3 7 9-4 9 4v10l-9 4-9-4Zm0 0 9 4 9-4m-9 4v10m-4.5-16 9 4"/>',
    plan: '<circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1"/>',
    digital: '<rect x="3" y="4.5" width="18" height="12" rx="2"/><path d="M9 20h6m-3-3.5V20M7.5 10.5l2 2 4-4"/>',
    ict: '<path d="M12 22s7-3.4 7-10V6l-7-3-7 3v6c0 6.6 7 10 7 10Z"/><rect x="9.5" y="11" width="5" height="4" rx="1"/><path d="M11 11V9.5a1 1 0 0 1 2 0V11"/>',
    lkeh: '<path d="m3 8 9-5 9 5v1H3Zm1 12h16M6 11v6m4-6v6m4-6v6m4-6v6"/>',
    admin: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M8 7V5a1 1 0 0 1 1-1h6a1 1 0 0 1 1 1v2M3 12h18m-11 0v2h4v-2"/>',
    integ: '<ellipse cx="12" cy="6" rx="8" ry="3"/><path d="M4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3"/>',
    users: '<circle cx="8.5" cy="8" r="3"/><path d="M3 20v-2a3.5 3.5 0 0 1 3.5-3.5h4"/><rect x="13" y="13" width="8" height="7" rx="1"/><path d="M15 13v-2a2 2 0 0 1 4 0v2m-2 3.5h.01"/>',
    alerts: '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9Zm-8 12a2.4 2.4 0 0 0 4 0"/>',
    collapse: '<rect x="3" y="4.5" width="18" height="15" rx="2"/><path d="M9 4.5v15m7-10-2.5 2.5 2.5 2.5"/>',
    expand: '<rect x="3" y="4.5" width="18" height="15" rx="2"/><path d="M9 4.5v15m5-10 2.5 2.5-2.5 2.5"/>',
    settings: '<path d="M4 6h16M4 12h16M4 18h16"/><circle cx="8" cy="6" r="2" fill="var(--side-icon-fill, white)"/><circle cx="15" cy="12" r="2" fill="var(--side-icon-fill, white)"/><circle cx="10" cy="18" r="2" fill="var(--side-icon-fill, white)"/>',
    logout: '<path d="M10 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h5m-1-8h12m-4-4 4 4-4 4"/>',
    search: '<circle cx="10.5" cy="10.5" r="5.5"/><path d="m15 15 4 4"/>',
    download: '<path d="M12 3v12m-4-4 4 4 4-4M5 17v3a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-3"/>',
    down: '<path d="m6 9 6 6 6-6"/>',
  };

  window.dashboardIcon = (name) =>
    `<svg class="dashboard-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${paths[name]}</svg>`;
})();
