import { LogOut, Menu, RefreshCw } from "lucide-react";
import type { ReactNode } from "react";
import { useState } from "react";
import { navByRole, pageTitle, type PageKey } from "../lib/navigation";
import type { Session } from "../types";

export function AppShell({
  session,
  activePage,
  loading,
  children,
  onNavigate,
  onRefresh,
  onLogout,
}: {
  session: Session;
  activePage: PageKey;
  loading: boolean;
  children: ReactNode;
  onNavigate: (page: PageKey) => void;
  onRefresh: () => void;
  onLogout: () => void;
}) {
  const [menuOpen, setMenuOpen] = useState(false);
  return (
    <div className="app-shell">
      <aside className={`sidebar ${menuOpen ? "open" : ""}`}>
        <div className="brand">
          <img src="/logo.png" alt="WareWise" />
          <div><strong>WareWise</strong><span>{session.role.toLowerCase()} workflow</span></div>
        </div>
        <nav>
          {navByRole[session.role].map(({ key, label, icon: Icon }) => (
            <button key={key} className={activePage === key ? "selected" : ""} onClick={() => { onNavigate(key); setMenuOpen(false); }}>
              <Icon size={18} />{label}
            </button>
          ))}
        </nav>
      </aside>
      <main className="workspace">
        <header className="topbar">
          <button className="icon-button mobile-only" onClick={() => setMenuOpen((open) => !open)} title="Menu"><Menu size={20} /></button>
          <div><h1>{pageTitle(activePage)}</h1><p>Connected as <strong>{session.username}</strong></p></div>
          <div className="topbar-actions">
            {loading ? <span className="status-pill">Refreshing</span> : null}
            <button className="ghost-button" onClick={onRefresh}><RefreshCw size={16} />Refresh</button>
            <button className="ghost-button" onClick={onLogout}><LogOut size={16} />Log out</button>
          </div>
        </header>
        {children}
      </main>
    </div>
  );
}
