package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Exercise4Router is a class that configures an Apache Camel route
 * to route messages based on their content using the choice EIP pattern.
 */
@ApplicationScoped
public class Exercise4Router extends RouteBuilder {

    private static final String DIRECT_CHOICE_ENDPOINT = "direct:choice";

    @ConfigProperty(name = "app.route.exercise4.enabled", defaultValue = "false")
    boolean enabled;

    @Override
    public void configure() throws Exception {
        // Route that reads from direct:choice endpoint and routes messages
        // based on their content using the choice EIP pattern
        from(DIRECT_CHOICE_ENDPOINT)
                .autoStartup(enabled)
                .choice()
                    // If message contains "admin", route to log:admin
                    .when(simple("${body} contains 'admin'"))
                        .to("log:admin?level=INFO&showBody=true")
                        .log("Message routed to admin log: ${body}")
                    // If message contains "user", route to log:user
                    .when(simple("${body} contains 'user'"))
                        .to("log:user?level=INFO&showBody=true")
                        .log("Message routed to user log: ${body}")
                    // Otherwise, route to log:default
                    .otherwise()
                        .to("log:default?level=INFO&showBody=true")
                        .log("Message routed to default log: ${body}")
                .end()
                .routeId("exercise4-route");
        
        // Test route that sends sample messages to direct:choice for demonstration
        from("timer:test-choice?repeatCount=1&delay=2000")
                .autoStartup(enabled)
                .process(exchange -> {
                    // Send different types of messages to test the choice routing
                    String[] testMessages = {
                        "This is an admin message",
                        "This is a user message",
                        "This is a regular message"
                    };
                    exchange.getIn().setBody(testMessages[0]);
                })
                .to(DIRECT_CHOICE_ENDPOINT)
                .routeId("exercise4-test-route-1");
        
        // Additional test routes for different message types
        from("timer:test-choice-user?repeatCount=1&delay=3000")
                .autoStartup(enabled)
                .setBody(constant("This is a user message"))
                .to(DIRECT_CHOICE_ENDPOINT)
                .routeId("exercise4-test-route-2");
        
        from("timer:test-choice-default?repeatCount=1&delay=4000")
                .autoStartup(enabled)
                .setBody(constant("This is a regular message without keywords"))
                .to(DIRECT_CHOICE_ENDPOINT)
                .routeId("exercise4-test-route-3");
    }
}
