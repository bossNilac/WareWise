import { useState } from "react";
import { CheckField, Panel } from "../components/ui";
import { sessionKey } from "../hooks/useSession";
import type { Session } from "../types";

export default function SettingsPage({ session }: { session: Session }) {
  const [darkMode, setDarkMode] = useState(true);
  const [refreshRate, setRefreshRate] = useState("30s");
  const [logoutTime, setLogoutTime] = useState("Never");
  const [remember, setRemember] = useState(Boolean(localStorage.getItem(sessionKey)));
  return (
    <Panel title="Settings">
      <div className="settings-grid">
        <CheckField label="Dark Mode" checked={darkMode} onChange={setDarkMode} />
        <label>Data Refresh Rate<select value={refreshRate} onChange={(event) => setRefreshRate(event.target.value)}><option>15s</option><option>30s</option><option>1m</option><option>5m</option></select></label>
        <label>Auto Logout Timer<select value={logoutTime} onChange={(event) => setLogoutTime(event.target.value)}><option>Never</option><option>15m</option><option>30m</option><option>1h</option></select></label>
        <CheckField label="Remember Me" checked={remember} onChange={setRemember} />
        <div className="muted">Signed in as {session.username}</div>
      </div>
    </Panel>
  );
}
