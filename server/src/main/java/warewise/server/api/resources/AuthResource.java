package warewise.server.api.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.JwtUtil;
import warewise.server.api.NotificationService;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.encryption.Encrypt;
import warewise.server.common.handler.ForgotPasswordHandler;
import warewise.server.common.handler.UserHandler;
import warewise.server.common.model.ForgotPasswordToken;
import warewise.server.common.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @POST
    @Path("/login")
    public Response login(AuthResource.LoginRequest req){
        for  (User u: UserHandler.getInstance().getAllUsers()){
            if(u.getUsername().equals(req.username)){
                if(Encrypt.verifyPassword(u.getPasswordHash(),req.password)){
                    ApiResponse<String> resp = new ApiResponse<>(true, "Login success", JwtUtil.generateLoginToken(req.username));
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
    public Response forgot_password(@PathParam("token") String token, AuthResource.LoginRequest req) {

        if(ForgotPasswordHandler.getInstance().getTokenByToken(token).isExpired()){
            ApiResponse<Void> resp = new ApiResponse<>(true, "Token Invalid", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }

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

        String token = JwtUtil.generateLoginToken(user.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String exp = now.format(formatter);
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken(req.userId,token,exp);
        ForgotPasswordHandler.getInstance().addToken(forgotPasswordToken);
        NotificationService.notifyForgotPassword(user,"/forgot_password-{token}");

        ApiResponse<String> resp = new ApiResponse<>(true, "Token generated successfully",
                token);
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

