import java.io.InputStreamReader;
import java.io.Reader;

import controller.Controller;
import view.IView;
import view.JFrameView;


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
    Reader rd = new InputStreamReader(System.in);
    Controller controller = new Controller( rd);
    controller.runProgram();
  }
}