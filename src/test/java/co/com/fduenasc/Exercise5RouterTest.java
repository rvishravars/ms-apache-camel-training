package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.junit.jupiter.api.Test;

@QuarkusTest
class Exercise5RouterTest {

    @Inject
    CamelContext camelContext;

    @Test
    void shouldDefineDynamicApiRouteWithoutStartingExternalCalls() {
        Route route = camelContext.getRoute("exercise5-route");

        assertNotNull(route);
        assertTrue(route.getEndpoint().getEndpointUri().contains("dynamic-api"));
        assertFalse(camelContext.getRouteController().getRouteStatus("exercise5-route").isStarted());
    }
}
