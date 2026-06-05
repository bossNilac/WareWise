import { useState } from "react";
import type { FormEvent } from "react";
import { Save, Trash2, X } from "lucide-react";
import { endpoints } from "../../api";
import { DataTable, FilterBar, IconAction, Panel } from "../../components/ui";
import { runAction } from "../../lib/notifications";
import type { Notice } from "../../lib/notifications";
import type { Order, OrderStatus, Session, WareWiseData } from "../../types";
import { formatDate, itemName, nowIso, userName } from "../../utils";

export default function OrdersPage({
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
  const [filters, setFilters] = useState({ date: "", user: "", status: "" });
  const [draft, setDraft] = useState({ general_item_id: "", quantity: "1", status: "PENDING" as OrderStatus });
  const rows = data.orders.filter((order) => {
    const owner = userName(data.users, order.userId).toLowerCase();
    return (!filters.date || order.createdAt?.startsWith(filters.date))
      && (!filters.user || owner.includes(filters.user.toLowerCase()))
      && (!filters.status || order.status === filters.status);
  });

  const createOrder = async (event: FormEvent) => {
    event.preventDefault();
    await runAction(notify, refresh, () => endpoints.orders.add(session.token, {
      general_item_id: Number(draft.general_item_id),
      quantity: Number(draft.quantity),
      status: draft.status,
      createdAt: nowIso(),
      updatedAt: nowIso(),
      userId: session.userId,
    }));
  };

  const updateStatus = async (order: Order, status: OrderStatus) => {
    await runAction(notify, refresh, () => endpoints.orders.update(session.token, { orderId: order.ID, status, userId: order.userId }));
  };

  const deleteOrder = async (order: Order) => {
    if (!window.confirm("Delete Order?")) return;
    await runAction(notify, refresh, () => endpoints.orders.delete(session.token, order.ID));
  };

  return (
    <section className="section-stack">
      <FilterBar>
        <input value={filters.date} onChange={(event) => setFilters({ ...filters, date: event.target.value })} placeholder="YYYY-MM-DD" />
        <input value={filters.user} onChange={(event) => setFilters({ ...filters, user: event.target.value })} placeholder="Username" />
        <select value={filters.status} onChange={(event) => setFilters({ ...filters, status: event.target.value })}>
          <option value="">All Statuses</option><option>PENDING</option><option>FULFILLED</option><option>CANCELLED</option>
        </select>
        <button className="ghost-button" onClick={() => setFilters({ date: "", user: "", status: "" })}><X size={16} />Reset Filter</button>
      </FilterBar>
      <DataTable rows={rows} columns={[
        ["Status", (order) => <select value={order.status} onChange={(event) => updateStatus(order, event.target.value as OrderStatus)}><option>PENDING</option><option>FULFILLED</option><option>CANCELLED</option></select>],
        ["Created At", (order) => formatDate(order.createdAt)],
        ["Updated At", (order) => formatDate(order.updatedAt)],
        ["Done By", (order) => userName(data.users, order.userId)],
        ["Item", (order) => itemName(data.generalItems, order.general_item_id)],
        ["Amount", (order) => order.quantity],
        ["", (order) => <IconAction title="Delete Order" icon={Trash2} onClick={() => deleteOrder(order)} />],
      ]} />
      <Panel title="Add New Order">
        <form className="form-grid compact" onSubmit={createOrder}>
          <select required value={draft.general_item_id} onChange={(event) => setDraft({ ...draft, general_item_id: event.target.value })}>
            <option value="">Select item</option>
            {data.generalItems.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
          </select>
          <input min={1} type="number" value={draft.quantity} onChange={(event) => setDraft({ ...draft, quantity: event.target.value })} />
          <select value={draft.status} onChange={(event) => setDraft({ ...draft, status: event.target.value as OrderStatus })}><option>PENDING</option><option>FULFILLED</option><option>CANCELLED</option></select>
          <button className="primary-button" type="submit"><Save size={16} />Add New Order</button>
        </form>
      </Panel>
    </section>
  );
}
