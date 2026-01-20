import java.util.ArrayList;
import java.util.List;

public class Aoko {
    public static void main(String[] args) {
        System.out.println("____________________________________");
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        System.out.println("____________________________________");

        List<String> tasks = new ArrayList<>();

        while(true) { 
            String userInput = System.console().readLine();
            Boolean exit = false;
            
            switch (userInput.toLowerCase()) {
                case "list" -> {
                    System.out.println("____________________________________");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i));
                    }
                    System.out.println("____________________________________");
                }
                case "bye" -> {
                    exit = true;
                }
                default -> {
                    System.out.println("____________________________________");
                    System.out.println("added: " + userInput + "\n");
                    System.out.println("____________________________________");
                    tasks.add(userInput);
                }
            }
            if (exit) {
                break;
            }
        }

        System.out.println("____________________________________");
        System.out.println("See you again soon!\n");
        System.out.println("____________________________________");
    }
}
