package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import Model.IModel;
import View.IView;

/**
 * The `Controller.Controller` class serves as the controller in the Model.Model-View-Controller.Controller (MVC)
 * architectural pattern.
 * It handles user interactions from the view, processes user input, and communicates with the
 * model and view components.
 */
public class Controller implements ActionListener {
  private final IModel model;
  private final IView view;

  /**
   * Constructs a new Controller.Controller instance.
   *
   * @param m The model to work with.
   * @param v The view to interact with.
   */
  public Controller(IModel m, IView v) {
    model = m;
    view = v;
    view.setListener(this);
    view.display();
  }

  /**
   * Handles user actions, such as button clicks and text input.
   *
   * @param e An ActionEvent representing the user's action.
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (Objects.equals(e.getActionCommand(), "Execute Button")) {
      // Read the text from the input textField
      String inputText = view.getInputString();

      // Check if the input text represents a file path
      File file = new File(inputText);
      if (file.exists() && file.isFile()) {
        try {
          // Read the contents of the file and display them in the view
          StringBuilder fileContents = new StringBuilder();
          try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
              fileContents.append(line).append("\n");
            }
          }

          // Pass the file contents to the model for processing
          model.executeScriptFromFile(inputText);

          // Display any output or result from the model in the view
          String result = model.getResult(); // This method depends on your model structure
          view.setEchoOutput(result);
        } catch (IOException ex) {
          // Handle any exceptions that occur during file reading
          view.setEchoOutput("Error reading the file: " + ex.getMessage());
        }
      } else {
        // Send the text to the model
        model.setString(inputText);

        // Clear the input textField
        view.clearInputString();

        // Finally, echo the string in the view
        String text = model.getString();
        view.setEchoOutput(text);
      }
    } else if (Objects.equals(e.getActionCommand(), "Exit Button")) {
      view.closeOrDispose();
      Scanner scanner = new Scanner(System.in);
      while (true) {
        System.out.print("Enter a command (or type 'exit' to quit): ");
        String command = scanner.nextLine();
        if (command.equals("exit")) {
          break;
        }
        try {
          model.parseAndExecute(command);
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }
}


