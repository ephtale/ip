public class Aoko {
    public static void main(String[] args) {
        System.out.println("____________________________________");
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        System.out.println("____________________________________");

        while(true) { 
            String userInput = System.console().readLine();
            if (userInput.toLowerCase().equals("bye")) {
                break;
            }
            System.out.println("____________________________________");
            System.out.println(userInput + "\n");
            System.out.println("____________________________________");
        }

        System.out.println("____________________________________");
        System.out.println("See you again soon!\n");
        System.out.println("____________________________________");
    }
}
