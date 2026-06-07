# WareWise Web Port

Vite + React + TypeScript port of the JavaFX worker, manager, and admin workflows.

## Runtime

- API base defaults to `http://localhost:8080/api`.
- Override with `VITE_WAREWISE_API_URL`.
- Authentication uses `/api/auth/login` and stores the JWT session in `localStorage`.

## JavaFX to React Map

| JavaFX source | React module |
| --- | --- |
| `Login.fxml`, `LoginController` | `src/pages/LoginPage.tsx` |
| `Main.fxml`, `MainController`, `ManagerMenu.fxml`, `WorkersMenu.fxml` | `src/components/AppShell.tsx`, `src/lib/navigation.ts`, `src/pages/PageRouter.tsx` |
| `DashboardView.fxml`, `DashboardController` | `src/pages/DashboardPage.tsx` |
| `OrdersView.fxml`, `OrdersController` | `src/pages/worker/OrdersPage.tsx` |
| `UpdateInventoryView.fxml`, `InventoryController` | `src/pages/worker/InventoryPage.tsx` |
| `AddItemView.fxml`, `AddItemController` | `src/pages/worker/AddItemPage.tsx` |
| `ReceiveShipmentsView.fxml`, `ReceiveShipmentsController` | `src/pages/worker/StockAdjustmentPage.tsx` with `mode="receive"` |
| `AdjustDamageView.fxml`, `AdjustDamageController` | `src/pages/worker/StockAdjustmentPage.tsx` with `mode="damage"` |
| `AlertsView.fxml`, `AlertsController` | `src/pages/worker/AlertsPage.tsx` |
| `CategoriesView.fxml`, `GeneralItemsView.fxml`, `SuppliersView.fxml` | `src/pages/manager/CrudPages.tsx` |
| `ReportsView.fxml`, `ReplenishmentView.fxml`, `WarehouseHealthView.fxml` | `src/pages/manager/AnalyticsPages.tsx` |
| `SettingsView.fxml`, `SettingsController` | `src/pages/SettingsPage.tsx` |
| `admin-console-gui/main-view.fxml`, `admin MainController` | `src/pages/admin/AdminConsolePage.tsx` |
| `DataHandler`, `WareHouseDataHandler`, `ApiHandler` | `src/api.ts`, `src/hooks/useWareWiseData.ts` |
| Java model classes in client/admin/server | `src/types.ts` |

## Structure

- `src/api.ts`: server endpoint wrapper, using server resource paths as the source of truth.
- `src/types.ts`: TypeScript versions of the server models and enums.
- `src/hooks`: session and data-loading hooks.
- `src/components`: reusable app shell and table/form/dashboard primitives.
- `src/pages`: JavaFX-screen-equivalent React pages split by workflow.
- `src/styles.css`: web adaptation of the JavaFX dark palette.
