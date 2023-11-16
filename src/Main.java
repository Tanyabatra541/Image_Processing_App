import controller.Controller;
//import model.IModel;
//import model.Model;
import view.IView;
import view.JFrameView;


/**
 * The `Main` class represents the entry point of the application. It initializes the Model,
 * View, and controller.Controller.controller.Controller components and starts the application.
 */
public class Main {
  /**
   * The entry point of the application.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {
    IView view = new JFrameView("Echo a string");
    Controller controller = new Controller( view);
  }
}