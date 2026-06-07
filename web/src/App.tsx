import { useEffect, useState } from "react";
import { logout } from "./api";
import { AppShell } from "./components/AppShell";
import { Toast } from "./components/ui";
import { useSession } from "./hooks/useSession";
import { useTheme } from "./hooks/useTheme";
import { useWareWiseData } from "./hooks/useWareWiseData";
import type { PageKey } from "./lib/navigation";
import type { Notice } from "./lib/notifications";
import LoginPage from "./pages/LoginPage";
import PageRouter from "./pages/PageRouter";

export default function App() {
  const { session, setSession, remember, setRemember } = useSession();
  const { theme, setTheme } = useTheme();
  const [activePage, setActivePage] = useState<PageKey>(session?.role === "ADMIN" ? "admin" : "dashboard");
  const [notice, setNotice] = useState<Notice>(null);
  const { data, loading, refresh } = useWareWiseData(session, setNotice);

  useEffect(() => {
    if (!notice) return;
    const timer = window.setTimeout(() => setNotice(null), 4500);
    return () => window.clearTimeout(timer);
  }, [notice]);

  if (!session) {
    return <LoginPage onLogin={setSession} notify={setNotice} remember={remember} onRememberChange={setRemember} />;
  }

  const handleLogout = async () => {
    try {
      await logout(session.token);
    } finally {
      setSession(null);
      setActivePage("dashboard");
    }
  };

  return (
    <>
      <AppShell
        session={session}
        activePage={activePage}
        loading={loading}
        onNavigate={setActivePage}
        onRefresh={refresh}
        onLogout={handleLogout}
      >
        <PageRouter
          page={activePage}
          session={session}
          data={data}
          refresh={refresh}
          notify={setNotice}
          goTo={setActivePage}
          theme={theme}
          setTheme={setTheme}
          remember={remember}
          setRemember={setRemember}
        />
      </AppShell>
      <Toast notice={notice} onClose={() => setNotice(null)} />
    </>
  );
}
