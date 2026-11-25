"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState, type FormEvent } from "react";
import type { ApiResponse } from "@/lib/api/client";
import styles from "./login.module.css";

type RoleView = {
  userRoleId: number;
  roleName: string;
  organizationalUnit: string | null;
};

type LoginSession = {
  authenticated: boolean;
  roles: RoleView[];
  temporaryLocalAuth?: boolean;
};

const apiBase = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const signInErrors: Record<string, string> = {
  sso: "CIABOC Account sign-in failed. Please try again.",
  "not-authorized": "This account does not have access to the dashboard.",
  "no-role": "No dashboard roles are assigned to this account.",
};

export default function LoginPage() {
  const router = useRouter();
  const [session, setSession] = useState<LoginSession | null>(null);
  const [selectedRole, setSelectedRole] = useState<RoleView | null>(null);
  const [pin, setPin] = useState("");
  const [error, setError] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    const signInError = new URLSearchParams(window.location.search).get("error");

    async function loadSession() {
      try {
        const response = await fetch(`${apiBase}/api/v1/auth/session`, {
          credentials: "include",
          signal: controller.signal,
        });
        if (response.ok) {
          const body: ApiResponse<LoginSession> = await response.json();
          if (controller.signal.aborted) return;
          if (body.data?.temporaryLocalAuth) {
            router.replace("/dashboard");
            return;
          }
          setSession(body.data);
        }
      } catch {
        // The sign-in form can still retry the connection.
      }

      if (!controller.signal.aborted && signInError && Object.hasOwn(signInErrors, signInError)) {
        setError(signInErrors[signInError]);
      }
    }

    void loadSession();
    return () => controller.abort();
  }, [router]);

  async function submitAuth(
    path: string,
    payload: Record<string, string | number>,
    failureMessage: string,
    onSuccess: () => void,
  ) {
    setError("");
    setSubmitting(true);
    try {
      const response = await fetch(`${apiBase}/api/v1/auth/${path}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      const body: ApiResponse<unknown> = await response.json();
      if (response.ok && body.success) {
        onSuccess();
      } else {
        setError(body.error || failureMessage);
      }
    } catch {
      setError("The sign-in service is unavailable. Please try again.");
    } finally {
      setSubmitting(false);
    }
  }

  function loginLocally(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    return submitAuth("local/login", { email, password }, "Sign-in failed.", () => {
      router.replace("/dashboard");
    });
  }

  function selectRole(role: RoleView) {
    return submitAuth("role/select", { userRoleId: role.userRoleId, pin: "" }, "Role selection failed.", () => {
      setSelectedRole(role);
      setPin("");
    });
  }

  function verifyPin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedRole) return;
    return submitAuth("role/verify", { userRoleId: selectedRole.userRoleId, pin }, "PIN verification failed.", () => {
      router.replace("/dashboard");
    });
  }

  const authenticated = session?.authenticated ?? false;
  const heading = selectedRole ? "Verify your PIN" : authenticated ? "Choose a role" : "Sign in";
  const instructions = selectedRole
    ? `Enter the eight-digit PIN for ${selectedRole.roleName}.`
    : authenticated
      ? "Select the role you want to use for this session."
      : "Use your work email and password.";

  return (
    <main className={styles.page} data-theme="ciaboc-login">
      <section className={styles.hero} aria-label="CIABOC Management Dashboard">
        <div className={styles.brand}>
          <span className={styles.mark} aria-hidden="true">C</span>
          <p>
            <strong>CIABOC</strong>
            <span>Sri Lanka</span>
          </p>
        </div>
        <div className={styles.heroCopy}>
          <p className={styles.product}>Management<br className={styles.titleBreak} /> Dashboard</p>
          <p className={styles.institution}>
            Commission to Investigate Allegations of Bribery or Corruption
          </p>
        </div>
      </section>

      <section className={styles.signIn} aria-labelledby="sign-in-heading">
        <div className={styles.panel}>
          <header className={styles.heading}>
            <h1 id="sign-in-heading">{heading}</h1>
            <p>{instructions}</p>
          </header>

          {error && (
            <p className={`alert alert-error alert-soft ${styles.alert}`} role="alert">{error}</p>
          )}

          {!authenticated ? (
            <>
              <form className={styles.form} onSubmit={loginLocally} aria-busy={submitting}>
                <div className={styles.field}>
                  <label htmlFor="email">Email address</label>
                  <div className={`input ${styles.control}`}>
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <rect x="3.5" y="5.5" width="17" height="13" rx="2" />
                      <path d="m4.5 7 7.5 6 7.5-6" />
                    </svg>
                    <input
                      id="email"
                      name="email"
                      type="email"
                      autoComplete="username"
                      placeholder="name@ciaboc.gov.lk"
                      value={email}
                      onChange={(event) => setEmail(event.target.value)}
                      required
                    />
                  </div>
                </div>
                <div className={styles.field}>
                  <label htmlFor="password">Password</label>
                  <div className={`input ${styles.control}`}>
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <rect x="5" y="10" width="14" height="10" rx="2" />
                      <path d="M8 10V7a4 4 0 0 1 8 0v3" />
                    </svg>
                    <input
                      id="password"
                      name="password"
                      type={showPassword ? "text" : "password"}
                      autoComplete="current-password"
                      placeholder="Enter your password"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      required
                    />
                    <button
                      className={`btn btn-ghost ${styles.visibility}`}
                      type="button"
                      aria-controls="password"
                      aria-label={showPassword ? "Hide password" : "Show password"}
                      onClick={() => setShowPassword((visible) => !visible)}
                    >
                      {showPassword ? "Hide" : "Show"}
                    </button>
                  </div>
                </div>
                <button className={`btn btn-primary ${styles.action}`} type="submit" disabled={submitting}>
                  {submitting && <span className="loading loading-spinner loading-sm" aria-hidden="true" />}
                  {submitting ? "Signing in…" : "Sign in"}
                </button>
              </form>

              <div className={`divider ${styles.divider}`}>or</div>
              <a className={`btn btn-outline ${styles.account}`} href={`${apiBase}/oauth2/authorization/ciaboc`}>
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M12 22s7-3.4 7-10V6l-7-3-7 3v6c0 6.6 7 10 7 10Z" />
                  <path d="m9 12 2 2 4-4" />
                </svg>
                Sign in with CIABOC Account
              </a>
              <p className={styles.accessNote}>
                Password sign-in is temporary while CIABOC Account integration is underway.
              </p>
            </>
          ) : selectedRole ? (
            <form className={styles.form} onSubmit={verifyPin} aria-busy={submitting}>
              <div className={styles.field}>
                <label htmlFor="role-pin">Role PIN</label>
                <div className={`input ${styles.control}`}>
                  <input
                    id="role-pin"
                    name="pin"
                    type="password"
                    inputMode="numeric"
                    autoComplete="off"
                    pattern="[0-9]{8}"
                    maxLength={8}
                    value={pin}
                    onChange={(event) => setPin(event.target.value.replace(/\D/g, ""))}
                    required
                  />
                </div>
              </div>
              <button className={`btn btn-primary ${styles.action}`} type="submit" disabled={submitting}>
                {submitting && <span className="loading loading-spinner loading-sm" aria-hidden="true" />}
                {submitting ? "Verifying…" : "Verify PIN"}
              </button>
              <button
                className="btn btn-ghost"
                type="button"
                disabled={submitting}
                onClick={() => {
                  setSelectedRole(null);
                  setError("");
                  setPin("");
                }}
              >
                Back to roles
              </button>
            </form>
          ) : session?.roles.length ? (
            <ul className={styles.roleList} aria-busy={submitting}>
              {session.roles.map((role) => (
                <li key={role.userRoleId}>
                  <button
                    className={`btn btn-outline ${styles.role}`}
                    type="button"
                    disabled={submitting}
                    onClick={() => selectRole(role)}
                  >
                    <span>{role.roleName}</span>
                    {role.organizationalUnit && <small>{role.organizationalUnit}</small>}
                  </button>
                </li>
              ))}
            </ul>
          ) : (
            <p className="alert">No dashboard roles are assigned to this account.</p>
          )}

          <footer className={styles.footer}>Prototype with fictional dashboard data</footer>
        </div>
      </section>
    </main>
  );
}
