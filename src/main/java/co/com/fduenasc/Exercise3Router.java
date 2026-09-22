package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Exercise3Router is a class that configures an Apache Camel route
 * to transform a text message to uppercase using a custom Processor.
 */
@ApplicationScoped
public class Exercise3Router extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(Exercise3Router.class);

    @ConfigProperty(name = "app.route.exercise3.enabled", defaultValue = "false")
    boolean enabled;

    @Override
    public void configure() throws Exception {
        // Route that reads from direct:uppercase endpoint, transforms the message
        // to uppercase using a custom Processor, and displays the result in console
        from("direct:uppercase")
                .autoStartup(enabled)
                .process(new UppercaseProcessor())
                .log("Texto transformado a mayúsculas: ${body}")
                .routeId("exercise3-route");
        
        // Test route that sends sample messages to direct:uppercase for demonstration
        from("timer:test-uppercase?repeatCount=10&delay=2000")
                .autoStartup(enabled)
                .setBody(constant("Hola Mundo desde Apache Camel - Ejercicio 3"))
                .to("direct:uppercase")
                .routeId("exercise3-test-route");
    }

    /**
     * Custom Processor that transforms the message body to uppercase.
     * Implements the Processor interface to provide reusable transformation logic.
     */
    private static class UppercaseProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            // Get the message body as String
            String body = exchange.getIn().getBody(String.class);
            
            // Handle null case
            if (body == null) {
                LOGGER.warn("Message body is null, cannot convert to uppercase");
                return;
            }
            
            // Convert text to uppercase
            String uppercaseText = body.toUpperCase();
            
            // Replace the message body with the transformed text
            exchange.getIn().setBody(uppercaseText);
            
            LOGGER.debug("Transformed text from '" + body + "' to '" + uppercaseText + "'");
        }
    }
}
