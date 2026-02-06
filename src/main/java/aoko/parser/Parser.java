package aoko.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses raw user input into command words, arguments, and date/time values.
 */
public class Parser {
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
    };

    private static final DateTimeFormatter[] DATE_ONLY_FORMATS = {
        DateTimeFormatter.ofPattern("d/M/yyyy"),
    };

    private static final DateTimeFormatter[] TIME_ONLY_FORMATS = {
        DateTimeFormatter.ofPattern("HHmm"),
        DateTimeFormatter.ofPattern("H:mm"),
    };

    /**
     * Supported top-level command words.
     */
    public enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, BYE, UNKNOWN;

        static Command parse(String token) {
            if (token == null) {
                return UNKNOWN;
            }
            return switch (token.toLowerCase()) {
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "on" -> ON;
            case "find" -> FIND;
            case "bye" -> BYE;
            default -> UNKNOWN;
            };
        }
    }

    /**
     * Represents a parsed user command, including the command word and remainder.
     */
    public static class ParsedCommand {
        public final Command command;
        public final String[] parts;
        public final String remainder;

        /**
         * Creates a parsed command.
         *
         * @param command Command word.
         * @param parts Tokenized raw input.
         * @param remainder Remainder after the command word.
         */
        ParsedCommand(Command command, String[] parts, String remainder) {
            this.command = command;
            this.parts = parts;
            this.remainder = remainder;
        }
    }

    /**
     * Represents a parsed date/time and whether the original input included a time component.
     */
    public static class ParsedDateTime {
        public final LocalDateTime dateTime;
        public final boolean hasTime;

        /**
         * Creates a parsed date/time.
         *
         * @param dateTime Parsed date/time value.
         * @param hasTime Whether the original input included a time component.
         */
        public ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }
    }

    /**
     * Parses a raw input line into a command word and remainder.
     */
    public static ParsedCommand parseCommand(String userInput) {
        String[] parts = userInput.trim().split("\\s+", 2);
        Command command = Command.parse(parts[0]);
        String remainder = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedCommand(command, parts, remainder);
    }

    /**
     * Parses a 1-based task index from a tokenized command.
     *
     * @return Parsed index, or {@code null} if invalid.
     */
    public static Integer parseIndex(String[] parts) {
        if (parts == null) {
            return null;
        }
        if (parts.length < 2) {
            return null;
        }
        try {
            return Integer.valueOf(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a date/time string into a {@link ParsedDateTime}.
     *
     * <p>Accepts date-only and date-time formats used by the chatbot.
     */
    public static ParsedDateTime parseDateTime(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(s, formatter), true);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        for (DateTimeFormatter formatter : DATE_ONLY_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(s, formatter);
                return new ParsedDateTime(date.atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        return null;
    }

    /**
     * Parses a date-only string.
     *
     * @return Parsed date, or {@code null} if invalid.
     */
    public static LocalDate parseDateOnly(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        for (DateTimeFormatter formatter : DATE_ONLY_FORMATS) {
            try {
                return LocalDate.parse(s, formatter);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        return null;
    }

    /**
     * Parses an ISO date or ISO date-time used in persisted storage.
     */
    public static ParsedDateTime parseIsoDateOrDateTime(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        if (s.contains("T")) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses the end of an event.
     *
     * <p>Supports full date/time or time-only end (time-only uses the start date).
     */
    public static ParsedDateTime parseEventEnd(ParsedDateTime fromParsed, String rawTo) {
        ParsedDateTime full = parseDateTime(rawTo);
        if (full != null) {
            return full;
        }

        String s = rawTo == null ? "" : rawTo.trim();
        if (s.isEmpty()) {
            return null;
        }

        LocalTime time = null;
        for (DateTimeFormatter formatter : TIME_ONLY_FORMATS) {
            try {
                time = LocalTime.parse(s, formatter);
                break;
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        if (time == null) {
            return null;
        }

        LocalDate date = fromParsed.dateTime.toLocalDate();
        return new ParsedDateTime(LocalDateTime.of(date, time), true);
    }
}
