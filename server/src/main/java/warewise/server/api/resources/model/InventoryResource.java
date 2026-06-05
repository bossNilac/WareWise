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
import warewise.server.common.model.Inventory;
import warewise.server.common.model.User;

import java.util.List;

/**
 * REST resource providing endpoints to manage inventory items.
 * Includes endpoints to retrieve, add, update, and delete inventory records.
 */
@Path("/inventory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {
    @GET
    @Path("/get_inventory")
    public Response getInventory(@Context HttpHeaders headers) {
        User user = AuthContext.currentUser(headers);
        List<Inventory> inventories = AuthContext.filterByWarehouse(
                user,
                InventoryHandler.getInstance().getAllInventories(),
                Inventory::getWarehouseId
        );
        String data = JsonSerializer.serializeListToJson(inventories);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_inventory")
    public Response addInventory(AddRequest req, @Context HttpHeaders headers) {
        User user = AuthContext.currentUser(headers);
        if (!AuthContext.canAccessWarehouse(user, req.warehouseId)) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }
        Inventory newInv = new Inventory(
                req.name,
                req.description,
                req.stockQuantity,
                req.lastUpdated,
                req.warehouseId
        );
        InventoryHandler.getInstance().addInventory(newInv);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Inventory added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_inventory")
    public Response updateInventory(UpdateRequest req, @Context HttpHeaders headers) {
        if (req.inventoryId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "inventoryId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Inventory inventory = InventoryHandler.getInstance().getInventory(req.inventoryId);
        if (inventory == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Inventory not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }
        User user = AuthContext.currentUser(headers);
        Integer targetWarehouseId = req.warehouseId == null ? inventory.getWarehouseId() : req.warehouseId;
        if (!AuthContext.canAccessWarehouse(user, inventory.getWarehouseId())
                || !AuthContext.canAccessWarehouse(user, targetWarehouseId)) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }

        if (req.name != null) inventory.setName(req.name);
        if (req.stockQuantity != null) inventory.setQuantity(req.stockQuantity);
        if (req.description != null) inventory.setDescription(req.description);
        if (req.lastUpdated != null) inventory.setLastUpdated(req.lastUpdated);
        if (req.warehouseId != null) inventory.setWarehouseId(req.warehouseId);

        InventoryHandler.getInstance().updateInventory(inventory);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Inventory updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_inventory/{inventoryId}")
    public Response deleteInventory(@PathParam("inventoryId") int inventoryId, @Context HttpHeaders headers) {
        Inventory inventory = InventoryHandler.getInstance().getInventory(inventoryId);
        if (inventory == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Inventory not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }
        if (!AuthContext.canAccessWarehouse(AuthContext.currentUser(headers), inventory.getWarehouseId())) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not allowed", null);
            return Response.status(Response.Status.FORBIDDEN).entity(resp).build();
        }

        InventoryHandler.getInstance().deleteInventory(inventoryId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Inventory deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    static class AddRequest {
        public String name;
        public Integer stockQuantity;
        public String description;
        public String lastUpdated;
        public Integer warehouseId;
    }

    static class UpdateRequest {
        public Integer inventoryId;
        public String name;
        public Integer stockQuantity;
        public String description;
        public String lastUpdated;
        public Integer warehouseId;
    }

}
