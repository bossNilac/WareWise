export type UserRole = "ADMIN" | "MANAGER" | "WORKER";
export type OrderStatus = "PENDING" | "FULFILLED" | "CANCELLED";

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Session {
  token: string;
  role: UserRole;
  userId: number;
  username: string;
}

export interface Category {
  ID: number;
  name: string;
  description: string;
}

export interface GeneralItem {
  id: number;
  name: string;
  setQuantity: number;
  barcode: string;
  categoryId: number;
  price: number;
  supplierId: number;
  expires: boolean;
}

export interface Inventory {
  ID: number;
  name: string;
  description: string;
  quantity: number;
  lastUpdated: string;
  warehouseId: number;
}

export interface WarehouseItem {
  ID: number;
  orderID: number;
  inventoryID: number;
  quantity: number;
  generalItemId: number;
  expireDate: string;
  sold: boolean;
}

export interface Order {
  ID: number;
  general_item_id: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  userId: number;
  quantity: number;
}

export interface Supplier {
  ID: number;
  name: string;
  contactEmail: string;
  contactPhoneNo: string;
  address: string;
  createdAt: string;
}

export interface StockAlert {
  ID: number;
  productID: number;
  createdAt: string;
  resolved: boolean;
}

export interface User {
  ID: number;
  username: string;
  role: UserRole;
  email: string;
  createdAt: string;
  warehouseIds: number[];
}

export interface Warehouse {
  warehouse_id: number;
  name: string;
  address: string;
}

export interface Log {
  ID: number;
  username: string | null;
  action: string | null;
  description: string;
  createdAt: string;
}

export interface WareWiseData {
  users: User[];
  categories: Category[];
  generalItems: GeneralItem[];
  inventory: Inventory[];
  orders: Order[];
  suppliers: Supplier[];
  alerts: StockAlert[];
  warehouses: Warehouse[];
  warehouseItems: WarehouseItem[];
  logs: Log[];
}
