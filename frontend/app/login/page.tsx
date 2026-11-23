"use client";

import { useEffect, useState } from "react";

type RoleView = {
  userRoleId: number;
  roleCode: string;
  roleName: string;
  organizationalUnit: string | null;
};

const apiBase = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function LoginPage() {
  const [roles, setRoles] = useState<RoleView[]>([]);
  const [authenticated, setAuthenticated] = useState(false);
  const [selectedRole, setSelectedRole] = useState<number | null>(null);
  const [pin, setPin] = useState("");
  const [error, setError] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetch(`${apiBase}/api/v1/auth/session`, { credentials: "include" })
      .then((response) => response.json())
      .then((body) => {
        setAuthenticated(Boolean(body.data?.authenticated));
        setRoles(body.data?.roles ?? []);
        if (body.data?.temporaryLocalAuth) {
          window.location.href = "/dashboard";
        }
      })
      .catch(() => setAuthenticated(false));
  }, []);

  async function selectRole(userRoleId: number) {
    setError("");
    const response = await fetch(`${apiBase}/api/v1/auth/role/select`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userRoleId, pin: "" }),
    });
    const body = await response.json();
    if (!response.ok) {
      setError(body.error || "Role selection failed");
      return;
    }
    setSelectedRole(userRoleId);
  }

  async function verifyPin(event: React.FormEvent) {
    event.preventDefault();
    setError("");
    const response = await fetch(`${apiBase}/api/v1/auth/role/verify`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userRoleId: selectedRole, pin }),
    });
    const body = await response.json();
    if (!response.ok) {
      setError(body.error || "PIN verification failed");
      return;
    }
    window.location.href = "/dashboard";
  }

  async function loginLocally(event: React.FormEvent) {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    const response = await fetch(`${apiBase}/api/v1/auth/local/login`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    const body = await response.json();
    setSubmitting(false);
    if (!response.ok) {
      setError(body.error || "Login failed");
      return;
    }
    sessionStorage.setItem("ciaboc-temporary-session", "active");
    window.location.href = "/dashboard";
  }

  return (
    <main className="ct">
      <section className="lc" aria-label="CIABOC sign in">
        <div className="brand-lockup">
          <div className="logo" aria-hidden="true">C</div>
          <div>
            <p className="eyebrow">SECURE ACCESS</p>
            <h2>CIABOC Management</h2>
          </div>
        </div>
        <p className="institution">Commission to Investigate Allegations of Bribery or Corruption · Sri Lanka</p>

        {!authenticated ? (
          <>
            <form onSubmit={loginLocally} className="role-pin">
              <div className="field">
                <label htmlFor="email">Email address</label>
                <div className="input-wrap">
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M3.75 5.75h16.5v12.5H3.75zM4.5 7l7.5 6 7.5-6" />
                  </svg>
                  <input
                    id="email"
                    type="email"
                    autoComplete="username"
                    placeholder="name@ciaboc.gov.lk"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="field">
                <label htmlFor="password">Password</label>
                <div className="input-wrap">
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <rect x="5" y="10" width="14" height="10" rx="2" />
                    <path d="M8 10V7a4 4 0 018 0v3" />
                  </svg>
                  <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    autoComplete="current-password"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    required
                  />
                  <button
                    className="visibility"
                    type="button"
                    aria-label={showPassword ? "Hide password" : "Show password"}
                    onClick={() => setShowPassword((visible) => !visible)}
                  >
                    {showPassword ? "Hide" : "Show"}
                  </button>
                </div>
              </div>
              <button className="btn" type="submit" disabled={submitting}>
                {submitting ? "Signing in..." : "Sign in"}
              </button>
            </form>
            <div className="divider"><span>or continue with</span></div>
            <a className="btn secondary" href={`${apiBase}/oauth2/authorization/ciaboc`}>
              Sign in with CIABOC Account
            </a>
            <p className="security-note">
              Temporary local access while CIABOC SSO is being integrated.
            </p>
          </>
        ) : selectedRole ? (
          <form onSubmit={verifyPin} className="role-pin">
            <label htmlFor="role-pin">Enter your role-specific PIN</label>
            <input
              id="role-pin"
              className="srch"
              inputMode="numeric"
              autoComplete="off"
              pattern="[0-9]{8}"
              maxLength={8}
              value={pin}
              onChange={(event) => setPin(event.target.value.replace(/\D/g, ""))}
              required
            />
            <button className="btn" type="submit">Verify PIN</button>
          </form>
        ) : (
          <div className="role-list">
            {roles.map((role) => (
              <button
                className="role-option"
                type="button"
                key={role.userRoleId}
                onClick={() => selectRole(role.userRoleId)}
              >
                <strong>{role.roleName}</strong>
                {role.organizationalUnit && <small>{role.organizationalUnit}</small>}
              </button>
            ))}
          </div>
        )}

        {error && <p className="note" role="alert">{error}</p>}
        <div className="note">Prototype with fictional sample data. Figures do not represent actual CIABOC statistics.</div>
      </section>
      <style jsx>{`
        :root { --b:#0F3D63; --g:#C89B3C; --t:#1D6FA5; --tx:#26384A; --bd:#DDE5EC; --mu:#657789; }
        .ct { display:grid; place-items:center; min-height:100vh; background:radial-gradient(circle at 50% 16%,#3C719A 0,#1A4C73 36%,#0F3D63 68%); font:14px/1.45 'Source Sans 3','Noto Sans Sinhala','Noto Sans Tamil',system-ui,sans-serif; color:var(--tx); padding:24px; }
        .lc { background:rgba(255,255,255,.98); border:1px solid rgba(255,255,255,.7); border-radius:18px; padding:34px; width:min(440px,94vw); box-shadow:0 24px 70px rgba(7,38,65,.32),0 4px 14px rgba(7,38,65,.12); }
        .brand-lockup { display:flex; align-items:center; gap:12px; }
        .logo { width:48px; height:48px; border-radius:12px; background:linear-gradient(145deg,var(--g),#DDB968); color:var(--b); display:grid; place-items:center; font-weight:800; font-size:23px; box-shadow:0 8px 18px rgba(200,155,60,.22); }
        .eyebrow { margin:0 0 1px; color:var(--g); font-size:10px; line-height:1.2; letter-spacing:.16em; font-weight:800; }
        h2 { margin:0; color:var(--b); font-size:24px; line-height:1.15; letter-spacing:-.02em; }
        .institution { margin:14px 0 22px; color:var(--mu); font-size:12px; line-height:1.5; }
        .role-pin { display:grid; gap:14px; }
        .field { display:grid; gap:6px; }
        .field label { color:var(--b); font-size:12.5px; font-weight:700; }
        .input-wrap { position:relative; display:flex; align-items:center; }
        .input-wrap svg { position:absolute; left:12px; width:18px; height:18px; fill:none; stroke:#7A8C9C; stroke-width:1.7; pointer-events:none; }
        .input-wrap input { width:100%; height:46px; border:1px solid var(--bd); border-radius:10px; background:#FBFCFD; padding:0 76px 0 40px; color:var(--tx); font-size:14px; outline:none; transition:border-color .18s,box-shadow .18s,background .18s; }
        .input-wrap input::placeholder { color:#9AA7B3; }
        .input-wrap input:focus { border-color:#78A1BF; background:#fff; box-shadow:0 0 0 4px rgba(29,111,165,.10); }
        .visibility { position:absolute; right:8px; border:0; background:transparent; color:var(--b); font-size:12px; font-weight:700; padding:8px; cursor:pointer; }
        .btn { display:flex; width:100%; min-height:46px; align-items:center; justify-content:center; border:1px solid var(--b); background:var(--b); color:#fff; border-radius:10px; margin-top:2px; font-weight:700; text-align:center; text-decoration:none; box-sizing:border-box; cursor:pointer; box-shadow:0 8px 18px rgba(15,61,99,.16); transition:transform .18s,box-shadow .18s,background .18s; }
        .btn:hover:not(:disabled) { background:#174E77; transform:translateY(-1px); box-shadow:0 10px 22px rgba(15,61,99,.22); }
        .btn:disabled { opacity:.68; cursor:not-allowed; }
        .btn.secondary { margin-top:0; background:#fff; color:var(--b); box-shadow:none; border-color:#C9D6E1; }
        .btn.secondary:hover { background:#F5F8FA; transform:none; }
        .divider { display:flex; align-items:center; gap:10px; margin:18px 0 10px; color:#8A99A7; font-size:11px; text-transform:uppercase; letter-spacing:.08em; }
        .divider::before,.divider::after { content:''; height:1px; flex:1; background:#E6EBF0; }
        .divider span { white-space:nowrap; }
        .security-note { margin:12px 0 0; text-align:center; color:#7A8996; font-size:11.5px; }
        .note { background:#FFF8E6; border:1px solid #EBD9A8; border-radius:8px; padding:8px 12px; font-size:12.5px; margin:14px 0 0; }
        .role-list { display:grid; gap:8px; margin-top:14px; }
        .role-option { width:100%; border:1px solid var(--bd); background:#fff; border-radius:8px; padding:10px; color:var(--b); text-align:left; cursor:pointer; }
        .role-option small { display:block; color:var(--mu); }
        .role-pin label { color:var(--b); font-weight:600; }
        @media(max-width:700px) { .ct { padding:16px; } .lc { padding:24px; border-radius:16px; } h2 { font-size:21px; } }
        @media(prefers-reduced-motion:reduce) { * { animation:none!important; transition:none!important; } }
      `}</style>
    </main>
  );
}
