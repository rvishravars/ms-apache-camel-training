package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Exercise1Router is a class that configures an Apache Camel route
 * to print sequential numbers starting from 0, every 3 seconds using the timer component.
 * Uses a counter that increments with each timer execution.
 */
@ApplicationScoped
public class Exercise1Router extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(Exercise1Router.class);
    private static final int TIMER_DELAY_MS = 3000; // 3 seconds

    @ConfigProperty(name = "app.route.exercise1.enabled", defaultValue = "false")
    boolean enabled;
    
    // Counter that starts at 0 and increments with each timer execution
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public void configure() throws Exception {
        // Route that prints sequential numbers starting from 0, every 3 seconds
        // The counter increments with each timer execution
        from("timer:exercise1?delay=" + TIMER_DELAY_MS + "&period=" + TIMER_DELAY_MS)
                .autoStartup(enabled)
                .process(exchange -> {
                    // Get and increment the counter (starts at 0)
                    long currentNumber = counter.getAndIncrement();
                    LOGGER.info("Número secuencial: " + currentNumber);
                });
    }
}
