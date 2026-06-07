import { useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { Save, Trash2, X } from "lucide-react";
import { endpoints } from "../../api";
import { DataTable, IconAction, Panel } from "../../components/ui";
import { runAction } from "../../lib/notifications";
import type { Notice } from "../../lib/notifications";
import type { Category, GeneralItem, Session, Supplier, WareWiseData } from "../../types";
import { formatDate, nowIso } from "../../utils";

type CrudProps = {
  session: Session;
  data: WareWiseData;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
};

type FieldConfig<T> = {
  key: keyof T;
  label: string;
  type?: "text" | "number" | "checkbox" | "select";
  options?: Array<[number | string, string]>;
};

export function CrudPanel<T extends object>({
  title,
  rows,
  fields,
  columns,
  blank,
  toAdd,
  toUpdate,
  add,
  update,
  deleteRow,
  refresh,
  notify,
}: {
  title: string;
  rows: T[];
  fields: FieldConfig<T>[];
  columns: Array<[string, (row: T) => ReactNode]>;
  blank: T;
  toAdd: (row: T) => unknown;
  toUpdate: (row: T) => unknown;
  add: (body: unknown) => Promise<unknown>;
  update: (body: unknown) => Promise<unknown>;
  deleteRow: (row: T) => Promise<unknown>;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
}) {
  const [draft, setDraft] = useState<T>(blank);
  const [adding, setAdding] = useState(true);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    await runAction(notify, refresh, async () => {
      if (adding) await add(toAdd(draft));
      else await update(toUpdate(draft));
      setDraft(blank);
      setAdding(true);
    });
  };

  const remove = async (row: T) => {
    if (!window.confirm(`Delete ${title}?`)) return;
    await runAction(notify, refresh, () => deleteRow(row));
  };

  return (
    <section className="section-stack">
      <DataTable rows={rows} columns={[
        ...columns,
        ["", (row) => <div className="row-actions">
          <button className="ghost-button" onClick={() => { setAdding(false); setDraft(row); }}>Edit</button>
          <IconAction title={`Delete ${title}`} icon={Trash2} onClick={() => remove(row)} />
        </div>],
      ]} />
      <Panel title={adding ? `Add New ${title}` : `Save ${title}`}>
        <form className="form-grid compact" onSubmit={submit}>
          {fields.map((field) => <EditorField key={String(field.key)} field={field} draft={draft} setDraft={setDraft} />)}
          <div className="form-actions">
            <button className="primary-button" type="submit"><Save size={16} />{adding ? "Add" : "Save"}</button>
            <button className="ghost-button" type="button" onClick={() => { setDraft(blank); setAdding(true); }}><X size={16} />Clear</button>
          </div>
        </form>
      </Panel>
    </section>
  );
}

function EditorField<T extends object>({
  field,
  draft,
  setDraft,
}: {
  field: FieldConfig<T>;
  draft: T;
  setDraft: (draft: T) => void;
}) {
  const value = draft[field.key];
  if (field.type === "checkbox") {
    return <label className="check-field"><input type="checkbox" checked={Boolean(value)} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.checked } as T)} /><span>{field.label}</span></label>;
  }
  if (field.type === "select") {
    return <label>{field.label}<select value={String(value ?? "")} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value } as T)}>
      <option value="">Select</option>{field.options?.map(([optionValue, label]) => <option key={String(optionValue)} value={optionValue}>{label}</option>)}
    </select></label>;
  }
  return <label>{field.label}<input type={field.type ?? "text"} value={Array.isArray(value) ? value.join(",") : String(value ?? "")} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value } as T)} /></label>;
}

