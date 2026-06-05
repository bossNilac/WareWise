package warewise.server.api.resources.model;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.AuthContext;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.LogHandler;
import warewise.server.common.model.Log;
import warewise.server.common.model.User;

import java.util.List;

@Path("/logs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LogsResource {

    @GET
    @Path("/get_logs")
    public Response getInventory(@Context HttpHeaders headers) {
        User user = AuthContext.currentUser(headers);
        List<Log> logs = LogHandler.getInstance().getAllLogs();
        if (!AuthContext.isAdmin(user) && !AuthContext.isManager(user)) {
            logs = user == null ? List.of() : logs.stream()
                    .filter(log -> user.getUsername().equals(log.getUsername()))
                    .toList();
        }
        String data = JsonSerializer.serializeListToJson(logs);
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }
}
