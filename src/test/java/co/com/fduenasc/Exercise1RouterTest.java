package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.junit.jupiter.api.Test;

@QuarkusTest
class Exercise1RouterTest {

    @Inject
    CamelContext camelContext;

    @Test
    void shouldStartSequentialNumberTimerRoute() {
        Route route = camelContext.getRoutes().stream()
                .filter(candidate -> candidate.getEndpoint().getEndpointUri().contains("exercise1"))
                .findFirst()
                .orElse(null);

        assertNotNull(route);
        assertEquals("Started", camelContext.getRouteController().getRouteStatus(route.getId()).name());
    }
}
