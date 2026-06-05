export type Notice = { tone: "success" | "error" | "warning" | "info"; text: string } | null;

export async function runAction(
  notify: (notice: Notice) => void,
  refresh: () => Promise<void>,
  action: () => Promise<unknown>,
) {
  try {
    await action();
    notify({ tone: "success", text: "Operation completed" });
    await refresh();
  } catch (error) {
    notify({ tone: "error", text: error instanceof Error ? error.message : "Unsuccessful operation" });
  }
}
