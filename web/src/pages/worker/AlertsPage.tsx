import { useMemo, useState } from "react";
import { Trash2 } from "lucide-react";
import { endpoints } from "../../api";
import { DataTable, FilterBar, IconAction, Panel } from "../../components/ui";
import { runAction } from "../../lib/notifications";
import type { Notice } from "../../lib/notifications";
import type { Inventory, Session, StockAlert, WareWiseData } from "../../types";
import { LOW_STOCK_THRESHOLD, formatDate, nowIso, warehouseName } from "../../utils";

export default function AlertsPage({
  session,
  data,
  refresh,
  notify,
}: {
  session: Session;
  data: WareWiseData;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
}) {
  const [filters, setFilters] = useState({ product: "", date: "", resolved: "All" });
  const inventoryById = useMemo(() => new Map(data.inventory.map((record) => [record.ID, record])), [data.inventory]);
  const lowStock = data.inventory.filter((record) => record.quantity <= LOW_STOCK_THRESHOLD);
  const alerts = data.alerts.filter((alert) => {
    const inventory = inventoryById.get(alert.productID);
    return (!filters.product || inventory?.name.toLowerCase().includes(filters.product.toLowerCase()))
      && (!filters.date || alert.createdAt?.startsWith(filters.date))
      && (filters.resolved === "All" || (filters.resolved === "Yes" ? alert.resolved : !alert.resolved));
  });

  const createAlert = async (record: Inventory) => {
    if (data.alerts.some((alert) => alert.productID === record.ID && !alert.resolved)) return;
    await runAction(notify, refresh, () => endpoints.alerts.add(session.token, { productId: record.ID, createdAt: nowIso(), resolved: false }));
  };

  const updateAlert = async (alert: StockAlert, resolved: boolean) => {
    await runAction(notify, refresh, () => endpoints.alerts.update(session.token, { stockAlertId: alert.ID, productId: alert.productID, createdAt: alert.createdAt, resolved }));
  };

  const deleteAlert = async (alert: StockAlert) => {
    if (!window.confirm("Delete Alert?")) return;
    await runAction(notify, refresh, () => endpoints.alerts.delete(session.token, alert.ID));
  };

  return (
    <section className="section-stack">
      <Panel title="Low stock levels">
        <DataTable rows={lowStock} columns={[
          ["Item Name", (record) => record.name],
          ["Quantity", (record) => record.quantity],
          ["Status", (record) => record.quantity === 0 ? "Out of stock" : "Low stock"],
          ["Inventory", (record) => record.name],
          ["Warehouse", (record) => warehouseName(data.warehouses, record.warehouseId)],
          ["", (record) => <button className="ghost-button" onClick={() => createAlert(record)}>Create an Alert</button>],
        ]} />
      </Panel>
      <FilterBar>
        <input value={filters.product} onChange={(event) => setFilters({ ...filters, product: event.target.value })} placeholder="Product name" />
        <input value={filters.date} onChange={(event) => setFilters({ ...filters, date: event.target.value })} placeholder="YYYY-MM-DD" />
        <select value={filters.resolved} onChange={(event) => setFilters({ ...filters, resolved: event.target.value })}><option>All</option><option>Yes</option><option>No</option></select>
      </FilterBar>
      <DataTable rows={alerts} columns={[
        ["Inventory", (alert) => inventoryById.get(alert.productID)?.name ?? `Inventory ${alert.productID}`],
        ["Created At", (alert) => formatDate(alert.createdAt)],
        ["Resolved", (alert) => <input type="checkbox" checked={alert.resolved} onChange={(event) => updateAlert(alert, event.target.checked)} />],
        ["", (alert) => <IconAction title="Delete Alert" icon={Trash2} onClick={() => deleteAlert(alert)} />],
      ]} />
    </section>
  );
}
