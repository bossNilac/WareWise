package warewise.server.api;

import jakarta.ws.rs.core.HttpHeaders;
import warewise.server.common.handler.UserHandler;
import warewise.server.common.model.User;
import warewise.server.common.util.enums.UserRole;

import java.util.List;

public final class AuthContext {
    private AuthContext() {
    }

    public static User currentUser(HttpHeaders headers) {
        String username = usernameFromAuthorization(headers == null ? null : headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        if (username == null) {
            return null;
        }
        return UserHandler.getInstance().getUserByUsername(username);
    }

    public static String usernameFromHeaders(HttpHeaders headers) {
        return usernameFromAuthorization(headers == null ? null : headers.getHeaderString(HttpHeaders.AUTHORIZATION));
    }

    public static String usernameFromAuthorization(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring("Bearer ".length());
        if (!JwtUtil.isValid(token) || JwtUtil.isRevoked(token)) {
            return null;
        }
        return JwtUtil.extractUsername(token);
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public static boolean isManager(User user) {
        return user != null && user.getRole() == UserRole.MANAGER;
    }

    public static boolean canAccessWarehouse(User user, Integer warehouseId) {
        if (warehouseId == null) {
            return false;
        }
        if (isAdmin(user)) {
            return true;
        }
        return user != null && user.getWarehouseIds().contains(warehouseId);
    }

    public static <T> List<T> filterByWarehouse(User user, List<T> values, WarehouseIdReader<T> reader) {
        if (isAdmin(user)) {
            return values;
        }
        if (user == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> canAccessWarehouse(user, reader.warehouseId(value)))
                .toList();
    }

    @FunctionalInterface
    public interface WarehouseIdReader<T> {
        Integer warehouseId(T value);
    }
}
