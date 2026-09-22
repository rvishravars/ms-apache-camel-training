package co.com.fduenasc;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * GameOfThronesRouter is a class that configures an Apache Camel route
 * to send JSON messages describing Game of Thrones characters to RabbitMQ.
 */
@ApplicationScoped
public class GameOfThronesRouter extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(GameOfThronesRouter.class);

    @ConfigProperty(name = "app.route.gameofthrones.enabled", defaultValue = "false")
    boolean enabled;

    // Constants for JSON field names
    private static final String FIELD_NAME = "name";
    private static final String FIELD_HOUSE = "house";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_STATUS = "status";

    // Constants for character status
    private static final String STATUS_DECEASED = "Deceased";
    private static final String STATUS_ALIVE = "Alive";

    @Override
    public void configure() {
        // Route that sends 7 JSON messages to RabbitMQ with Game of Thrones characters
        // The ConnectionFactory bean will be automatically detected by the spring-rabbitmq component
        from("timer:got-characters?repeatCount=1&delay=2000")
                .autoStartup(enabled)
                .process(exchange -> {
                    // Create the 7 characters from different houses
                    Map<String, Object>[] characters = createGameOfThronesCharacters();
                    exchange.getIn().setBody(characters);
                    LOGGER.info("Prepared " + characters.length + " Game of Thrones characters to send to RabbitMQ");
                })
                .split(body())
                    .marshal().json(JsonLibrary.Jackson)
                    .to("spring-rabbitmq:got-exchange?exchangeType=topic&routingKey=character")
                    .log("Sent character to RabbitMQ: ${body}")
                .end();

        // Consume character messages from RabbitMQ and log them to the application console.
        from("spring-rabbitmq:got-exchange?exchangeType=topic&queues=character-queue&routingKey=character&autoDeclare=true&arg.queue.durable=true")
                .autoStartup(enabled)
                .log("Received character from RabbitMQ: ${body}")
                .delay(1000);
    }

    /**
     * Creates an array of 7 Game of Thrones characters from different houses.
     * @return Array of character maps
     */
    Map<String, Object>[] createGameOfThronesCharacters() {
        @SuppressWarnings("unchecked")
        Map<String, Object>[] characters = new Map[7];

        // 1. Stark Family
        Map<String, Object> nedStark = new HashMap<>();
        nedStark.put(FIELD_NAME, "Eddard Stark");
        nedStark.put(FIELD_HOUSE, "Stark");
        nedStark.put(FIELD_TITLE, "Lord of Winterfell");
        nedStark.put(FIELD_DESCRIPTION, "Honorable Warden of the North, known for his strong sense of justice");
        nedStark.put(FIELD_STATUS, STATUS_DECEASED);
        characters[0] = nedStark;

        // 2. Lannister Family
        Map<String, Object> tyrionLannister = new HashMap<>();
        tyrionLannister.put(FIELD_NAME, "Tyrion Lannister");
        tyrionLannister.put(FIELD_HOUSE, "Lannister");
        tyrionLannister.put(FIELD_TITLE, "Hand of the Queen");
        tyrionLannister.put(FIELD_DESCRIPTION, "The Imp, known for his wit and intelligence despite his stature");
        tyrionLannister.put(FIELD_STATUS, STATUS_ALIVE);
        characters[1] = tyrionLannister;

        // 3. Targaryen Family
        Map<String, Object> daenerysTargaryen = new HashMap<>();
        daenerysTargaryen.put(FIELD_NAME, "Daenerys Targaryen");
        daenerysTargaryen.put(FIELD_HOUSE, "Targaryen");
        daenerysTargaryen.put(FIELD_TITLE, "Queen of the Andals and the First Men");
        daenerysTargaryen.put(FIELD_DESCRIPTION, "The Mother of Dragons, breaker of chains");
        daenerysTargaryen.put(FIELD_STATUS, STATUS_DECEASED);
        characters[2] = daenerysTargaryen;

        // 4. Baratheon Family
        Map<String, Object> robertBaratheon = new HashMap<>();
        robertBaratheon.put(FIELD_NAME, "Robert Baratheon");
        robertBaratheon.put(FIELD_HOUSE, "Baratheon");
        robertBaratheon.put(FIELD_TITLE, "King of the Seven Kingdoms");
        robertBaratheon.put(FIELD_DESCRIPTION, "The Usurper, known for his strength in battle");
        robertBaratheon.put(FIELD_STATUS, STATUS_DECEASED);
        characters[3] = robertBaratheon;

        // 5. Greyjoy Family
        Map<String, Object> theonGreyjoy = new HashMap<>();
        theonGreyjoy.put(FIELD_NAME, "Theon Greyjoy");
        theonGreyjoy.put(FIELD_HOUSE, "Greyjoy");
        theonGreyjoy.put(FIELD_TITLE, "Prince of Winterfell");
        theonGreyjoy.put(FIELD_DESCRIPTION, "Reek, torn between his birth family and the Starks who raised him");
        theonGreyjoy.put(FIELD_STATUS, STATUS_DECEASED);
        characters[4] = theonGreyjoy;

        // 6. Tyrell Family
        Map<String, Object> margaeryTyrell = new HashMap<>();
        margaeryTyrell.put(FIELD_NAME, "Margaery Tyrell");
        margaeryTyrell.put(FIELD_HOUSE, "Tyrell");
        margaeryTyrell.put(FIELD_TITLE, "Queen of the Seven Kingdoms");
        margaeryTyrell.put(FIELD_DESCRIPTION, "The Queen of Thorns' granddaughter, known for her political acumen");
        margaeryTyrell.put(FIELD_STATUS, STATUS_DECEASED);
        characters[5] = margaeryTyrell;

        // 7. Martell Family
        Map<String, Object> oberynMartell = new HashMap<>();
        oberynMartell.put(FIELD_NAME, "Oberyn Martell");
        oberynMartell.put(FIELD_HOUSE, "Martell");
        oberynMartell.put(FIELD_TITLE, "Prince of Dorne");
        oberynMartell.put(FIELD_DESCRIPTION, "The Red Viper, known for his combat skills and passionate nature");
        oberynMartell.put(FIELD_STATUS, STATUS_DECEASED);
        characters[6] = oberynMartell;

        return characters;
    }
}
