package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Exercise5Router is a class that configures an Apache Camel route
 * to dynamically invoke different REST APIs using toD based on user type.
 */
@ApplicationScoped
public class Exercise5Router extends RouteBuilder {

    private static final String DIRECT_DYNAMIC_API_ENDPOINT = "direct:dynamic-api";

    @ConfigProperty(name = "app.route.exercise5.enabled", defaultValue = "false")
    boolean enabled;
    
    // API endpoints (using mock APIs for demonstration)
    private static final String API_A_URL = "https://httpbin.org/post"; // Mock API for user
    private static final String API_B_URL = "https://httpbin.org/put";  // Mock API for admin

    @Override
    public void configure() throws Exception {
        // Global error handler for HTTP exceptions
        onException(org.apache.camel.http.base.HttpOperationFailedException.class)
                .handled(true)
                .log("ERROR: HTTP operation failed")
                .to("log:error?level=ERROR&showBody=true&showException=true")
                .end();
        
        // Route that reads from direct:dynamic-api endpoint and routes messages
        // to different REST APIs based on user type using dynamic toD
        from(DIRECT_DYNAMIC_API_ENDPOINT)
                .autoStartup(enabled)
                .choice()
                    // If user type is "user", call API A
                    .when(simple("${body} contains 'user'"))
                        .log("Routing to API A for user type")
                        .setHeader("User-Type", constant("user"))
                        .setHeader("CamelHttpMethod", constant("POST"))
                        .toD(API_A_URL + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                        .log("Response from API A: ${body}")
                    // If user type is "admin", call API B
                    .when(simple("${body} contains 'admin'"))
                        .log("Routing to API B for admin type")
                        .setHeader("User-Type", constant("admin"))
                        .setHeader("CamelHttpMethod", constant("PUT"))
                        .toD(API_B_URL + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                        .log("Response from API B: ${body}")
                    // For any other value, send to error log
                    .otherwise()
                        .log("ERROR: Unknown user type - ${body}")
                        .to("log:error?level=ERROR&showBody=true")
                        .log("Message sent to error log: ${body}")
                .end()
                .routeId("exercise5-route");
        
        // Test route that sends sample messages with different user types
        from("timer:test-dynamic-api-user?repeatCount=1&delay=2000")
                .autoStartup(enabled)
                .setBody(constant("user:john.doe@example.com"))
                .to(DIRECT_DYNAMIC_API_ENDPOINT)
                .routeId("exercise5-test-route-user");
        
        from("timer:test-dynamic-api-admin?repeatCount=1&delay=3000")
                .autoStartup(enabled)
                .setBody(constant("admin:admin@example.com"))
                .to(DIRECT_DYNAMIC_API_ENDPOINT)
                .routeId("exercise5-test-route-admin");
        
        from("timer:test-dynamic-api-error?repeatCount=1&delay=4000")
                .autoStartup(enabled)
                .setBody(constant("guest:guest@example.com"))
                .to(DIRECT_DYNAMIC_API_ENDPOINT)
                .routeId("exercise5-test-route-error");
    }
}
