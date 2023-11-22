import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import controller.Controller;


/**
 * The `Main` class represents the entry point of the application. It initializes the Model,
 * View, and Controller components and starts the application.
 */
public class Main {
  /**
   * The entry point of the application.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {
    if (args.length > 1 && args[0].equals("-file")) {
      // Check if the correct option is provided
      StringBuilder argsStringBuilder = new StringBuilder();
      for (String arg : args) {
        argsStringBuilder.append(arg).append(" ");
      }
      Reader rd = new StringReader(argsStringBuilder.toString()); // Use StringReader with the args string
      // Pass the StringReader to the Controller
      Controller controller = new Controller(rd);
      controller.runProgram();
      return; // Exit the program after running the Controller
    }
    Reader rd = new InputStreamReader(System.in);
    Controller controller = new Controller( rd);
    controller.runProgram();
  }
}