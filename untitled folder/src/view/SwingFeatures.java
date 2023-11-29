//package view;
//
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//
//import controller.Controller;
//
///**
// * This example shows the different user interface elements in Java Swing.
// * Please use these examples as guidelines only to see how to use them. This
// * example has not been designed very well, it is only meant to illustrate code
// * snippets.
// *
// * Feel free to try out different modifications to see how the program changes
// */
//
//public class SwingFeatures {
//
//  public static void main(String[] args) {
//
//    SwingFeaturesFrame frame = new SwingFeaturesFrame();
//    Controller controller = new Controller(frame);
//    controller.addFeaturesToView(controller);
//
//    try {
//
//      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
//
//    } catch (UnsupportedLookAndFeelException e) {
//      // handle exception
//    } catch (ClassNotFoundException e) {
//      // handle exception
//    } catch (InstantiationException e) {
//      // handle exception
//    } catch (IllegalAccessException e) {
//      // handle exception
//    } catch (Exception e) {
//    }
//
//  }
//
//}
package view;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controller.Controller;

public class SwingFeatures {

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
        if (args.length >= 2) {
          String scriptFilePath = args[1];
          // Execute script file and shut down
          executeScriptFile(scriptFilePath);
        } else {
          System.out.println("Usage: java -jar Program.jar -file path-of-script-file");
        }
        break;
      case "-text":
        // Open in interactive text mode
        openTextMode();
        break;
      default:
        System.out.println("Usage: java -jar Program.jar -file path-of-script-file");
        System.out.println("       java -jar Program.jar -text");
    }
  }

  private static void executeScriptFile(String scriptFilePath) {
    // Implement the logic to read and execute the script file
    System.out.println("Executing script file: " + scriptFilePath);
    // Your code here
    System.exit(0);
  }

  private static void openTextMode() {
    // Implement the logic to open in interactive text mode
    System.out.println("Opening in interactive text mode");
    // Your code here
    System.exit(0);
  }

  private static void launchGUI() {
    SwingFeaturesFrame frame = new SwingFeaturesFrame();
    Controller controller = new Controller(frame);
    controller.addFeaturesToView(controller);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }

    // Implement the logic to open the graphical user interface
  }
}

