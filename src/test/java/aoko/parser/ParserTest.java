package aoko.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parseDateOnly_acceptsIsoDate() {
        LocalDate date = Parser.parseDateOnly("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), date);
    }

    @Test
    void parseDateTime_acceptsIsoDate_dateOnly() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("2019-06-06");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), parsed.dateTime);
        assertEquals(false, parsed.hasTime);
    }

    @Test
    void parseDateTime_acceptsDmyDateTime_hasTimeTrue() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("2/12/2019 1800");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.dateTime);
        assertTrue(parsed.hasTime);
    }

    @Test
    void parseEventEnd_acceptsTimeOnly_endUsesSameDateAsStart() {
        Parser.ParsedDateTime from = Parser.parseDateTime("2019-08-06 1400");
        assertNotNull(from);

        Parser.ParsedDateTime to = Parser.parseEventEnd(from, "1600");
        assertNotNull(to);
        assertEquals(LocalDateTime.of(2019, 8, 6, 16, 0), to.dateTime);
        assertTrue(to.hasTime);
    }

    @Test
    void parseDateTime_invalid_returnsNull() {
        assertNull(Parser.parseDateTime("not-a-date"));
        assertNull(Parser.parseDateTime("2019-13-40"));
        assertNull(Parser.parseDateTime(""));
        assertNull(Parser.parseDateTime("   "));
    }
}
