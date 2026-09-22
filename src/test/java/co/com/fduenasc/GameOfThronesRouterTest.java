package co.com.fduenasc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GameOfThronesRouterTest {

    private final GameOfThronesRouter router = new GameOfThronesRouter();

    @Test
    void shouldCreateSevenCharacters() {
        Map<String, Object>[] characters = router.createGameOfThronesCharacters();

        assertEquals(7, characters.length);
        assertTrue(Arrays.stream(characters).allMatch(character -> character != null));
    }

    @Test
    void shouldCreateCharactersWithRequiredFieldsAndDistinctHouses() {
        Map<String, Object>[] characters = router.createGameOfThronesCharacters();
        Set<String> houses = new HashSet<>();

        for (Map<String, Object> character : characters) {
            assertFalse(character.isEmpty());
            assertNotNull(character.get("name"));
            assertNotNull(character.get("house"));
            assertNotNull(character.get("title"));
            assertNotNull(character.get("description"));
            assertNotNull(character.get("status"));
            assertTrue(Set.of("Alive", "Deceased").contains(character.get("status")));
            houses.add((String) character.get("house"));
        }

        assertEquals(7, houses.size());
    }

    @Test
    void shouldIncludeExpectedCharacterDetails() {
        Map<String, Object>[] characters = router.createGameOfThronesCharacters();

        Map<String, Object> nedStark = Arrays.stream(characters)
                .filter(character -> "Eddard Stark".equals(character.get("name")))
                .findFirst()
                .orElseThrow();

        assertEquals("Stark", nedStark.get("house"));
        assertEquals("Lord of Winterfell", nedStark.get("title"));
        assertEquals("Deceased", nedStark.get("status"));
    }
}
