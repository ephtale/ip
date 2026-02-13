package aoko.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event task with a start and end date/time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern(
            "MMM dd yyyy HH:mm",
            Locale.ENGLISH);
    private static final DateTimeFormatter KEY_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter KEY_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    /**
     * Creates an event task.
     *
     * @param description Task description.
     * @param from Start date/time.
     * @param fromHasTime Whether the start includes a time.
     * @param to End date/time.
     * @param toHasTime Whether the end includes a time.
     */
    public Event(String description, LocalDateTime from, boolean fromHasTime, LocalDateTime to, boolean toHasTime) {
        super(description);
        assert from != null : "Event start must not be null";
        assert to != null : "Event end must not be null";
        assert !to.isBefore(from) : "Event end must not be before start";
        this.from = from;
        this.to = to;
        this.fromHasTime = fromHasTime;
        this.toHasTime = toHasTime;
    }

    /**
     * Returns the event start date/time.
     */
    public LocalDateTime getFrom() {
        assert from != null : "Event start must not be null";
        return from;
    }

    /**
     * Returns the event end date/time.
     */
    public LocalDateTime getTo() {
        assert to != null : "Event end must not be null";
        return to;
    }

    /**
     * Returns whether the start includes a time component.
     */
    public boolean fromHasTime() {
        return fromHasTime;
    }

    /**
     * Returns whether the end includes a time component.
     */
    public boolean toHasTime() {
        return toHasTime;
    }

    @Override
    protected String typeIcon() {
        return "[E]";
    }

    @Override
    protected String taskDetails() {
        assert from != null : "Event start must not be null";
        assert to != null : "Event end must not be null";
        assert !to.isBefore(from) : "Event end must not be before start";
        String formattedFrom = fromHasTime ? from.format(DISPLAY_DATE_TIME) : from.toLocalDate().format(DISPLAY_DATE);

        String formattedTo;
        if (toHasTime && fromHasTime && from.toLocalDate().equals(to.toLocalDate())) {
            formattedTo = to.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH));
        } else {
            formattedTo = toHasTime ? to.format(DISPLAY_DATE_TIME) : to.toLocalDate().format(DISPLAY_DATE);
        }

        return description + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }

    @Override
    protected String uniqueDetailsKeyExtras() {
        assert from != null : "Event start must not be null";
        assert to != null : "Event end must not be null";
        String fromKey = fromHasTime ? from.format(KEY_DATE_TIME) : from.toLocalDate().format(KEY_DATE);
        String toKey = toHasTime ? to.format(KEY_DATE_TIME) : to.toLocalDate().format(KEY_DATE);
        return "|" + fromKey + "|" + fromHasTime + "|" + toKey + "|" + toHasTime;
    }
}
