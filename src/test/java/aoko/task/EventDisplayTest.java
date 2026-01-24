package aoko.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventDisplayTest {

    @Test
    void display_sameDayFromAndToWithTime_showsToAsTimeOnly() {
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), true,
                LocalDateTime.of(2019, 8, 6, 16, 0), true);

        String shown = event.display();
        assertTrue(shown.contains("(from: Aug 06 2019 14:00 to: 16:00)"));
    }
}
