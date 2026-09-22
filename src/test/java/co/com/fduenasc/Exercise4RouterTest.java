package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.junit.jupiter.api.Test;

@QuarkusTest
class Exercise4RouterTest {

    @Inject
    CamelContext camelContext;

    @Inject
    ProducerTemplate producerTemplate;

    @Test
    void shouldRouteAdminMessage() {
        String message = "This is an admin message";

        String response = producerTemplate.requestBody("direct:choice", message, String.class);

        assertEquals(message, response);
    }

    @Test
    void shouldRouteUserMessage() {
        String message = "This is a user message";

        String response = producerTemplate.requestBody("direct:choice", message, String.class);

        assertEquals(message, response);
    }

    @Test
    void shouldRouteMessageWithoutKeywordsToDefault() {
        String message = "This is a regular message";

        String response = producerTemplate.requestBody("direct:choice", message, String.class);

        assertEquals(message, response);
    }

    @Test
    void shouldExposeExercise4Route() {
        Route route = camelContext.getRoute("exercise4-route");

        assertNotNull(route);
        assertEquals("Started", camelContext.getRouteController().getRouteStatus("exercise4-route").name());
    }
}
