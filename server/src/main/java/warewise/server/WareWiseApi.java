package warewise.server;

import org.glassfish.jersey.server.ResourceConfig;
import warewise.server.api.ApiSecurityFilter;

public class WareWiseApi extends ResourceConfig {
    public WareWiseApi() {
        packages("warewise.server.api.resources"); // where @Path classes are
        register(ApiSecurityFilter.class);
    }
}
