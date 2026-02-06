package aoko.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parseCommand_trimsAndSplitsCommandAndRemainder() {
        Parser.ParsedCommand parsed = Parser.parseCommand("   todo   read book  ");
        assertEquals(Parser.Command.TODO, parsed.command);
        assertEquals("read book", parsed.remainder);
    }

    @Test
    void parseCommand_unknownCommand_mapsToUnknown() {
        Parser.ParsedCommand parsed = Parser.parseCommand("wat is this");
        assertEquals(Parser.Command.UNKNOWN, parsed.command);
        assertEquals("is this", parsed.remainder);
    }

    @Test
    void parseIndex_missingOrNonNumeric_returnsNull() {
        assertNull(Parser.parseIndex(new String[] { "mark" }));
        assertNull(Parser.parseIndex(new String[] { "mark", "abc" }));
        assertNull(Parser.parseIndex(new String[] { "mark", "" }));
        assertNull(Parser.parseIndex(new String[] { "mark", "  " }));
    }

    @Test
    void parseDateOnly_acceptsIsoDate() {
        LocalDate date = Parser.parseDateOnly("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), date);
    }

    @Test
    void parseDateOnly_acceptsDmyDate() {
        LocalDate date = Parser.parseDateOnly("2/12/2019");
        assertEquals(LocalDate.of(2019, 12, 2), date);
    }

    @Test
    void parseDateOnly_rejectsDateTimeAndInvalidDates() {
        assertNull(Parser.parseDateOnly("2019-10-15 1200"));
        assertNull(Parser.parseDateOnly("2019-02-29"));
    }

    @Test
    void parseDateTime_acceptsIsoDate_dateOnly() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("2019-06-06");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), parsed.dateTime);
        assertFalse(parsed.hasTime);
    }

    @Test
    void parseDateTime_acceptsIsoDateTime_hhmm() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("2019-08-06 1400");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), parsed.dateTime);
        assertTrue(parsed.hasTime);
    }

    @Test
    void parseDateTime_acceptsIsoDateTime_hhColonMm() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("2019-08-06 14:30");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 30), parsed.dateTime);
        assertTrue(parsed.hasTime);
    }

    @Test
    void parseDateTime_trimsWhitespace() {
        Parser.ParsedDateTime parsed = Parser.parseDateTime("  2019-08-06 14:30  ");
        assertNotNull(parsed);
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 30), parsed.dateTime);
        assertTrue(parsed.hasTime);
    }

    @Test
    void parseDateTime_acceptsLeapDay_onLeapYearOnly() {
        assertNotNull(Parser.parseDateTime("2020-02-29"));
        assertNull(Parser.parseDateTime("2019-02-29"));
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
    void parseEventEnd_acceptsTimeOnly_hColonMm() {
        Parser.ParsedDateTime from = Parser.parseDateTime("2019-08-06 1400");
        assertNotNull(from);

        Parser.ParsedDateTime to = Parser.parseEventEnd(from, "9:05");
        assertNotNull(to);
        assertEquals(LocalDateTime.of(2019, 8, 6, 9, 5), to.dateTime);
        assertTrue(to.hasTime);
    }

    @Test
    void parseEventEnd_blankOrInvalidTime_returnsNull() {
        Parser.ParsedDateTime from = Parser.parseDateTime("2019-08-06");
        assertNotNull(from);

        assertNull(Parser.parseEventEnd(from, ""));
        assertNull(Parser.parseEventEnd(from, "   "));
        assertNull(Parser.parseEventEnd(from, "2360"));
        assertNull(Parser.parseEventEnd(from, "25:00"));
        assertNull(Parser.parseEventEnd(from, "not-a-time"));
    }

    @Test
    void parseEventEndTreats2400AsMidnight() {
        Parser.ParsedDateTime from = Parser.parseDateTime("2019-08-06 1400");
        assertNotNull(from);

        Parser.ParsedDateTime to2400 = Parser.parseEventEnd(from, "2400");
        assertNotNull(to2400);
        assertEquals(LocalDateTime.of(2019, 8, 6, 0, 0), to2400.dateTime);
        assertTrue(to2400.hasTime);

        Parser.ParsedDateTime to24Colon00 = Parser.parseEventEnd(from, "24:00");
        assertNotNull(to24Colon00);
        assertEquals(LocalDateTime.of(2019, 8, 6, 0, 0), to24Colon00.dateTime);
        assertTrue(to24Colon00.hasTime);
    }

    @Test
    void parseEventEnd_fullDateTimeOrDate_isAccepted() {
        Parser.ParsedDateTime from = Parser.parseDateTime("2019-08-06 1400");
        assertNotNull(from);

        Parser.ParsedDateTime toFull = Parser.parseEventEnd(from, "2019-08-07 1600");
        assertNotNull(toFull);
        assertEquals(LocalDateTime.of(2019, 8, 7, 16, 0), toFull.dateTime);
        assertTrue(toFull.hasTime);

        Parser.ParsedDateTime toDateOnly = Parser.parseEventEnd(from, "2019-08-07");
        assertNotNull(toDateOnly);
        assertEquals(LocalDateTime.of(2019, 8, 7, 0, 0), toDateOnly.dateTime);
        assertFalse(toDateOnly.hasTime);
    }

    @Test
    void parseIsoDateOrDateTime_acceptsIsoDateAndIsoDateTime() {
        Parser.ParsedDateTime dateOnly = Parser.parseIsoDateOrDateTime("2019-08-06");
        assertNotNull(dateOnly);
        assertEquals(LocalDateTime.of(2019, 8, 6, 0, 0), dateOnly.dateTime);
        assertFalse(dateOnly.hasTime);

        Parser.ParsedDateTime dateTime = Parser.parseIsoDateOrDateTime("2019-08-06T14:30");
        assertNotNull(dateTime);
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 30), dateTime.dateTime);
        assertTrue(dateTime.hasTime);
    }

    @Test
    void parseIsoDateOrDateTime_invalid_returnsNull() {
        assertNull(Parser.parseIsoDateOrDateTime(""));
        assertNull(Parser.parseIsoDateOrDateTime("2019-08-06T25:00"));
        assertNull(Parser.parseIsoDateOrDateTime("not-a-date"));
    }

    @Test
    void parseDateTime_invalid_returnsNull() {
        assertNull(Parser.parseDateTime("not-a-date"));
        assertNull(Parser.parseDateTime("2019-13-40"));
        assertNull(Parser.parseDateTime(""));
        assertNull(Parser.parseDateTime("   "));
    }
}
