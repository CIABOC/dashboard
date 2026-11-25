window.vmrLive = (() => {
  const refreshInterval = 30000;
  const unavailable = "The Atlas report could not be loaded. Try refreshing.";

  let endpoint;
  let host;
  let options;
  let report;
  let error;
  let signInRequired;
  let pending;
  let timer;
  let updateDrawer;
  let filtersOpen = false;

  function prepare(element) {
    element.innerHTML = '<div class="vmr-content" data-vmr-content></div><dialog class="vmr-drawer" aria-labelledby="vmr-drawer-title"></dialog>';
    const drawer = element.querySelector("dialog");
    let opener;

    element.addEventListener("click", (event) => {
      const detail = event.target.closest("[data-vmr-detail]");
      if (detail && !drawer.open) {
        opener = detail;
        drawer.innerHTML = window.vmrView.detail(detail.dataset.vmrDetail, report);
        drawer.showModal();
      } else if (drawer.open && (event.target === drawer || event.target.closest("[data-vmr-close]"))) {
        drawer.close();
      }
    });

    drawer.addEventListener("close", () => {
      if (opener && !opener.isConnected) {
        element.querySelector(`[data-vmr-detail="${opener.dataset.vmrDetail}"]`)?.focus({ preventScroll: true });
      }
      opener = null;
    });

    updateDrawer = () => {
      if (!drawer.open || !opener) return;
      const hadFocus = drawer.contains(document.activeElement);
      drawer.innerHTML = window.vmrView.detail(opener.dataset.vmrDetail, report);
      if (hadFocus) drawer.querySelector("[data-vmr-close]").focus({ preventScroll: true });
    };
  }

  function draw() {
    if (!host) return;
    const content = host.querySelector("[data-vmr-content]");
    const active = document.activeElement;
    const selectors = ["[data-a=vmrrefresh]", "[data-a=per]", "[data-a=cmp]", "[data-vmr-filters] summary"];
    const focused = content.contains(active) && selectors.find((selector) => active.matches(selector));
    filtersOpen = content.querySelector("[data-vmr-filters]")?.open ?? filtersOpen;
    const issue = endpoint ? error : window.parent === window ? "Open this report from the dashboard." : null;

    content.innerHTML = window.vmrView.board(report, options, {
      issue, signInRequired, pending, filtersOpen, canRefresh: Boolean(endpoint), refreshSeconds: refreshInterval / 1000,
    });
    if (issue) content.querySelector("[data-vmr-status]").textContent = issue;
    if (focused) content.querySelector(focused)?.focus({ preventScroll: true });
    updateDrawer();
  }

  function resetReport() {
    pending?.abort();
    pending = report = error = null;
    signInRequired = false;
  }

  async function refresh() {
    if (!host || !endpoint || pending) return;
    const request = new AbortController();
    pending = request;
    draw();
    const url = new URL(endpoint);
    url.search = new URLSearchParams(options);

    try {
      const response = await fetch(url, {
        credentials: "include",
        cache: "no-store",
        signal: AbortSignal.any([request.signal, AbortSignal.timeout(25000)]),
      });
      if (request.signal.aborted) return;
      signInRequired = false;
      if (response.status === 204) {
        report = null;
        error = "Atlas reporting is not configured.";
        return;
      }
      if (response.status === 401 || response.status === 403 || response.redirected) {
        report = null;
        signInRequired = true;
        error = "Sign in with a dashboard role that can view reception reports.";
        return;
      }
      const body = await response.json();
      if (request.signal.aborted) return;
      if (!response.ok || !body.success || !body.data) {
        report = null;
        error = response.status === 503 && typeof body.error === "string" ? body.error : unavailable;
        return;
      }
      report = body.data;
      error = null;
    } catch {
      if (!request.signal.aborted) {
        report = null;
        signInRequired = false;
        error = unavailable;
      }
    } finally {
      if (pending === request) {
        pending = null;
        draw();
      }
    }
  }

  function unmount() {
    clearInterval(timer);
    timer = null;
    resetReport();
    host = options = null;
    updateDrawer = null;
  }

  function mount(element, selection) {
    if (!element) return;
    const changed = options?.months !== selection.months || options?.compare !== selection.compare;
    if (changed) resetReport();
    if (element !== host) {
      host = element;
      prepare(element);
    }
    options = selection;
    if (endpoint && !timer) {
      timer = setInterval(() => {
        if (document.visibilityState === "visible") void refresh();
      }, refreshInterval);
    }
    if (endpoint && changed) void refresh();
    else draw();
  }

  function connect(apiBase) {
    try {
      const base = new URL(apiBase);
      if (!["http:", "https:"].includes(base.protocol) || base.username || base.password) return false;
      const next = new URL("/api/v1/vmr/overview", base);
      if (endpoint?.href === next.href) return false;
      unmount();
      endpoint = next;
      return true;
    } catch {
      return false;
    }
  }

  function disconnect() {
    unmount();
    endpoint = null;
  }

  return { connect, disconnect, mount, unmount, refresh };
})();
