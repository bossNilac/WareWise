import { Download } from "lucide-react";
import { BarList, DataTable, KpiGrid, Panel } from "../../components/ui";
import type { WareWiseData } from "../../types";
import { LOW_STOCK_THRESHOLD, inventoryValue, warehouseName } from "../../utils";

export function ReportsPage({ data }: { data: WareWiseData }) {
  const fulfilled = data.orders.filter((order) => order.status === "FULFILLED").length;
  const stockUnits = data.inventory.reduce((sum, record) => sum + record.quantity, 0);
  const value = inventoryValue(data.inventory, data.generalItems);
  const lowStock = data.inventory.filter((record) => record.quantity <= LOW_STOCK_THRESHOLD).length;
  const revenue = data.warehouseItems
    .filter((item) => item.sold)
    .reduce((sum, item) => sum + item.quantity * (data.generalItems.find((catalog) => catalog.id === item.generalItemId)?.price ?? 0), 0);
  const byWarehouse = data.warehouses.map((warehouse) => ({
    label: warehouse.name,
    value: data.inventory.filter((record) => record.warehouseId === warehouse.warehouse_id).reduce((sum, record) => sum + record.quantity, 0),
  }));

  return (
    <div className="page-grid">
      <KpiGrid items={[
        ["Total Orders", String(data.orders.length)],
        ["Fulfilled Orders", String(fulfilled)],
        ["Stock Units", String(stockUnits)],
        ["Inventory Value", `$${value.toFixed(2)}`],
        ["Low Stock Records", String(lowStock)],
        ["Sold Revenue", `$${revenue.toFixed(2)}`],
      ]} />
      <Panel title="Inventory by Warehouse" wide><BarList rows={byWarehouse} /></Panel>
      <Panel title="Report Metrics">
        <DataTable rows={[
          ["Total Orders", data.orders.length],
          ["Fulfilled Orders", fulfilled],
          ["Stock Units", stockUnits],
          ["Inventory Value", `$${value.toFixed(2)}`],
          ["Low Stock Records", lowStock],
          ["Sold Revenue", `$${revenue.toFixed(2)}`],
        ]} columns={[["Metric", (row) => row[0]], ["Value", (row) => row[1]]]} />
      </Panel>
      <button className="primary-button" onClick={() => window.print()}><Download size={16} />Export PDF</button>
    </div>
  );
}

export function ReplenishmentPage({ data }: { data: WareWiseData }) {
  const rows = data.inventory
    .filter((record) => record.quantity <= LOW_STOCK_THRESHOLD)
    .map((record) => ({ ...record, suggested: Math.max(0, LOW_STOCK_THRESHOLD * 2 - record.quantity), priority: record.quantity <= 3 ? "High" : "Normal" }));
  return (
    <DataTable rows={rows} columns={[
      ["Item", (row) => row.name],
      ["Warehouse", (row) => warehouseName(data.warehouses, row.warehouseId)],
      ["On Hand", (row) => row.quantity],
      ["Suggested Reorder", (row) => row.suggested],
      ["Priority", (row) => <span className={`priority ${row.priority.toLowerCase()}`}>{row.priority}</span>],
    ]} />
  );
}

export function WarehouseHealthPage({ data }: { data: WareWiseData }) {
  const itemByName = new Map(data.generalItems.map((item) => [item.name.toLowerCase(), item]));
  const rows = data.warehouses.map((warehouse) => {
    const inventory = data.inventory.filter((record) => record.warehouseId === warehouse.warehouse_id);
    const stockUnits = inventory.reduce((sum, record) => sum + record.quantity, 0);
    const lowStock = inventory.filter((record) => record.quantity <= LOW_STOCK_THRESHOLD).length;
    const estimatedValue = inventory.reduce((sum, record) => sum + record.quantity * (itemByName.get(record.name.toLowerCase())?.price ?? 0), 0);
    const managers = data.users.filter((user) => user.role === "MANAGER" && user.warehouseIds?.includes(warehouse.warehouse_id)).map((user) => user.username).join(", ");
    return { warehouse: warehouse.name, stockUnits, lowStock, estimatedValue, managers: managers || "Unassigned" };
  });
  return (
    <section className="section-stack">
      <Panel title="Stock Units by Warehouse"><BarList rows={rows.map((row) => ({ label: row.warehouse, value: row.stockUnits }))} /></Panel>
      <DataTable rows={rows} columns={[
        ["Warehouse", (row) => row.warehouse],
        ["Stock Units", (row) => row.stockUnits],
        ["Low Stock Records", (row) => row.lowStock],
        ["Est. Value", (row) => `$${row.estimatedValue.toFixed(2)}`],
        ["Assigned Managers", (row) => row.managers],
      ]} />
    </section>
  );
}