export function CategoriesPage(props: CrudProps) {
  return (
    <CrudPanel<Category>
      title="Categories"
      rows={props.data.categories}
      fields={[{ key: "name", label: "Category Name" }, { key: "description", label: "Description" }]}
      columns={[["Name", (row) => row.name], ["Description", (row) => row.description]]}
      blank={{ ID: 0, name: "", description: "" }}
      toAdd={(row) => ({ name: row.name, description: row.description })}
      toUpdate={(row) => ({ categoryId: row.ID, name: row.name, description: row.description })}
      add={(body) => endpoints.categories.add(props.session.token, body)}
      update={(body) => endpoints.categories.update(props.session.token, body)}
      deleteRow={(row) => endpoints.categories.delete(props.session.token, row.ID)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

export function CatalogPage(props: CrudProps) {
  return (
    <CrudPanel<GeneralItem>
      title="Items"
      rows={props.data.generalItems}
      fields={[
        { key: "name", label: "Name" },
        { key: "barcode", label: "Barcode" },
        { key: "categoryId", label: "Category", type: "select", options: props.data.categories.map((category) => [category.ID, category.name]) },
        { key: "price", label: "Price", type: "number" },
        { key: "setQuantity", label: "Set Quantity", type: "number" },
        { key: "supplierId", label: "Supplier", type: "select", options: props.data.suppliers.map((supplier) => [supplier.ID, supplier.name]) },
        { key: "expires", label: "Expires", type: "checkbox" },
      ]}
      columns={[
        ["Name", (row) => row.name],
        ["Barcode", (row) => row.barcode],
        ["Category Name", (row) => props.data.categories.find((category) => category.ID === Number(row.categoryId))?.name ?? row.categoryId],
        ["Price", (row) => row.price],
        ["Set Quantity", (row) => row.setQuantity],
        ["Supplier", (row) => props.data.suppliers.find((supplier) => supplier.ID === Number(row.supplierId))?.name ?? row.supplierId],
        ["Expires", (row) => row.expires ? "Yes" : "No"],
      ]}
      blank={{ id: 0, name: "", barcode: "", categoryId: 0, price: 0, setQuantity: 0, supplierId: 0, expires: false }}
      toAdd={(row) => itemBody(row)}
      toUpdate={(row) => ({ itemId: row.id, ...itemBody(row) })}
      add={(body) => endpoints.generalItems.add(props.session.token, body)}
      update={(body) => endpoints.generalItems.update(props.session.token, body)}
      deleteRow={(row) => endpoints.generalItems.delete(props.session.token, row.id)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

export function SuppliersPage(props: CrudProps) {
  return (
    <CrudPanel<Supplier>
      title="Suppliers"
      rows={props.data.suppliers}
      fields={[
        { key: "name", label: "Name" },
        { key: "contactEmail", label: "Email" },
        { key: "contactPhoneNo", label: "Contact Phone" },
        { key: "address", label: "Address" },
      ]}
      columns={[
        ["Name", (row) => row.name],
        ["Email", (row) => row.contactEmail],
        ["Contact Phone", (row) => row.contactPhoneNo],
        ["Address", (row) => row.address],
        ["Created At", (row) => formatDate(row.createdAt)],
      ]}
      blank={{ ID: 0, name: "", contactEmail: "", contactPhoneNo: "", address: "", createdAt: nowIso() }}
      toAdd={(row) => supplierBody(row)}
      toUpdate={(row) => ({ supplierId: row.ID, ...supplierBody(row), createdAt: row.createdAt })}
      add={(body) => endpoints.suppliers.add(props.session.token, body)}
      update={(body) => endpoints.suppliers.update(props.session.token, body)}
      deleteRow={(row) => endpoints.suppliers.delete(props.session.token, row.ID)}
      refresh={props.refresh}
      notify={props.notify}
    />
  );
}

function itemBody(row: GeneralItem) {
  return {
    name: row.name,
    setQuantity: Number(row.setQuantity),
    barcode: row.barcode,
    categoryId: Number(row.categoryId),
    supplierId: Number(row.supplierId),
    price: Number(row.price),
    expires: Boolean(row.expires),
  };
}

function supplierBody(row: Supplier) {
  return {
    supplierName: row.name,
    contactEmail: row.contactEmail,
    contactPhone: row.contactPhoneNo,
    address: row.address,
    createdAt: row.createdAt || nowIso(),
  };
}
