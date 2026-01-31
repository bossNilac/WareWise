package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.NotificationService;
import warewise.server.common.encryption.Encrypt;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.model.User;
import warewise.server.common.handler.UserHandler;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.util.enums.UserRole;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST resource providing endpoints to manage users.
 * <p>
 * Includes endpoints to retrieve users, filter by role, link users to departments, and delete users.
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    /**
     * Retrieves all users in the system.
     *
     * @return a {@link Response} containing all users as JSON.
     */@GET
    @Path("/get_users")
    public Response get_users() {
        String data = JsonSerializer.serializeListToJson(UserHandler.getInstance().getAllUsers(),List.of("passwordHash"));
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param role the role to filter users by.
     * @return a {@link Response} containing filtered users.
     */@GET
    @Path("/get_users/{role}")
    public Response get_users_by_role(@PathParam("role") String role) {
        List<User> dat = UserHandler.getInstance().getAllUsers();
        dat.removeIf(u -> !u.getRole().name().equals(role));
        String data = JsonSerializer.serializeListToJson(dat);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    /**
     * Retrieves the user's role.
     *
     * @param userid the id to filter users by.
     * @return a {@link Response} containing filtered user role.
     */@GET
    @Path("/{userid}/get_user_role")
    public Response get_users_by_role(@PathParam("userid") int userid) {
        User user = UserHandler.getInstance().getUser(userid);
        if(user == null) {
            ApiResponse<String> resp = new ApiResponse<>(false, "User not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }else{
            ApiResponse<UserRole> resp = new ApiResponse<>(true, "", user.getRole());
            return Response.status(Response.Status.OK).entity(resp).build();
        }
    }

    /**
     * Registers a new user with the provided data.
     * <p>
     * Checks for duplicate usernames, hashes the password,
     * and stores the new user record.
     *
     * @param req the {@link AddRequest} containing registration data.
     * @return a {@link Response} containing an {@link ApiResponse} indicating success or conflict.

     **/@POST
    @Path("/add_user")
    public Response addUser(AddRequest req){
        for  (User u: UserHandler.getInstance().getAllUsers()){
            if(u.getUsername().equals(req.username)){
                ApiResponse<Void> resp = new ApiResponse<>(false, "User already exists", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
            }
        }
        // hash & insert
        String password = Encrypt.hashPassword(req.password);
        User newUser = new User(LocalDateTime.now(Clock.systemDefaultZone())
                .toString(), req.email,
                UserRole.fromLabel(req.role),password, req.username,req.warehouseId);
        NotificationService.notifyNewAccount(newUser, req.password);
        UserHandler.getInstance().addUser(newUser);
        ApiResponse<Void> resp = new ApiResponse<>(false, "User added", null);
        return Response.status(Response.Status.OK).entity(resp).build();

    }

    /**
     * Updates an existing user.
     * Only provided (non-null) fields will be updated.
     * Validates that the user exists before updating.
     *
     * @param updateUserRequest the {@link UpdateUserRequest} with updated user data.
     * @return a {@link Response} indicating update result.
     */
    @PATCH
    @Path("/update_user")
    public Response update_user(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.userId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "userId", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        User user = UserHandler.getInstance().getUser(updateUserRequest.userId);
        if (user == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "User does not exist", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (updateUserRequest.username != null) {
            user.setUsername(updateUserRequest.username);
        }
        if (updateUserRequest.email != null) {
            user.setEmail(updateUserRequest.email);
        }
        if (updateUserRequest.role != null) {
            user.setRole(UserRole.fromLabel(updateUserRequest.role));
        }
        if (updateUserRequest.password != null) {
            String passHash = Encrypt.hashPassword(updateUserRequest.password);
            user.setPasswordHash(passHash);
        }

        if (updateUserRequest.warehouseId != null) {
            user.setWarehouseId(updateUserRequest.warehouseId);
        }

        UserHandler.getInstance().updateUser(user);

        ApiResponse<Void> resp = new ApiResponse<>(true, "User updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    /**
     * Deletes a user by ID.
     *
     * @param userId containing the user ID.
     * @return a {@link Response} indicating deletion result.
     */

    @DELETE
    @Path("/delete_user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") int userId) {
        User user = UserHandler.getInstance().getUser(userId);
        if (user == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "User not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        UserHandler.getInstance().deleteUser(user.getID());
        ApiResponse<Void> resp = new ApiResponse<>(true, "Deleted user!", null);
        return Response.ok(resp).build();
    }


    /**
     * DTO for updating an existing user.
     */
    static class UpdateUserRequest {
        public Integer userId;
        public String username;
        public String password;
        public String email;
        public String role;
        public Integer warehouseId;
    }

    static class AddRequest {
        public String username;
        public String email;
        public String role;
        public String password;
        public Integer warehouseId;
    }
}
