import type { PageKey } from "../lib/navigation";
import type { Notice } from "../lib/notifications";
import type { Session, WareWiseData } from "../types";
import AdminConsolePage from "./admin/AdminConsolePage";
import AlertsPage from "./worker/AlertsPage";
import DashboardPage from "./DashboardPage";
import InventoryPage from "./worker/InventoryPage";
import OrdersPage from "./worker/OrdersPage";
import SettingsPage from "./SettingsPage";
import StockAdjustmentPage from "./worker/StockAdjustmentPage";
import AddItemPage from "./worker/AddItemPage";
import { CatalogPage, CategoriesPage, SuppliersPage } from "./manager/CrudPages";
import { ReplenishmentPage, ReportsPage, WarehouseHealthPage } from "./manager/AnalyticsPages";

export type PageProps = {
  session: Session;
  data: WareWiseData;
  refresh: () => Promise<void>;
  notify: (notice: Notice) => void;
};

export default function PageRouter({
  page,
  session,
  data,
  refresh,
  notify,
  goTo,
}: PageProps & { page: PageKey; goTo: (page: PageKey) => void }) {
  const props = { session, data, refresh, notify };
  switch (page) {
    case "dashboard":
      return <DashboardPage {...props} goTo={goTo} />;
    case "orders":
      return <OrdersPage {...props} />;
    case "inventory":
      return <InventoryPage data={data} />;
    case "add-item":
      return <AddItemPage {...props} />;
    case "receive":
      return <StockAdjustmentPage {...props} mode="receive" />;
    case "damage":
      return <StockAdjustmentPage {...props} mode="damage" />;
    case "alerts":
      return <AlertsPage {...props} />;
    case "categories":
      return <CategoriesPage {...props} />;
    case "catalog":
      return <CatalogPage {...props} />;
    case "suppliers":
      return <SuppliersPage {...props} />;
    case "reports":
      return <ReportsPage data={data} />;
    case "replenishment":
      return <ReplenishmentPage data={data} />;
    case "warehouse-health":
      return <WarehouseHealthPage data={data} />;
    case "admin":
      return <AdminConsolePage {...props} />;
    case "settings":
      return <SettingsPage session={session} />;
  }
}
