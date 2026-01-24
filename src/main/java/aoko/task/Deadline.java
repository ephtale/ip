package aoko.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    private final LocalDateTime by;
    private final boolean hasTime;

    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    public LocalDateTime getBy() {
        return by;
    }

    public boolean hasTime() {
        return hasTime;
    }

    @Override
    protected String typeIcon() {
        return "[D]";
    }

    @Override
    protected String taskDetails() {
        String formatted = hasTime ? by.format(DISPLAY_DATE_TIME) : by.toLocalDate().format(DISPLAY_DATE);
        return description + " (by: " + formatted + ")";
    }
}
