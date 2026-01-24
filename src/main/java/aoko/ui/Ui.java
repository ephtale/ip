package aoko.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import aoko.task.Task;
import aoko.task.TaskList;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    public void showLine() {
        System.out.println(LINE);
    }

    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        showLine();
    }

    public void showBye() {
        showLine();
        System.out.println("See you again soon!\n");
        showLine();
    }

    public void showAdded(Task task, int newSize) {
        showLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.display());
        System.out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

    public void showList(TaskList tasks) {
        showLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).display());
        }
        showLine();
    }

    public void showMarked(Task task) {
        showLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task.display());
        showLine();
    }

    public void showUnmarked(Task task) {
        showLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task.display());
        showLine();
    }

    public void showDeleted(Task removed, int newSize) {
        showLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removed.display());
        System.out.println("Now you have " + newSize + " tasks in the list.");
        showLine();
    }

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

    public void showUnknownCommand() {
        showLine();
        System.out.println("That's not a command I recognize.");
        System.out.println("Available commands: list, mark, unmark, delete, todo, deadline, event, on, bye");
        showLine();
    }

    public void showMessageBlock(String... lines) {
        showLine();
        for (String line : lines) {
            System.out.println(line);
        }
        showLine();
    }
}
