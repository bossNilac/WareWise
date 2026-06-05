import {
  BarChart3,
  Bell,
  Boxes,
  ClipboardList,
  HeartPulse,
  LayoutDashboard,
  PackagePlus,
  RefreshCw,
  Settings,
  Shield,
  ShoppingCart,
  Truck,
  Wrench,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import type { UserRole } from "../types";

export type PageKey =
  | "dashboard"
  | "orders"
  | "inventory"
  | "add-item"
  | "receive"
  | "damage"
  | "alerts"
  | "categories"
  | "catalog"
  | "suppliers"
  | "reports"
  | "replenishment"
  | "warehouse-health"
  | "settings"
  | "admin";

export type NavItem = { key: PageKey; label: string; icon: LucideIcon };

export const navByRole: Record<UserRole, NavItem[]> = {
  ADMIN: [
    { key: "admin", label: "Admin Console", icon: Shield },
    { key: "dashboard", label: "Dashboard", icon: LayoutDashboard },
    { key: "settings", label: "Settings", icon: Settings },
  ],
  MANAGER: [
    { key: "dashboard", label: "Dashboard", icon: LayoutDashboard },
    { key: "orders", label: "Orders", icon: ShoppingCart },
    { key: "inventory", label: "Inventory", icon: Boxes },
    { key: "categories", label: "Categories", icon: ClipboardList },
    { key: "catalog", label: "Items", icon: PackagePlus },
    { key: "suppliers", label: "Suppliers", icon: Truck },
    { key: "alerts", label: "Alerts & Tasks", icon: Bell },
    { key: "reports", label: "Reports & Analytics", icon: BarChart3 },
    { key: "replenishment", label: "Replenishment", icon: RefreshCw },
    { key: "warehouse-health", label: "Warehouse Health", icon: HeartPulse },
    { key: "settings", label: "Settings", icon: Settings },
  ],
  WORKER: [
    { key: "dashboard", label: "Dashboard", icon: LayoutDashboard },
    { key: "orders", label: "Orders", icon: ShoppingCart },
    { key: "inventory", label: "Update Inventory", icon: Boxes },
    { key: "receive", label: "Receive Shipments", icon: Truck },
    { key: "add-item", label: "Add Item", icon: PackagePlus },
    { key: "damage", label: "Adjust Damage", icon: Wrench },
    { key: "alerts", label: "Alerts", icon: Bell },
    { key: "settings", label: "Settings", icon: Settings },
  ],
};

export function pageTitle(page: PageKey) {
  return Object.values(navByRole).flat().find((item) => item.key === page)?.label ?? "WareWise";
}
