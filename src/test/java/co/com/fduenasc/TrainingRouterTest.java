package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TrainingRouterTest {

    @Inject
    CamelContext camelContext;

    @Test
    void shouldStartTrainingTimerRoute() {
        Route route = camelContext.getRoutes().stream()
                .filter(candidate -> candidate.getEndpoint().getEndpointUri().contains("training"))
                .findFirst()
                .orElse(null);

        assertNotNull(route);
        assertEquals("Started", camelContext.getRouteController().getRouteStatus(route.getId()).name());
    }
}
