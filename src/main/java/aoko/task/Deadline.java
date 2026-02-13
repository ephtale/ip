package aoko.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be done by a specific date/time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern(
            "MMM dd yyyy HH:mm",
            Locale.ENGLISH);
    private static final DateTimeFormatter KEY_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter KEY_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates a deadline task.
     *
     * @param description Task description.
     * @param by Deadline date/time.
     * @param hasTime Whether the original input included a time.
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        assert by != null : "Deadline date/time must not be null";
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns the deadline date/time.
     */
    public LocalDateTime getBy() {
        assert by != null : "Deadline date/time must not be null";
        return by;
    }

    /**
     * Returns whether the deadline includes a time component.
     */
    public boolean hasTime() {
        return hasTime;
    }

    @Override
    protected String typeIcon() {
        return "[D]";
    }

    @Override
    protected String taskDetails() {
        assert by != null : "Deadline date/time must not be null";
        String formatted = hasTime ? by.format(DISPLAY_DATE_TIME) : by.toLocalDate().format(DISPLAY_DATE);
        return description + " (by: " + formatted + ")";
    }

    @Override
    protected String uniqueDetailsKeyExtras() {
        assert by != null : "Deadline date/time must not be null";
        String byKey = hasTime ? by.format(KEY_DATE_TIME) : by.toLocalDate().format(KEY_DATE);
        return "|" + byKey + "|" + hasTime;
    }
}
