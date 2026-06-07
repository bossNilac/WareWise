import { Monitor, Moon, Sun } from "lucide-react";
import { useState } from "react";
import { CheckField, Panel } from "../components/ui";
import type { ThemeMode } from "../hooks/useTheme";
import type { Session } from "../types";

export default function SettingsPage({
  session,
  theme,
  setTheme,
  remember,
  setRemember,
}: {
  session: Session;
  theme: ThemeMode;
  setTheme: (theme: ThemeMode) => void;
  remember: boolean;
  setRemember: (remember: boolean) => void;
}) {
  const [refreshRate, setRefreshRate] = useState("30s");
  const [logoutTime, setLogoutTime] = useState("Never");
  return (
    <Panel title="Settings">
      <div className="settings-page">
        <div className="settings-hero">
          <div>
            <span className="eyebrow">Workspace</span>
            <h2>Display and session preferences</h2>
            <p>Signed in as {session.username}</p>
          </div>
          <Monitor size={34} />
        </div>
        <div className="settings-grid">
          <section className="settings-block">
            <div>
              <h3>Theme Mode</h3>
              <p>Switch between the JavaFX-inspired dark workspace and a lighter operating view.</p>
            </div>
            <div className="segmented-control" role="group" aria-label="Theme Mode">
              <button className={theme === "dark" ? "selected" : ""} onClick={() => setTheme("dark")} type="button">
                <Moon size={16} />
                Dark
              </button>
              <button className={theme === "light" ? "selected" : ""} onClick={() => setTheme("light")} type="button">
                <Sun size={16} />
                Light
              </button>
            </div>
          </section>
          <section className="settings-block">
            <div>
              <h3>Session</h3>
              <p>Remember Me stores your token locally. Turning it off keeps the session only until this browser session ends.</p>
            </div>
            <CheckField label="Remember Me" checked={remember} onChange={setRemember} />
          </section>
          <section className="settings-block">
            <label>Data Refresh Rate<select value={refreshRate} onChange={(event) => setRefreshRate(event.target.value)}><option>15s</option><option>30s</option><option>1m</option><option>5m</option></select></label>
          </section>
          <section className="settings-block">
            <label>Auto Logout Timer<select value={logoutTime} onChange={(event) => setLogoutTime(event.target.value)}><option>Never</option><option>15m</option><option>30m</option><option>1h</option></select></label>
          </section>
        </div>
      </div>
    </Panel>
  );
}
