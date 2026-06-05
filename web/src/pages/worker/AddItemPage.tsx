import { useState } from "react";
import type { FormEvent } from "react";
import { Save, X } from "lucide-react";
import { endpoints } from "../../api";
import { CheckField, Panel, TextInput } from "../../components/ui";
import { runAction } from "../../lib/notifications";
import type { Notice } from "../../lib/notifications";
import type { Session, WareWiseData } from "../../types";
import { nowIso } from "../../utils";

export default function AddItemPage({
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
  const empty = {
    name: "",
    barcode: "",
    categoryId: "",
    newCategory: "",
    categoryDescription: "",
    supplierId: "",
    price: "",
    setQuantity: "",
    expires: false,
    addToInventory: false,
    warehouseId: "",
    inventoryQuantity: "",
    inventoryDescription: "",
  };
  const [draft, setDraft] = useState(empty);

  const save = async (event: FormEvent) => {
    event.preventDefault();
    await runAction(notify, refresh, async () => {
      let categoryId = Number(draft.categoryId);
      if (draft.newCategory.trim()) {
        await endpoints.categories.add(session.token, { name: draft.newCategory.trim(), description: draft.categoryDescription });
        const categories = await endpoints.categories.list(session.token);
        categoryId = categories.filter((category) => category.name.toLowerCase() === draft.newCategory.trim().toLowerCase()).sort((a, b) => b.ID - a.ID)[0]?.ID;
      }
      await endpoints.generalItems.add(session.token, {
        name: draft.name.trim(),
        setQuantity: Number(draft.setQuantity),
        barcode: draft.barcode.trim(),
        categoryId,
        supplierId: Number(draft.supplierId),
        price: Number(draft.price),
        expires: draft.expires,
      });
      if (draft.addToInventory) {
        await endpoints.inventory.add(session.token, {
          name: draft.name.trim(),
          stockQuantity: Number(draft.inventoryQuantity),
          description: draft.inventoryDescription.trim() || "Created from Add Item",
          lastUpdated: nowIso(),
          warehouseId: Number(draft.warehouseId),
        });
      }
      setDraft(empty);
    });
  };

  return (
    <Panel title="Add Item">
      <form className="form-grid" onSubmit={save}>
        <TextInput label="Name" value={draft.name} onChange={(name) => setDraft({ ...draft, name })} required />
        <TextInput label="Barcode" value={draft.barcode} onChange={(barcode) => setDraft({ ...draft, barcode })} required />
        <label>Category<select value={draft.categoryId} onChange={(event) => setDraft({ ...draft, categoryId: event.target.value })}>
          <option value="">Select category</option>{data.categories.map((category) => <option key={category.ID} value={category.ID}>{category.name}</option>)}
        </select></label>
        <TextInput label="New category" value={draft.newCategory} onChange={(newCategory) => setDraft({ ...draft, newCategory })} />
        <TextInput label="Category description" value={draft.categoryDescription} onChange={(categoryDescription) => setDraft({ ...draft, categoryDescription })} />
        <label>Supplier<select required value={draft.supplierId} onChange={(event) => setDraft({ ...draft, supplierId: event.target.value })}>
          <option value="">Select supplier</option>{data.suppliers.map((supplier) => <option key={supplier.ID} value={supplier.ID}>{supplier.name}</option>)}
        </select></label>
        <TextInput label="Price" type="number" value={draft.price} onChange={(price) => setDraft({ ...draft, price })} required />
        <TextInput label="Set quantity" type="number" value={draft.setQuantity} onChange={(setQuantity) => setDraft({ ...draft, setQuantity })} required />
        <CheckField label="Expires" checked={draft.expires} onChange={(expires) => setDraft({ ...draft, expires })} />
        <CheckField label="Add this item to warehouse inventory now" checked={draft.addToInventory} onChange={(addToInventory) => setDraft({ ...draft, addToInventory })} />
        {draft.addToInventory ? <>
          <label>Warehouse<select required value={draft.warehouseId} onChange={(event) => setDraft({ ...draft, warehouseId: event.target.value })}>
            <option value="">Select warehouse</option>{data.warehouses.map((warehouse) => <option key={warehouse.warehouse_id} value={warehouse.warehouse_id}>{warehouse.name}</option>)}
          </select></label>
          <TextInput label="Inventory quantity" type="number" value={draft.inventoryQuantity} onChange={(inventoryQuantity) => setDraft({ ...draft, inventoryQuantity })} required />
          <label className="full-span">Inventory description<textarea value={draft.inventoryDescription} onChange={(event) => setDraft({ ...draft, inventoryDescription: event.target.value })} /></label>
        </> : null}
        <div className="form-actions">
          <button className="primary-button" type="submit"><Save size={16} />Save Item</button>
          <button className="ghost-button" type="button" onClick={() => setDraft(empty)}><X size={16} />Clear</button>
        </div>
      </form>
    </Panel>
  );
}
