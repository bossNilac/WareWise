import { useState } from "react";
import type { FormEvent } from "react";
import { login, pingServer } from "../api";
import type { Notice } from "../lib/notifications";
import type { Session } from "../types";

export default function LoginPage({
  onLogin,
  notify,
}: {
  onLogin: (session: Session) => void;
  notify: (notice: Notice) => void;
}) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    if (!username.trim() || !password) return;
    setSubmitting(true);
    try {
      if (!(await pingServer())) {
        setError("Server offline!");
        return;
      }
      onLogin(await login(username.trim(), password));
    } catch {
      setError("Wrong username or password");
      notify({ tone: "error", text: "Wrong username or password" });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-root">
      <form className="login-card" onSubmit={submit}>
        <img className="login-logo" src="/logo.png" alt="WareWise" />
        <h1>WareWise</h1>
        <p>Sign in to your warehouse workspace</p>
        <label>Username<input value={username} onChange={(event) => setUsername(event.target.value)} placeholder="Username" /></label>
        <label>Password<input value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Password" type="password" /></label>
        <button className="primary-button" disabled={submitting} type="submit">{submitting ? "Signing in" : "Sign in"}</button>
        {error ? <div className="login-error">{error}</div> : null}
      </form>
    </div>
  );
}
