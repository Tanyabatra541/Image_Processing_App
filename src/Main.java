import Controller.Controller;
import Model.Model;
import View.IView;
import View.JFrameView;

/**
 * The `Main` class represents the entry point of the application. It initializes the Model.Model,
 * View, and Controller.Controller components and starts the application.
 */
public class Main {
  /**
   * The entry point of the application.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {
    IModel model = new Model();
    IView view = new JFrameView("Echo a string");
    Controller controller = new Controller(model, view);
  }
}
