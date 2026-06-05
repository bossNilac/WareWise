import type {
  ApiResponse,
  Category,
  GeneralItem,
  Inventory,
  Log,
  Order,
  Session,
  StockAlert,
  Supplier,
  User,
  UserRole,
  Warehouse,
  WarehouseItem,
  WareWiseData,
} from "./types";

const API_BASE = import.meta.env.VITE_WAREWISE_API_URL ?? "http://localhost:8080/api";

type Method = "GET" | "POST" | "PATCH" | "DELETE";

async function request<T>(path: string, method: Method, token?: string, body?: unknown): Promise<ApiResponse<T>> {
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body == null ? undefined : JSON.stringify(body),
  });

  const text = await response.text();
  const parsed = text ? JSON.parse(text) : {};
  if ("error" in parsed) {
    throw new Error(parsed.error);
  }
  const apiResponse = parsed as ApiResponse<T>;
  if (!response.ok || apiResponse.success === false) {
    throw new Error(apiResponse.message || response.statusText);
  }
  if (typeof apiResponse.data === "string") {
    apiResponse.data = (apiResponse.data ? JSON.parse(apiResponse.data) : []) as T;
  }
  return apiResponse;
}

export async function pingServer(): Promise<boolean> {
  try {
    await fetch(`${API_BASE}/status`, { method: "GET" });
    return true;
  } catch {
    return false;
  }
}

export async function login(username: string, password: string): Promise<Session> {
  const resp = await request<[string, UserRole, number]>("/auth/login", "POST", undefined, { username, password });
  return { token: resp.data[0], role: resp.data[1], userId: Number(resp.data[2]), username };
}

export async function logout(token: string): Promise<void> {
  await request<void>("/auth/logout", "GET", token);
}

export const endpoints = {
  users: {
    list: (token: string) => request<User[]>("/users/get_users", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/users/add_user", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/users/update_user", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/users/delete_user/${id}`, "DELETE", token),
    resetPassword: (token: string, body: unknown) => request<void>("/auth/reset_password", "PATCH", token, body),
  },
  categories: {
    list: (token: string) => request<Category[]>("/categories/get_categories", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/categories/add_category", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/categories/update_category", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/categories/delete_category/${id}`, "DELETE", token),
  },
  generalItems: {
    list: (token: string) => request<GeneralItem[]>("/general_items/get_items", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/general_items/add_item", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/general_items/update_item", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/general_items/delete_item/${id}`, "DELETE", token),
  },
  inventory: {
    list: (token: string) => request<Inventory[]>("/inventory/get_inventory", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/inventory/add_inventory", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/inventory/update_inventory", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/inventory/delete_inventory/${id}`, "DELETE", token),
  },
  orders: {
    list: (token: string) => request<Order[]>("/orders/get_orders", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/orders/add_order", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/orders/update_order", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/orders/delete_order/${id}`, "DELETE", token),
  },
  suppliers: {
    list: (token: string) => request<Supplier[]>("/suppliers/get_suppliers", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/suppliers/add_supplier", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/suppliers/update_supplier", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/suppliers/delete_supplier/${id}`, "DELETE", token),
  },
  alerts: {
    list: (token: string) => request<StockAlert[]>("/stock_alerts/get_stock_alerts", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/stock_alerts/add_stock_alert", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/stock_alerts/update_stock_alert", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/stock_alerts/delete_stock_alert/${id}`, "DELETE", token),
  },
  warehouses: {
    list: (token: string) => request<Warehouse[]>("/warehouses/get_warehouses", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/warehouses/add_warehouse", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/warehouses/update_warehouse", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/warehouses/delete_warehouse/${id}`, "DELETE", token),
  },
  warehouseItems: {
    list: (token: string) => request<WarehouseItem[]>("/items/get_items", "GET", token).then((r) => r.data),
    add: (token: string, body: unknown) => request<void>("/items/add_item", "POST", token, body),
    update: (token: string, body: unknown) => request<void>("/items/update_item", "PATCH", token, body),
    delete: (token: string, id: number) => request<void>(`/items/delete_item/${id}`, "DELETE", token),
  },
  logs: {
    list: (token: string) => request<Log[]>("/logs/get_logs", "GET", token).then((r) => r.data),
  },
};

export async function loadWareWiseData(token: string): Promise<WareWiseData> {
  const [users, categories, generalItems, inventory, orders, suppliers, alerts, warehouses, warehouseItems, logs] =
    await Promise.all([
      endpoints.users.list(token),
      endpoints.categories.list(token),
      endpoints.generalItems.list(token),
      endpoints.inventory.list(token),
      endpoints.orders.list(token),
      endpoints.suppliers.list(token),
      endpoints.alerts.list(token),
      endpoints.warehouses.list(token),
      endpoints.warehouseItems.list(token),
      endpoints.logs.list(token),
    ]);
  return { users, categories, generalItems, inventory, orders, suppliers, alerts, warehouses, warehouseItems, logs };
}
