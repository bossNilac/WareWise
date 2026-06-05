package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.AuthContext;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.InventoryHandler;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.StockAlertHandler;
import warewise.server.common.model.Inventory;
import warewise.server.common.model.StockAlert;
import warewise.server.common.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST resource providing endpoints to manage stock alerts.
 * Includes endpoints to retrieve, add, update, and delete stock alerts.
 */
@Path("/stock_alerts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockAlertResource {

    @GET
    @Path("/get_stock_alerts")
    public Response getStockAlerts(@Context HttpHeaders headers) {
        User user = AuthContext.currentUser(headers);
        List<StockAlert> alerts = StockAlertHandler.getInstance().getAllStockAlerts();
        if (!AuthContext.isAdmin(user)) {
            Map<Integer, Inventory> inventoryById = InventoryHandler.getInstance().getAllInventories()
                    .stream()
                    .collect(Collectors.toMap(Inventory::getID, inventory -> inventory, (first, second) -> first));
            alerts = alerts.stream()
                    .filter(alert -> {
                        Inventory inventory = inventoryById.get(alert.getProductID());
                        return inventory != null && AuthContext.canAccessWarehouse(user, inventory.getWarehouseId());
                    })
                    .toList();
        }
        String data = JsonSerializer.serializeListToJson(alerts);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_stock_alert")
    public Response addStockAlert(AddRequest req, @Context HttpHeaders headers) {
        Inventory inventory = InventoryHandler.getInstance().getInventory(req.productId);
        if (inventory == null || !AuthContext.canAccessWarehouse(AuthContext.currentUser(headers), inventory.getWarehouseId())) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }
        StockAlert newAlert = new StockAlert(
                req.productId,
                req.createdAt,
                req.resolved
        );
        StockAlertHandler.getInstance().addStockAlert(newAlert);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Stock alert added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_stock_alert")
    public Response updateStockAlert(UpdateRequest req, @Context HttpHeaders headers) {
        if (req.stockAlertId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "stockAlertId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        StockAlert alert = StockAlertHandler.getInstance().getStockAlert(req.stockAlertId);
        if (alert == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Stock alert not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }
        User user = AuthContext.currentUser(headers);
        Inventory existingInventory = InventoryHandler.getInstance().getInventory(alert.getProductID());
        Inventory targetInventory = req.productId == null ? existingInventory : InventoryHandler.getInstance().getInventory(req.productId);
        if (existingInventory == null || targetInventory == null
                || !AuthContext.canAccessWarehouse(user, existingInventory.getWarehouseId())
                || !AuthContext.canAccessWarehouse(user, targetInventory.getWarehouseId())) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }

        if (req.productId != null) alert.setProductID(req.productId);
        if (req.createdAt != null) alert.setCreatedAt(req.createdAt);
        if (req.resolved != null) alert.setResolved(req.resolved);

        StockAlertHandler.getInstance().updateStockAlert(alert);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Stock alert updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_stock_alert/{stockAlertId}")
    public Response deleteStockAlert(@PathParam("stockAlertId") int stockAlertId, @Context HttpHeaders headers) {

        StockAlert alert = StockAlertHandler.getInstance().getStockAlert(stockAlertId);
        if (alert == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Stock alert not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }
        Inventory inventory = InventoryHandler.getInstance().getInventory(alert.getProductID());
        if (inventory == null || !AuthContext.canAccessWarehouse(AuthContext.currentUser(headers), inventory.getWarehouseId())) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }

        StockAlertHandler.getInstance().deleteStockAlert(stockAlertId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Stock alert deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    static class AddRequest {
        public Integer productId;
        public String createdAt;
        public Boolean resolved;
    }

    static class UpdateRequest {
        public Integer stockAlertId;
        public Integer productId;
        public String createdAt;
        public Boolean resolved;
    }

}
