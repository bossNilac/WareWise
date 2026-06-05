import { useState } from "react";
import { DataTable, FilterBar } from "../../components/ui";
import type { WareWiseData } from "../../types";
import { formatDate, warehouseName } from "../../utils";

export default function InventoryPage({ data }: { data: WareWiseData }) {
  const [warehouse, setWarehouse] = useState("all");
  const rows = data.inventory.filter((record) => warehouse === "all" || String(record.warehouseId) === warehouse);
  return (
    <section className="section-stack">
      <FilterBar>
        <select value={warehouse} onChange={(event) => setWarehouse(event.target.value)}>
          <option value="all">All assigned warehouses</option>
          {data.warehouses.map((wh) => <option key={wh.warehouse_id} value={wh.warehouse_id}>{wh.name}</option>)}
        </select>
      </FilterBar>
      <DataTable rows={rows} columns={[
        ["Item", (record) => record.name],
        ["Warehouse", (record) => warehouseName(data.warehouses, record.warehouseId)],
        ["Quantity", (record) => record.quantity],
        ["Description", (record) => record.description],
        ["Last Updated", (record) => formatDate(record.lastUpdated)],
      ]} />
    </section>
  );
}
