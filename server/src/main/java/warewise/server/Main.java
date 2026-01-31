package warewise.server;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import warewise.server.common.handler.UserHandler;
import warewise.server.common.model.User;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        String base = System.getenv().getOrDefault("BASE_URI", "http://0.0.0.0:8080/api");
        URI uri = URI.create(base);

        ResourceConfig rc = new ResourceConfig()
                // IMPORTANT: this matches your screenshot
                .packages("warewise.server.api.resources")
                .register(JacksonFeature.class)
                // Logs every request + response status to console
                .register(new LoggingFeature(Logger.getLogger("Jersey"), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192));



        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri,rc, new WareWiseApi() ,false);
        server.start();



        System.out.println(" Server started at " + uri);
        System.out.println(" Scanning JAX-RS resources in: warewise.server.api.resources");

        Thread.currentThread().join(); // keep alive
    }
}
