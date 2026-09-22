package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;

@QuarkusTest
class Exercise2RouterTest {

    @Inject
    ProducerTemplate producerTemplate;

    @Test
    void shouldReplaceMessageWithFixedText() {
        String response = producerTemplate.requestBody("direct:start", "original message", String.class);

        assertEquals("Mensaje de texto fijo desde la ruta", response);
    }
}
