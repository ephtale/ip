package aoko.ui;

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
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Prints the divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        showLine();
    }

    /**
     * Prints the goodbye message.
     */
    public void showBye() {
        showLine();
        System.out.println("See you again soon!\n");
        showLine();
    }

    /**
     * Prints confirmation of an added task.
     */
    public void showAdded(Task task, int newSize) {
        showLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.display());
        System.out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

    /**
     * Prints all tasks currently in the list.
     */
    public void showList(TaskList tasks) {
        showLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).display());
        }
        showLine();
    }

    /**
     * Prints confirmation of a task being marked done.
     */
    public void showMarked(Task task) {
        showLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task.display());
        showLine();
    }

    /**
     * Prints confirmation of a task being marked not done.
     */
    public void showUnmarked(Task task) {
        showLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task.display());
        showLine();
    }

    /**
     * Prints confirmation of a task being deleted.
     */
    public void showDeleted(Task removed, int newSize) {
        showLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removed.display());
        System.out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

    /**
     * Prints tasks that match a given date.
     */
    public void showOn(LocalDate date, List<Task> matches) {
        showLine();
        if (matches.isEmpty()) {
            System.out.println("No tasks found on " + date.format(DISPLAY_DATE_ONLY) + ".");
            showLine();
            return;
        }

        System.out.println("Here are the tasks on " + date.format(DISPLAY_DATE_ONLY) + ":");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + "." + matches.get(i).display());
        }
        showLine();
    }

    /**
     * Prints a message for unrecognized commands.
     */
    public void showUnknownCommand() {
        showLine();
        System.out.println("That's not a command I recognize.");
        System.out.println("Available commands: list, mark, unmark, delete, todo, deadline, event, on, bye");
        showLine();
    }

    /**
     * Prints one or more lines surrounded by divider lines.
     */
    public void showMessageBlock(String... lines) {
        showLine();
        for (String line : lines) {
            System.out.println(line);
        }
        showLine();
    }
}
