import { useEffect, useState } from "react";
import { loadWareWiseData } from "../api";
import type { Notice } from "../lib/notifications";
import type { Session, WareWiseData } from "../types";
import { emptyData } from "../utils";

export function useWareWiseData(session: Session | null, notify: (notice: Notice) => void) {
  const [data, setData] = useState<WareWiseData>(emptyData);
  const [loading, setLoading] = useState(false);

  const refresh = async () => {
    if (!session) return;
    setLoading(true);
    try {
      setData(await loadWareWiseData(session.token));
    } catch (error) {
      notify({ tone: "error", text: error instanceof Error ? error.message : "Failed to load data" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refresh();
  }, [session?.token]);

  return { data, loading, refresh };
}
