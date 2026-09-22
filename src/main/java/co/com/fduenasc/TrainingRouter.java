package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * TrainingRouter is a class that configures a basic Apache Camel route
 * to print a message to the console.
 */
@ApplicationScoped
public class TrainingRouter extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(TrainingRouter.class);

    @ConfigProperty(name = "app.route.training.enabled", defaultValue = "false")
    boolean enabled;

    @Override
    public void configure() throws Exception {
        // Basic route that prints "The North Remembers!" to the console
        from("timer:training?repeatCount=10&delay=1000")
                .autoStartup(enabled)
                .setBody(constant("The North Remembers!"))
                .process(exchange -> {
                    String message = exchange.getIn().getBody(String.class);
                    LOGGER.info(message);
                });
    }
}
