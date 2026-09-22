package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Exercise2Router is a class that configures an Apache Camel route
 * to read messages from a direct:start endpoint and print them to console
 * using the log component.
 */
@ApplicationScoped
public class Exercise2Router extends RouteBuilder {

    private static final String MESSAGE_TEXT = "Mensaje de texto fijo desde la ruta";

    @ConfigProperty(name = "app.route.exercise2.enabled", defaultValue = "false")
    boolean enabled;

    @Override
    public void configure() throws Exception {
        // Route that reads from direct:start endpoint, sets a fixed text message,
        // and logs it to console using the log component
        from("direct:start")
                .autoStartup(enabled)
                .setBody(constant(MESSAGE_TEXT))
                .log("Contenido del mensaje: ${body}")
                .routeId("exercise2-route");

        // Producer route that triggers the direct:start route once after startup
        from("timer:exercise2-test?repeatCount=1&delay=1000")
                .autoStartup(enabled)
                .to("direct:start")
                .routeId("exercise2-producer-route");
    }
}
