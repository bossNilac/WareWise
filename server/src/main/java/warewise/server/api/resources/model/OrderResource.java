package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.OrderHandler;
import warewise.server.common.model.Order;
import warewise.server.common.util.enums.OrderStatus;

/**
 * REST resource providing endpoints to manage orders.
 * Includes endpoints to retrieve, add, update, and delete orders.
 */
@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @GET
    @Path("/get_orders")
    public Response getOrders() {
        String data = JsonSerializer.serializeListToJson(
                OrderHandler.getInstance().getAllOrders());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_order")
    public Response addOrder(AddRequest req) {
        Order newOrder = new Order(
                req.orderId,
                req.customerName,
                req.customerEmail,
                OrderStatus.fromLabel(req.status),
                req.createdAt,
                req.updatedAt,
                req.userId
        );
        OrderHandler.getInstance().addOrder(newOrder);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Order added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_order")
    public Response updateOrder(UpdateRequest req) {
        if (req.orderId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "orderId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Order order = OrderHandler.getInstance().getOrder(req.orderId);
        if (order == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Order not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.customerName != null) order.setCustomerName(req.customerName);
        if (req.customerEmail != null) order.setCustomerEmail(req.customerEmail);
        if (req.status != null) order.setStatus(OrderStatus.fromLabel(req.status));
        if (req.createdAt != null) order.setCreatedAt(req.createdAt);
        if (req.updatedAt != null) order.setUpdatedAt(req.updatedAt);
        if (req.userId != null) order.setUserId(req.userId);

        OrderHandler.getInstance().updateOrder(order);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Order updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_order")
    public Response deleteOrder(DeleteRequest req) {
        if (req.orderId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "orderId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Order order = OrderHandler.getInstance().getOrder(req.orderId);
        if (order == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Order not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        OrderHandler.getInstance().deleteOrder(req.orderId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Order deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class AddRequest {
        public Integer orderId;
        public String customerName;
        public String customerEmail;
        public String status;
        public String createdAt;
        public String updatedAt;
        public Integer userId;
    }

    static class UpdateRequest {
        public Integer orderId;
        public String customerName;
        public String customerEmail;
        public String status;
        public String createdAt;
        public String updatedAt;
        public Integer userId;
    }

    static class DeleteRequest {
        public Integer orderId;
    }
}
