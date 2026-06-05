import type { GeneralItem, Inventory, Log, User, Warehouse, WareWiseData } from "./types";

export const LOW_STOCK_THRESHOLD = 10;

export const emptyData: WareWiseData = {
  users: [],
  categories: [],
  generalItems: [],
  inventory: [],
  orders: [],
  suppliers: [],
  alerts: [],
  warehouses: [],
  warehouseItems: [],
  logs: [],
};

export function idMap<T>(items: T[], idOf: (item: T) => number): Map<number, T> {
  return new Map(items.map((item) => [idOf(item), item]));
}

export function warehouseName(warehouses: Warehouse[], id: number): string {
  return idMap(warehouses, (warehouse) => warehouse.warehouse_id).get(id)?.name ?? `Warehouse ${id}`;
}

export function itemName(items: GeneralItem[], id: number): string {
  return idMap(items, (item) => item.id).get(id)?.name ?? `Item ${id}`;
}

export function userName(users: User[], id: number): string {
  return idMap(users, (user) => user.ID).get(id)?.username ?? `User ${id}`;
}

export function parseDate(value: string | null | undefined): Date | null {
  if (!value) return null;
  const normalized = value.includes(" ") && !value.includes("T") ? value.replace(" ", "T") : value;
  const date = new Date(normalized);
  return Number.isNaN(date.getTime()) ? null : date;
}

export function formatDate(value: string | null | undefined): string {
  const parsed = parseDate(value);
  return parsed ? parsed.toLocaleString() : value || "";
}

export function nowIso(): string {
  return new Date().toISOString();
}

export function inventoryValue(inventory: Inventory[], items: GeneralItem[]): number {
  const itemByName = new Map(items.map((item) => [item.name.toLowerCase(), item]));
  return inventory.reduce((sum, record) => {
    const item = itemByName.get(record.name.toLowerCase());
    return sum + record.quantity * (item?.price ?? 0);
  }, 0);
}

export function recentActions(logs: Log[], username: string, count = 6): string[] {
  return logs
    .filter((log) => log.action && log.action !== "GET" && log.username !== username)
    .slice(-count)
    .reverse()
    .map((log) => `${log.username || "System"} ${log.description} at ${formatDate(log.createdAt)}`);
}
