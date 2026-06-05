import { Boxes, ShoppingCart } from "lucide-react";
import { BarList, KpiGrid, List, Panel } from "../components/ui";
import type { PageKey } from "../lib/navigation";
import type { Notice } from "../lib/notifications";
import type { Session, WareWiseData } from "../types";
import { LOW_STOCK_THRESHOLD, formatDate, recentActions, userName } from "../utils";

export default function DashboardPage({
  data,
  session,
  goTo,
}: {
  data: WareWiseData;
  session: Session;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
  goTo: (page: PageKey) => void;
}) {
  const lowStock = data.inventory.filter((record) => record.quantity <= LOW_STOCK_THRESHOLD);
  const totalInventory = data.inventory.reduce((sum, record) => sum + record.quantity, 0);
  const soldRevenue = data.warehouseItems
    .filter((item) => item.sold)
    .reduce((sum, item) => sum + item.quantity * (data.generalItems.find((catalog) => catalog.id === item.generalItemId)?.price ?? 0), 0);
  const categoryChart = data.categories.map((category) => ({
    label: category.name,
    value: data.generalItems.filter((item) => item.categoryId === category.ID).reduce((sum, item) => sum + item.setQuantity, 0),
  }));
  const notifications = [
    ...data.orders.slice(-8).map((order) => `New order received by ${userName(data.users, order.userId)} at ${formatDate(order.createdAt)}`),
    ...lowStock.map((record) => `Low stock alert: ${record.name} has only ${record.quantity} left`),
  ];

  return (
    <div className="page-grid">
      <KpiGrid items={[
        ["Total Orders", data.orders.length.toString()],
        ["Inventory Levels", totalInventory.toString()],
        ["Low-Stock Items", lowStock.length.toString()],
        ["Overall Value", `$${soldRevenue.toFixed(2)}`],
      ]} />
      <Panel title="Inventory by Category" wide><BarList rows={categoryChart} /></Panel>
      <Panel title="Recent Activity Feed"><List items={recentActions(data.logs, session.username)} empty="No recent activity" /></Panel>
      <Panel title="Quick Actions">
        <div className="action-row">
          <button className="primary-button" onClick={() => goTo("orders")}><ShoppingCart size={16} />Create Order</button>
          <button className="primary-button" onClick={() => goTo("inventory")}><Boxes size={16} />Update Inventory</button>
        </div>
      </Panel>
      <Panel title="Notifications"><List items={notifications} empty="No active notifications" /></Panel>
    </div>
  );
}
