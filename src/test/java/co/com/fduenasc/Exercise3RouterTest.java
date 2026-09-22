package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;

@QuarkusTest
class Exercise3RouterTest {

    @Inject
    ProducerTemplate producerTemplate;

    @Test
    void shouldTransformToUppercase() {
        String response = producerTemplate.requestBody("direct:uppercase", "Hola Mundo desde Apache Camel", String.class);

        assertEquals("HOLA MUNDO DESDE APACHE CAMEL", response);
    }

    @Test
    void shouldHandleNullBodyWithoutThrowing() {
        String response = producerTemplate.requestBody("direct:uppercase", null, String.class);

        assertEquals(null, response);
    }
}
