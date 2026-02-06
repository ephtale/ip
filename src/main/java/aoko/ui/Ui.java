package aoko.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import aoko.task.Task;
import aoko.task.TaskList;

/**
 * Handles all user-facing output formatting for the chatbot.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern(
            "MMM dd yyyy",
            Locale.ENGLISH);

    private final PrintStream out;

    /**
     * Creates a UI that writes to standard output.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a UI that writes to the given output stream.
     */
    public Ui(PrintStream out) {
        assert out != null : "Output stream must not be null";
        this.out = out;
    }

    /**
     * Prints the divider line.
     */
    public void showLine() {
        assert out != null : "Output stream must not be null";
        out.println(LINE);
    }

    /**
     * Prints the welcome message.
     */
    public void showWelcome() {
        showLine();
        out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        showLine();
    }

    /**
     * Prints the goodbye message.
     */
    public void showBye() {
        showLine();
        out.println("See you again soon!\n");
        showLine();
    }

    /**
     * Prints confirmation of an added task.
     */
    public void showAdded(Task task, int newSize) {
        assert task != null : "Added task must not be null";
        assert newSize >= 0 : "Task list size must not be negative";
        showLine();
        out.println("Got it. I've added this task:");
        out.println("  " + task.display());
        out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

    /**
     * Prints all tasks currently in the list.
     */
    public void showList(TaskList tasks) {
        assert tasks != null : "Task list must not be null";
        showLine();
        out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            out.println((i + 1) + "." + tasks.get(i).display());
        }
        showLine();
    }

    /**
     * Prints confirmation of a task being marked done.
     */
    public void showMarked(Task task) {
        assert task != null : "Marked task must not be null";
        showLine();
        out.println("Nice! I've marked this task as done:");
        out.println("  " + task.display());
        showLine();
    }

    /**
     * Prints confirmation of a task being marked not done.
     */
    public void showUnmarked(Task task) {
        assert task != null : "Unmarked task must not be null";
        showLine();
        out.println("OK, I've marked this task as not done yet:");
        out.println("  " + task.display());
        showLine();
    }

    /**
     * Prints confirmation of a task being deleted.
     */
    public void showDeleted(Task removed, int newSize) {
        assert removed != null : "Removed task must not be null";
        assert newSize >= 0 : "Task list size must not be negative";
        showLine();
        out.println("Noted. I've removed this task:");
        out.println("  " + removed.display());
        out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

    /**
     * Prints tasks that match a given date.
     */
    public void showOn(LocalDate date, List<Task> matches) {
        assert date != null : "Date must not be null";
        assert matches != null : "Matches list must not be null";
        showLine();
        if (matches.isEmpty()) {
            out.println("No tasks found on " + date.format(DISPLAY_DATE_ONLY) + ".");
            showLine();
            return;
        }

        out.println("Here are the tasks on " + date.format(DISPLAY_DATE_ONLY) + ":");
        for (int i = 0; i < matches.size(); i++) {
            assert matches.get(i) != null : "Matches must not contain null tasks";
            out.println((i + 1) + "." + matches.get(i).display());
        }
        showLine();
    }

    /**
     * Prints tasks that match a given keyword.
     */
    public void showFind(List<Task> matches) {
        assert matches != null : "Matches list must not be null";
        showLine();
        if (matches.isEmpty()) {
            out.println("No matching tasks found.");
            showLine();
            return;
        }

        out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            assert matches.get(i) != null : "Matches must not contain null tasks";
            out.println((i + 1) + "." + matches.get(i).display());
        }
        showLine();
    }

    /**
     * Prints a message for unrecognized commands.
     */
    public void showUnknownCommand() {
        showLine();
        out.println("That's not a command I recognize.");
        out.println("Available commands: list, mark, unmark, delete, todo, deadline, event, on, find, bye");
        showLine();
    }

    /**
     * Prints one or more lines surrounded by divider lines.
     */
    public void showMessageBlock(String... lines) {
        assert lines != null : "Lines array must not be null";
        showLine();
        for (String line : lines) {
            assert line != null : "Message lines must not be null";
            out.println(line);
        }
        showLine();
    }
}
