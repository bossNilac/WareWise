import { useState } from "react";
import type { Session } from "../types";

export const sessionKey = "warewise.web.session";

export function useSession() {
  const [session, setSessionState] = useState<Session | null>(() => {
    const raw = localStorage.getItem(sessionKey);
    return raw ? (JSON.parse(raw) as Session) : null;
  });

  const setSession = (next: Session | null) => {
    setSessionState(next);
    if (next) localStorage.setItem(sessionKey, JSON.stringify(next));
    else localStorage.removeItem(sessionKey);
  };

  return [session, setSession] as const;
}
