package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.AuthContext;
import warewise.server.common.handler.InventoryHandler;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.WarehouseItemHandler;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.model.Inventory;
import warewise.server.common.model.User;
import warewise.server.common.model.WarehouseItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST resource providing endpoints to manage items.
 * Includes endpoints to retrieve, add, update, and delete items.
 */
@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseItemResource {

    @GET
    @Path("/get_items")
    public Response getItems(@Context HttpHeaders headers) {
        User user = AuthContext.currentUser(headers);
        List<WarehouseItem> items = WarehouseItemHandler.getInstance().getAllItems();
        if (!AuthContext.isAdmin(user)) {
            Map<Integer, Inventory> inventoryById = InventoryHandler.getInstance().getAllInventories()
                    .stream()
                    .collect(Collectors.toMap(Inventory::getID, inventory -> inventory, (first, second) -> first));
            items = items.stream()
                    .filter(item -> {
                        Inventory inventory = inventoryById.get(item.getInventoryID());
                        return inventory != null && AuthContext.canAccessWarehouse(user, inventory.getWarehouseId());
                    })
                    .toList();
        }
        String data = JsonSerializer.serializeListToJson(items);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_item")
    public Response addItem(AddRequest req) {
        WarehouseItem newWarehouseItem = new WarehouseItem(
                req.orderId,
                req.inventoryId,
                req.quantity,
                req.general_item_id,
                req.expireDate,
                req.sold
        );
        WarehouseItemHandler.getInstance().addItem(newWarehouseItem);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_item")
    public Response updateItem(UpdateRequest req) {
        if (req.itemId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "itemId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        WarehouseItem warehouseItem = WarehouseItemHandler.getInstance().getItem(req.itemId);
        if (warehouseItem == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.orderId != null) warehouseItem.setOrderID(req.orderId);
        if (req.inventoryId != null) warehouseItem.setInventoryID(req.inventoryId);
        if (req.quantity != null) warehouseItem.setQuantity(req.quantity);
        if (req.general_item_id != null) warehouseItem.setGeneralItemId(req.general_item_id);
        if (req.expireDate != null) warehouseItem.setExpireDate(req.expireDate);
        if (req.sold != null) warehouseItem.setSold(req.sold);

        WarehouseItemHandler.getInstance().updateItem(warehouseItem);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Item updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_item/{itemId}")
    public Response deleteItem(@PathParam("itemId") int itemId) {

        WarehouseItem warehouseItem = WarehouseItemHandler.getInstance().getItem(itemId);
        if (warehouseItem == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        WarehouseItemHandler.getInstance().deleteItem(itemId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    static class AddRequest {
        public Integer orderId;
        public Integer inventoryId;
        public Integer quantity;
        public Integer general_item_id;
        public String expireDate;
        public Boolean sold;

    }

    static class UpdateRequest {
        public Integer itemId;
        public Integer orderId;
        public Integer inventoryId;
        public Integer quantity;
        public Integer general_item_id;
        public String expireDate;
        public Boolean sold;

    }
}
