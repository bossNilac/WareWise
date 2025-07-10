package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.WarehouseHandler;
import warewise.server.common.model.Warehouse;

/**
 * REST resource providing endpoints to manage warehouses.
 * Includes endpoints to retrieve, add, update, and delete warehouses.
 */
@Path("/warehouses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseResource {

    @GET
    @Path("/get_warehouses")
    public Response getWarehouses() {
        String data = JsonSerializer.serializeListToJson(
                WarehouseHandler.getInstance().getAllWarehouses());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_warehouse")
    public Response addWarehouse(AddRequest req) {
        Warehouse newWarehouse = new Warehouse(
                req.warehouseId,
                req.name,
                req.address
        );
        WarehouseHandler.getInstance().addWarehouse(newWarehouse);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Warehouse added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_warehouse")
    public Response updateWarehouse(UpdateRequest req) {
        if (req.warehouseId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "warehouseId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Warehouse warehouse = WarehouseHandler.getInstance().getWarehouse(req.warehouseId);
        if (warehouse == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.name != null) warehouse.setName(req.name);
        if (req.address != null) warehouse.setAddress(req.address);

        WarehouseHandler.getInstance().updateWarehouse(warehouse);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Warehouse updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_warehouse")
    public Response deleteWarehouse(DeleteRequest req) {
        if (req.warehouseId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "warehouseId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Warehouse warehouse = WarehouseHandler.getInstance().getWarehouse(req.warehouseId);
        if (warehouse == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Warehouse not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        WarehouseHandler.getInstance().deleteWarehouse(req.warehouseId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Warehouse deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class AddRequest {
        public Integer warehouseId;
        public String name;
        public String address;
    }

    static class UpdateRequest {
        public Integer warehouseId;
        public String name;
        public String address;
    }

    static class DeleteRequest {
        public Integer warehouseId;
    }
}
