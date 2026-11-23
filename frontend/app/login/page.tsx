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

  useEffect(() => {
    fetch(`${apiBase}/api/v1/auth/session`, { credentials: "include" })
      .then((response) => response.json())
      .then((body) => {
        setAuthenticated(Boolean(body.data?.authenticated));
        setRoles(body.data?.roles ?? []);
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
    window.location.href = "/dashboard/overview";
  }

  return (
    <main className="ct">
      <section className="lc" aria-label="CIABOC sign in">
        <div className="logo">C</div>
        <h2>CIABOC Management Intelligence</h2>
        <p className="sub">Commission to Investigate Allegations of Bribery or Corruption · Sri Lanka</p>

        {!authenticated ? (
          <>
            <a className="btn" href={`${apiBase}/oauth2/authorization/ciaboc`}>
              Sign in with CIABOC Account
            </a>
            <p className="sub sso-note">
              Single sign-on with multi-factor authentication. Access is limited to authorized officers and every session is logged.
            </p>
            <p><a href="#" onClick={(event) => event.preventDefault()}>Forgot password?</a></p>
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
        :root { --b:#0F3D63; --g:#C89B3C; --t:#1D6FA5; --bg:#F5F7FA; --tx:#2D3748; --bd:#E2E8F0; --mu:#5B6B7F; }
        .ct { display:grid; place-items:center; min-height:100vh; background:linear-gradient(#0F3D63 0 38%,#F5F7FA 38%); font:14px/1.45 'Source Sans 3','Noto Sans Sinhala','Noto Sans Tamil',system-ui,sans-serif; color:var(--tx); }
        .lc { background:#fff; border-radius:16px; padding:32px; width:min(420px,92vw); box-shadow:0 12px 40px #0f3d6333; border-top:4px solid var(--g); }
        .logo { width:52px; height:52px; border-radius:9px; background:var(--g); color:var(--b); display:grid; place-items:center; font-weight:700; font-size:24px; }
        h2 { margin:14px 0 4px; color:var(--b); }
        .sub { font-size:12px; color:var(--mu); }
        .sso-note { margin-top:12px; }
        .btn { display:block; width:100%; border:1px solid var(--b); background:var(--b); color:#fff; border-radius:8px; padding:11px 14px; margin-top:14px; font-weight:600; text-align:center; text-decoration:none; box-sizing:border-box; cursor:pointer; }
        .note { background:#FFF8E6; border:1px solid #EBD9A8; border-radius:8px; padding:8px 12px; font-size:12.5px; margin:14px 0 0; }
        .role-list { display:grid; gap:8px; margin-top:14px; }
        .role-option { width:100%; border:1px solid var(--bd); background:#fff; border-radius:8px; padding:10px; color:var(--b); text-align:left; cursor:pointer; }
        .role-option small { display:block; color:var(--mu); }
        .role-pin { display:grid; gap:8px; margin-top:14px; }
        .role-pin label { color:var(--b); font-weight:600; }
        .srch { width:100%; border:1px solid var(--bd); border-radius:8px; padding:9px 10px; box-sizing:border-box; letter-spacing:.25em; }
        a { color:var(--t); }
        @media(max-width:700px) { .lc { padding:24px; } }
        @media(prefers-reduced-motion:reduce) { * { animation:none!important; transition:none!important; } }
      `}</style>
    </main>
  );
}
