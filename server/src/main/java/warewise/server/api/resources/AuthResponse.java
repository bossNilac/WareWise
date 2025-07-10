package warewise.server.api.resources;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import warewise.server.api.JwtUtil;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.encryption.Encrypt;
import warewise.server.common.handler.UserHandler;
import warewise.server.common.model.User;

@Path("/auth")
public class AuthResponse {
    @POST
    @Path("/login")
    public Response addUser(AuthResponse.LoginRequest req){
        for  (User u: UserHandler.getInstance().getAllUsers()){
            if(u.getUsername().equals(req.username)){
                if(Encrypt.verifyPassword(u.getPasswordHash(),req.password)){
                    ApiResponse<Void> resp = new ApiResponse<>(true, "Login success", null);
                    return Response.status(Response.Status.OK).entity(resp).build();
                }else {
                    ApiResponse<Void> resp = new ApiResponse<>(false, "Wrong password", null);
                    return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
                }
            }
        }
        ApiResponse<Void> resp = new ApiResponse<>(false, "User does not  exist", null);
        return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();

    }

    @POST
    @Path("/forgot_password-{token}")
    public Response forgot_password(@PathParam("token") String token, AuthResponse.LoginRequest req) {
        //verify token
//        if(!verifyToken(token)){
//            ApiResponse<Void> resp = new ApiResponse<>(true, "Token Invalid", null);
//        }
        for (User u : UserHandler.getInstance().getAllUsers()) {
            if (u.getUsername().equals(req.username)) {
                    u.setPasswordHash(Encrypt.hashPassword(req.password));
                    UserHandler.getInstance().updateUser(u);
                    ApiResponse<Void> resp = new ApiResponse<>(true, "Reset success", null);
                    return Response.status(Response.Status.OK).entity(resp).build();
                }
            }
            ApiResponse<Void> resp = new ApiResponse<>(false, "User does not  exist", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();

    }

    @POST
    @Path("/generate_login_token")
    public Response forgot_password(TokenRequest req) {
        User user = UserHandler.getInstance().getUser(req.userId);
        if (user == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "User does not  exist", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }

        ApiResponse<String> resp = new ApiResponse<>(true, "Token generated successfully",
                JwtUtil.generateLoginToken(user.getUsername()));
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class TokenRequest {
        public Integer userId;
    }

}

