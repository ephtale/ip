package aoko.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    public Event(String description, LocalDateTime from, boolean fromHasTime, LocalDateTime to, boolean toHasTime) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromHasTime = fromHasTime;
        this.toHasTime = toHasTime;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public boolean fromHasTime() {
        return fromHasTime;
    }

    public boolean toHasTime() {
        return toHasTime;
    }

    @Override
    protected String typeIcon() {
        return "[E]";
    }

    @Override
    protected String taskDetails() {
        String formattedFrom = fromHasTime ? from.format(DISPLAY_DATE_TIME) : from.toLocalDate().format(DISPLAY_DATE);

        String formattedTo;
        if (toHasTime && fromHasTime && from.toLocalDate().equals(to.toLocalDate())) {
            formattedTo = to.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH));
        } else {
            formattedTo = toHasTime ? to.format(DISPLAY_DATE_TIME) : to.toLocalDate().format(DISPLAY_DATE);
        }

        return description + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }
}
