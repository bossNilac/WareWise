import { useState } from "react";
import type { FormEvent } from "react";
import { Save } from "lucide-react";
import { endpoints } from "../../api";
import { Panel, TextInput } from "../../components/ui";
import { runAction } from "../../lib/notifications";
import type { Notice } from "../../lib/notifications";
import type { Session, WareWiseData } from "../../types";
import { nowIso, warehouseName } from "../../utils";

export default function StockAdjustmentPage({
  mode,
  session,
  data,
  refresh,
  notify,
}: {
  mode: "receive" | "damage";
  session: Session;
  data: WareWiseData;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
}) {
  const [inventoryId, setInventoryId] = useState("");
  const [quantity, setQuantity] = useState("1");
  const [kind, setKind] = useState("Damage");
  const [note, setNote] = useState("");
  const selected = data.inventory.find((record) => record.ID === Number(inventoryId));

  const apply = async (event: FormEvent) => {
    event.preventDefault();
    if (!selected) return;
    const amount = Number(quantity);
    const newQuantity = mode === "receive" ? selected.quantity + amount : Math.max(0, selected.quantity - amount);
    const suffix = mode === "receive"
      ? (note.trim() ? ` | Received: ${note.trim()}` : "")
      : ` | Adjusted: ${note.trim() ? `${kind}: ${note.trim()}` : kind}`;
    await runAction(notify, refresh, () => endpoints.inventory.update(session.token, {
      inventoryId: selected.ID,
      name: selected.name,
      description: `${selected.description || ""}${suffix}`,
      stockQuantity: newQuantity,
      lastUpdated: nowIso(),
      warehouseId: selected.warehouseId,
    }));
    setNote("");
  };

  return (
    <Panel title={mode === "receive" ? "Receive Shipment" : "Adjust Damage"}>
      <form className="form-grid" onSubmit={apply}>
        <label className="full-span">Inventory
          <select required value={inventoryId} onChange={(event) => setInventoryId(event.target.value)}>
            <option value="">Select inventory</option>
            {data.inventory.map((record) => <option key={record.ID} value={record.ID}>{record.name} | {warehouseName(data.warehouses, record.warehouseId)} | Qty {record.quantity}</option>)}
          </select>
        </label>
        {mode === "damage" ? <label>Adjustment Type<select value={kind} onChange={(event) => setKind(event.target.value)}><option>Damage</option><option>Shrinkage</option><option>Correction</option></select></label> : null}
        <TextInput label="Quantity" type="number" value={quantity} onChange={setQuantity} required />
        <label className="full-span">{mode === "receive" ? "Note" : "Reason"}<textarea value={note} onChange={(event) => setNote(event.target.value)} /></label>
        <div className="form-actions"><button className="primary-button" type="submit"><Save size={16} />{mode === "receive" ? "Receive Shipment" : "Apply Adjustment"}</button></div>
      </form>
    </Panel>
  );
}
