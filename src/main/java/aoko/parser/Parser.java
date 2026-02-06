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
        assert userInput != null : "User input must not be null";
        String[] parts = userInput.trim().split("\\s+", 2);
        assert parts.length >= 1 : "Split should always return at least one token";
        Command command = Command.parse(parts[0]);
        assert command != null : "Command.parse must not return null";
        String remainder = parts.length > 1 ? parts[1].trim() : "";
        assert remainder != null : "Remainder must not be null";
        return new ParsedCommand(command, parts, remainder);
    }

    /**
     * Parses a 1-based task index from a tokenized command.
     *
     * @return Parsed index, or {@code null} if invalid.
     */
    public static Integer parseIndex(String[] parts) {
        assert parts != null : "Tokenized parts must not be null";
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
     */
    public static ParsedDateTime parseEventEnd(ParsedDateTime fromParsed, String rawTo) {
        assert fromParsed != null : "Event start must be parsed before parsing an end";
        assert fromParsed.dateTime != null : "Parsed start date/time must not be null";
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
