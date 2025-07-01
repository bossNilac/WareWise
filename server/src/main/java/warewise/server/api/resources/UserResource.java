package warewise.server.api.resources;

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
        String data = JsonSerializer.serializeListToJson(UserHandler.getInstance().loadUsers());
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
        List<User> dat = UserHandler.getInstance().loadUsers();
        dat.removeIf(u -> !u.getRole().name().equals(role));
        String data = JsonSerializer.serializeListToJson(dat);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
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
        for  (User u: UserHandler.getInstance().loadUsers()){
            if(u.getUsername().equals(req.username)){
                ApiResponse<Void> resp = new ApiResponse<>(false, "User already exists", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
            }
        }
        // hash & insert
        String password = Encrypt.hashPassword(req.password);
        User newUser = new User(LocalDateTime.now(Clock.systemDefaultZone())
                .toString(), req.email,
                UserRole.fromLabel(req.role),password, req.username);
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

        User user = UserHandler.getInstance().loadUser(updateUserRequest.userId);
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

        UserHandler.getInstance().updateUser(user);

        ApiResponse<Void> resp = new ApiResponse<>(true, "User updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    /**
     * Deletes a user by ID.
     *
     * @param deleteUserRequest the {@link DeleteUserRequest} containing the user ID.
     * @return a {@link Response} indicating deletion result.
     */@DELETE
    @Path("/delete_user")
    public Response link_department(DeleteUserRequest deleteUserRequest){
        if(deleteUserRequest.userId ==null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "userId is needed", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }
        User user = UserHandler.getInstance().loadUser(deleteUserRequest.userId) ;
        if (user == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "User not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        } else {
            UserHandler.getInstance().deleteUser(user);
            ApiResponse<Void> resp = new ApiResponse<>(true, "Deleted user!", null);
            return Response.status(Response.Status.OK).entity(resp).build();
        }
    }


    static class DeleteUserRequest{
        public Integer userId;
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
    }

    static class AddRequest {
        public String username;
        public String email;
        public String role;
        public String password;
    }
}
