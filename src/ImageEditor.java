import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controller.Controller;
import view.ImageEditorView;

public class ImageEditor {

  public static void main(String[] args) {
    if (args.length > 0) {
      handleCommandLineArguments(args);
    } else {
      launchGUI();
    }
  }

  private static void handleCommandLineArguments(String[] args) {
    switch (args[0]) {
      case "-file":
        if (args.length == 2) {
          String scriptFilePath = args[1];
          // Execute script file and shut down
          executeScriptFile(scriptFilePath);
        } else {
          System.out.println("Invalid command line arguments.");
        }
        break;
      case "-text":
        // Open in interactive text mode
        openTextMode();
        break;
      default:
        System.out.println("Invalid command line arguments.");
    }
  }

  private static void executeScriptFile(String scriptFilePath) {
    // Implement the logic to read and execute the script file
    System.out.println("Executing script file: " + scriptFilePath);
    try (FileReader reader = new FileReader(scriptFilePath)) {
      Controller controller = new Controller(reader);
      controller.executeCommands();
    } catch (IOException e) {
      System.out.println("Error reading file");
    }
    // Your code here
    System.exit(0);
  }

  private static void openTextMode() {
    // Implement the logic to open in interactive text mode
    System.out.println("Opening in interactive text mode");
    Reader reader = new InputStreamReader(System.in);
    Controller controller = new Controller(reader);
    controller.executeCommands();
    // Your code here
    System.exit(0);
  }

  private static void launchGUI() {
    ImageEditorView frame = new ImageEditorView();
    Controller controller = new Controller(null);
    controller.setView(frame);
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
