package warewise.server.api;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import warewise.server.common.logs.AppLogger;
import warewise.server.common.model.User;
import warewise.server.common.util.enums.UserRole;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiSecurityFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        addCorsPreflightSupport(requestContext);
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok().build());
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("auth/login") || path.startsWith("status")) {
            return;
        }

        String username = AuthContext.usernameFromAuthorization(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
        if (username == null) {
            requestContext.abortWith(jsonError(Response.Status.UNAUTHORIZED, "Unauthorized: invalid or missing token"));
            return;
        }

        User user = warewise.server.common.handler.UserHandler.getInstance().getUserByUsername(username);
        if (!isAuthorized(user, requestContext.getMethod(), path)) {
            requestContext.abortWith(jsonError(Response.Status.FORBIDDEN, "Forbidden: insufficient role"));
            return;
        }

        requestContext.setProperty("username", username);
        if (!path.startsWith("logs")) {
            AppLogger.log(requestContext.getMethod(), "/api/" + path, username);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        if (origin != null) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
        }
        responseContext.getHeaders().putSingle("Vary", "Origin");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, Origin");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "3600");
    }

    private void addCorsPreflightSupport(ContainerRequestContext requestContext) {
        requestContext.setProperty("cors", true);
    }

    private Response jsonError(Response.Status status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"" + message + "\"}")
                .build();
    }

    private boolean isAuthorized(User user, String method, String path) {
        if (user == null) {
            return false;
        }
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        if (isAdminOnlyPath(method, path)) {
            return false;
        }
        if (path.startsWith("auth/logout")) {
            return true;
        }
        if (user.getRole() == UserRole.MANAGER) {
            return true;
        }
        if (user.getRole() != UserRole.WORKER) {
            return false;
        }

        if ("GET".equalsIgnoreCase(method)) {
            return path.startsWith("users/get_users")
                    || path.startsWith("categories/get_categories")
                    || path.startsWith("general_items/get_items")
                    || path.startsWith("suppliers/get_suppliers")
                    || path.startsWith("inventory/get_inventory")
                    || path.startsWith("warehouses/get_warehouses")
                    || path.startsWith("stock_alerts/get_stock_alerts")
                    || path.startsWith("orders/get_orders")
                    || path.startsWith("items/get_items")
                    || path.startsWith("logs/get_logs");
        }

        if ("POST".equalsIgnoreCase(method)) {
            return path.startsWith("categories/add_category")
                    || path.startsWith("general_items/add_item")
                    || path.startsWith("inventory/add_inventory")
                    || path.startsWith("stock_alerts/add_stock_alert");
        }

        if ("PATCH".equalsIgnoreCase(method)) {
            return path.startsWith("inventory/update_inventory")
                    || path.startsWith("stock_alerts/update_stock_alert");
        }

        return false;
    }

    private boolean isAdminOnlyPath(String method, String path) {
        if (path.startsWith("auth/register") || path.startsWith("auth/reset_password")) {
            return true;
        }
        if (path.startsWith("users/add_user")
                || path.startsWith("users/update_user")
                || path.startsWith("users/assign_warehouses")
                || path.startsWith("users/delete_user")) {
            return true;
        }
        return "DELETE".equalsIgnoreCase(method) && path.startsWith("users/");
    }
}
