package warewise.server;

import org.glassfish.jersey.server.ResourceConfig;

public class WareWiseApi extends ResourceConfig {
    public WareWiseApi() {
        packages("warewise.server.api.resources"); // where @Path classes are
    }
}
