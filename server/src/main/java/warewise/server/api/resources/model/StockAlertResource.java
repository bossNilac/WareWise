package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.StockAlertHandler;
import warewise.server.common.model.StockAlert;

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
    public Response getStockAlerts() {
        String data = JsonSerializer.serializeListToJson(
                StockAlertHandler.getInstance().getAllStockAlerts());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_stock_alert")
    public Response addStockAlert(AddRequest req) {
        StockAlert newAlert = new StockAlert(
                req.stockAlertId,
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
    public Response updateStockAlert(UpdateRequest req) {
        if (req.stockAlertId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "stockAlertId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        StockAlert alert = StockAlertHandler.getInstance().getStockAlert(req.stockAlertId);
        if (alert == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Stock alert not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.productId != null) alert.setProductID(req.productId);
        if (req.createdAt != null) alert.setCreatedAt(req.createdAt);
        if (req.resolved != null) alert.setResolved(req.resolved);

        StockAlertHandler.getInstance().updateStockAlert(alert);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Stock alert updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_stock_alert")
    public Response deleteStockAlert(DeleteRequest req) {
        if (req.stockAlertId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "stockAlertId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        StockAlert alert = StockAlertHandler.getInstance().getStockAlert(req.stockAlertId);
        if (alert == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Stock alert not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        StockAlertHandler.getInstance().deleteStockAlert(req.stockAlertId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Stock alert deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class AddRequest {
        public Integer stockAlertId;
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

    static class DeleteRequest {
        public Integer stockAlertId;
    }
}
