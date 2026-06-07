import { useState } from "react";
import type { Session } from "../types";

export const sessionKey = "warewise.web.session";
export const rememberKey = "warewise.web.remember";
const sessionOnlyKey = "warewise.web.sessionOnly";

function readRemember() {
  return localStorage.getItem(rememberKey) !== "false";
}

function readSession() {
  const raw = localStorage.getItem(sessionKey) ?? sessionStorage.getItem(sessionOnlyKey);
  return raw ? (JSON.parse(raw) as Session) : null;
}

export function useSession() {
  const [remember, setRememberState] = useState(readRemember);
  const [session, setSessionState] = useState<Session | null>(readSession);

  const setSession = (next: Session | null, shouldRemember = remember) => {
    setSessionState(next);
    localStorage.removeItem(sessionKey);
    sessionStorage.removeItem(sessionOnlyKey);
    if (!next) return;
    const storage = shouldRemember ? localStorage : sessionStorage;
    storage.setItem(shouldRemember ? sessionKey : sessionOnlyKey, JSON.stringify(next));
  };

  const setRemember = (next: boolean) => {
    setRememberState(next);
    localStorage.setItem(rememberKey, String(next));
    if (session) {
      setSession(session, next);
    }
  };

  return { session, setSession, remember, setRemember } as const;
}
