package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.GeneralItemHandler;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.model.GeneralItem;

import java.util.List;

/**
 * REST resource for managing general items.
 * Includes endpoints to retrieve, add, update, and delete general items.
 */
@Path("/general_items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeneralItemResource {

    @GET
    @Path("/get_items")
    public Response getAll() {
        List<GeneralItem> items = GeneralItemHandler.getInstance().getAllGeneralItems();
        String data = JsonSerializer.serializeListToJson(items);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_item")
    public Response addItem(AddRequest req) {
        GeneralItem item = new GeneralItem(
            req.name,
            req.setQuantity,
            req.barcode,
            req.categoryId,
            req.supplierId,
            req.price,
            req.expires

        );
        GeneralItemHandler.getInstance().addGeneralItem(item);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_item")
    public Response updateItem(UpdateRequest req) {
        if (req.itemId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item ID is required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        GeneralItem item = GeneralItemHandler.getInstance().getGeneralItem(req.itemId);
        if (item == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.name != null) item.setName(req.name);
        if (req.setQuantity != null) item.setSetQuantity(req.setQuantity);
        if (req.barcode != null) item.setBarcode(req.barcode);
        if (req.categoryId != null) item.setCategoryId(req.categoryId);
        if (req.supplierId != null) item.setSupplierId(req.supplierId);
        if (req.price != null) item.setSupplierId(req.price);
        if (req.expires != null) item.setExpires(req.expires);

        GeneralItemHandler.getInstance().updateGeneralItem(item);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item updated", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_item")
    public Response deleteItem(DeleteRequest req) {
        if (req.itemId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item ID is required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        GeneralItem item = GeneralItemHandler.getInstance().getGeneralItem(req.itemId);
        if (item == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Item not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        GeneralItemHandler.getInstance().deleteGeneralItem(req.itemId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Item deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    // DTOs
    static class AddRequest {
        public String name;
        public Integer setQuantity;
        public String barcode;
        public Integer categoryId;
        public Integer supplierId;
        public Integer price;
        public Boolean expires;
    }

    static class UpdateRequest {
        public Integer itemId;
        public String name;
        public Integer setQuantity;
        public String barcode;
        public Integer categoryId;
        public Integer supplierId;
        public Integer price;
        public Boolean expires;
    }

    static class DeleteRequest {
        public Integer itemId;
    }
}
