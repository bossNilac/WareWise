package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.ItemHandler;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.model.Item;

/**
 * REST resource providing endpoints to manage items.
 * Includes endpoints to retrieve, add, update, and delete items.
 */
@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemResource {

    @GET
    @Path("/get_items")
    public Response getItems() {
        String data = JsonSerializer.serializeListToJson(
                ItemHandler.getInstance().getAllItems());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_item")
    public Response addItem(AddRequest req) {
        Item newItem = new Item(
                req.itemId,
                req.orderId,
                req.inventoryId,
                req.price,
                req.quantity,
                req.total,
                req.categoryId,
                req.supplierId
        );
        ItemHandler.getInstance().addItem(newItem);
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

        Item item = ItemHandler.getInstance().getItem(req.itemId);
        if (item == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.orderId != null) item.setOrderID(req.orderId);
        if (req.inventoryId != null) item.setInventoryID(req.inventoryId);
        if (req.quantity != null) item.setQuantity(req.quantity);
        if (req.price != null) item.setPrice(req.price);
        if (req.total != null) item.setTotal(req.total);
        if (req.categoryId != null) item.setCategoryID(req.categoryId);
        if (req.supplierId != null) item.setSupplierId(req.supplierId);

        ItemHandler.getInstance().updateItem(item);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Item updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_item")
    public Response deleteItem(DeleteRequest req) {
        if (req.itemId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "itemId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Item item = ItemHandler.getInstance().getItem(req.itemId);
        if (item == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        ItemHandler.getInstance().deleteItem(req.itemId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class AddRequest {
        public Integer itemId;
        public Integer orderId;
        public Integer inventoryId;
        public Integer quantity;
        public Double price;
        public Double total;
        public Integer categoryId;
        public Integer supplierId;
    }

    static class UpdateRequest {
        public Integer itemId;
        public Integer orderId;
        public Integer inventoryId;
        public Integer quantity;
        public Double price;
        public Double total;
        public Integer categoryId;
        public Integer supplierId;
    }

    static class DeleteRequest {
        public Integer itemId;
    }
}
