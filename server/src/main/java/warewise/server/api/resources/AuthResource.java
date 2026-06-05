package warewise.server.api.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.JwtUtil;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.encryption.Encrypt;
import warewise.server.common.handler.UserHandler;
import warewise.server.common.model.User;
import warewise.server.common.util.enums.UserRole;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @POST
    @Path("/login")
    public Response login(AuthResource.LoginRequest req){
        if (req == null || req.username == null || req.password == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Username and password are required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        User user = UserHandler.getInstance().getUserByUsername(req.username);
        if (user == null || !Encrypt.verifyPassword(user.getPasswordHash(), req.password)) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Invalid username or password", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }

        List<Object> session = List.of(
                JwtUtil.generateLoginToken(user.getUsername()),
                user.getRole().name(),
                user.getID()
        );
        ApiResponse<List<Object>> resp = new ApiResponse<>(true, "Login success", session);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/register")
    public Response register(RegisterRequest req) {
        if (req == null || req.username == null || req.password == null || req.role == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "username, password, and role are required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        for (User user : UserHandler.getInstance().getAllUsers()) {
            if (user.getUsername().equals(req.username)) {
                ApiResponse<Void> resp = new ApiResponse<>(false, "User already exists", null);
                return Response.status(Response.Status.CONFLICT).entity(resp).build();
            }
        }

        User newUser = new User(
                LocalDateTime.now(Clock.systemDefaultZone()).toString(),
                req.email,
                UserRole.fromLabel(req.role),
                Encrypt.hashPassword(req.password),
                req.username,
                normalizeWarehouseIds(req.warehouseIds, req.warehouseId)
        );
        UserHandler.getInstance().addUser(newUser);
        ApiResponse<Void> resp = new ApiResponse<>(true, "User registered", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/reset_password")
    public Response resetPassword(ResetPasswordRequest req) {
        if (req == null || req.userId == null || req.password == null || req.password.isBlank()) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "userId and password are required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        User user = UserHandler.getInstance().getUser(req.userId);
        if (user == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "User does not exist", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        user.setPasswordHash(Encrypt.hashPassword(req.password));
        UserHandler.getInstance().updateUser(user);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Password reset", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    /**
     * Logs out a user by revoking the provided JWT token.
     *
     * @param authHeader the Authorization header containing the Bearer token.
     * @return a {@link Response} indicating logout success.
    */@GET
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Missing bearer token", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }
        JwtUtil.removeToken(authHeader.replace("Bearer ",""));
        ApiResponse<Void> resp = new ApiResponse<>(true, "Logout",null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class RegisterRequest {
        public String username;
        public String email;
        public String role;
        public String password;
        public Integer warehouseId;
        public List<Integer> warehouseIds;
    }

    static class ResetPasswordRequest {
        public Integer userId;
        public String password;
    }

    private static List<Integer> normalizeWarehouseIds(List<Integer> warehouseIds, Integer warehouseId) {
        List<Integer> normalized = new ArrayList<>();
        if (warehouseIds != null) {
            for (Integer id : warehouseIds) {
                if (id != null && id > 0 && !normalized.contains(id)) {
                    normalized.add(id);
                }
            }
        } else if (warehouseId != null && warehouseId > 0) {
            normalized.add(warehouseId);
        }
        return normalized;
    }

}

