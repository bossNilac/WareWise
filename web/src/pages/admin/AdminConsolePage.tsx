import { useState } from "react";
import { endpoints } from "../../api";
import { DataTable, KpiGrid } from "../../components/ui";
import type { Notice } from "../../lib/notifications";
import type { Inventory, Session, User, Warehouse, WareWiseData } from "../../types";
import { formatDate, nowIso, warehouseName } from "../../utils";
import { CatalogPage, CategoriesPage, CrudPanel, SuppliersPage } from "../manager/CrudPages";
import AlertsPage from "../worker/AlertsPage";
import OrdersPage from "../worker/OrdersPage";

type Props = {
  session: Session;
  data: WareWiseData;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
};

export default function AdminConsolePage(props: Props) {
  const [tab, setTab] = useState("Users");
  const tabs = ["Users", "Warehouse", "Logs", "Category", "Inventory", "General Items", "Suppliers", "Orders", "Alerts"];
  return (
    <section className="section-stack">
      <KpiGrid items={[["Users", props.data.users.length.toString()], ["Warehouses", props.data.warehouses.length.toString()]]} />
      <div className="tabbar">{tabs.map((name) => <button key={name} className={tab === name ? "selected" : ""} onClick={() => setTab(name)}>{name}</button>)}</div>
      {tab === "Users" ? <UsersCrud {...props} /> : null}
      {tab === "Warehouse" ? <WarehousesCrud {...props} /> : null}
      {tab === "Logs" ? <LogsTable data={props.data} /> : null}
      {tab === "Category" ? <CategoriesPage {...props} /> : null}
      {tab === "Inventory" ? <InventoryCrud {...props} /> : null}
      {tab === "General Items" ? <CatalogPage {...props} /> : null}
      {tab === "Suppliers" ? <SuppliersPage {...props} /> : null}
      {tab === "Orders" ? <OrdersPage {...props} /> : null}
      {tab === "Alerts" ? <AlertsPage {...props} /> : null}
    </section>
  );
}

function UsersCrud(props: Props) {
  return (
    <CrudPanel<User & { password?: string }>
      title="Users"
      rows={props.data.users}
      fields={[
        { key: "username", label: "Username" },
        { key: "email", label: "Email" },
        { key: "role", label: "Role", type: "select", options: [["ADMIN", "ADMIN"], ["MANAGER", "MANAGER"], ["WORKER", "WORKER"]] },
        { key: "warehouseIds", label: "Warehouse IDs" },
        { key: "password", label: "Password" },
      ]}
      columns={[
        ["Username", (row) => row.username],
        ["Email", (row) => row.email],
        ["Role", (row) => row.role],
        ["Warehouses", (row) => row.warehouseIds?.join(", ") || ""],
        ["Created At", (row) => formatDate(row.createdAt)],
      ]}
      blank={{ ID: 0, username: "", email: "", role: "WORKER", createdAt: nowIso(), warehouseIds: [], password: "" }}
      toAdd={(row) => ({ username: row.username, email: row.email, role: row.role, password: row.password, warehouseIds: csvNumbers(row.warehouseIds) })}
      toUpdate={(row) => ({ userId: row.ID, username: row.username, email: row.email, role: row.role, ...(row.password ? { password: row.password } : {}), warehouseIds: csvNumbers(row.warehouseIds) })}
      add={(body) => endpoints.users.add(props.session.token, body)}
      update={(body) => endpoints.users.update(props.session.token, body)}
      deleteRow={(row) => endpoints.users.delete(props.session.token, row.ID)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

function WarehousesCrud(props: Props) {
  return (
    <CrudPanel<Warehouse>
      title="Warehouse"
      rows={props.data.warehouses}
      fields={[{ key: "name", label: "Name" }, { key: "address", label: "Address" }]}
      columns={[["Name", (row) => row.name], ["Address", (row) => row.address]]}
      blank={{ warehouse_id: 0, name: "", address: "" }}
      toAdd={(row) => ({ name: row.name, address: row.address })}
      toUpdate={(row) => ({ warehouseId: row.warehouse_id, name: row.name, address: row.address })}
      add={(body) => endpoints.warehouses.add(props.session.token, body)}
      update={(body) => endpoints.warehouses.update(props.session.token, body)}
      deleteRow={(row) => endpoints.warehouses.delete(props.session.token, row.warehouse_id)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

function InventoryCrud(props: Props) {
  return (
    <CrudPanel<Inventory>
      title="Inventory"
      rows={props.data.inventory}
      fields={[
        { key: "name", label: "Name" },
        { key: "quantity", label: "Quantity", type: "number" },
        { key: "description", label: "Description" },
        { key: "warehouseId", label: "Warehouse", type: "select", options: props.data.warehouses.map((warehouse) => [warehouse.warehouse_id, warehouse.name]) },
      ]}
      columns={[
        ["Name", (row) => row.name],
        ["Quantity", (row) => row.quantity],
        ["Description", (row) => row.description],
        ["Last Updated", (row) => formatDate(row.lastUpdated)],
        ["Warehouse", (row) => warehouseName(props.data.warehouses, row.warehouseId)],
      ]}
      blank={{ ID: 0, name: "", quantity: 0, description: "", lastUpdated: nowIso(), warehouseId: props.data.warehouses[0]?.warehouse_id ?? 0 }}
      toAdd={(row) => inventoryBody(row)}
      toUpdate={(row) => ({ inventoryId: row.ID, ...inventoryBody(row) })}
      add={(body) => endpoints.inventory.add(props.session.token, body)}
      update={(body) => endpoints.inventory.update(props.session.token, body)}
      deleteRow={(row) => endpoints.inventory.delete(props.session.token, row.ID)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

function LogsTable({ data }: { data: WareWiseData }) {
  return <DataTable rows={data.logs} columns={[
    ["Username", (row) => row.username || "System"],
    ["Action", (row) => row.action || ""],
    ["Description", (row) => row.description],
    ["Created At", (row) => formatDate(row.createdAt)],
  ]} />;
}

function inventoryBody(row: Inventory) {
  return { name: row.name, stockQuantity: Number(row.quantity), description: row.description, lastUpdated: nowIso(), warehouseId: Number(row.warehouseId) };
}

function csvNumbers(value: unknown): number[] {
  if (Array.isArray(value)) return value.map(Number).filter((item) => item > 0);
  return String(value ?? "").split(",").map((item) => Number(item.trim())).filter((item) => item > 0);
}
