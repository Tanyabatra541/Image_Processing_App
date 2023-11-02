package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import controller.Controller;

/**
 * The `Model` class represents the application's model. The model interacts with
 * various image operations through the `ImageOperations` interface to perform image-related
 * tasks.
 */
public class Model implements IModel {
  private String input;
  private String result; // Store the result from processing the file

//  static ImageOperations imageObj = null;

  /**
   * Initializes the model with default values.
   */
  public Model() {
    input = "";
    result = "";
  }

  /**
   * Sets the input string.
   *
   * @param i The input string.
   */
  @Override
  public void setString(String i) {
    input = i;
  }

  /**
   * Gets the input string.
   *
   * @return The input string.
   */
  @Override
  public String getString() {
    return input;
  }

  /**
   * Executes a script loaded from a file, processing each line as a command.
   *
   * @param scriptFilename The filename of the script to execute.
   */
  public void executeScriptFromFile(String scriptFilename) {
    try {
      File scriptFile = new File(scriptFilename);
      if (!scriptFile.exists()) {
        System.out.println("Script file not found: " + scriptFilename);
        return;
      }

      Scanner sc = new Scanner(scriptFile);
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (!line.startsWith("#") && !line.isEmpty()) { // Skip comments and empty lines
          Controller.parseAndExecute(line);
        }
      }
      sc.close();
    } catch (FileNotFoundException e) {
      System.out.println("Error reading script file: " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the result from processing commands, if available.
   *
   * @return The result string obtained from command execution.
   */
  public String getResult() {
    return result;
  }

  /**
   * Identifies the file format of an image based on its file extension.
   *
   * @param filePath The path to the image file.
   * @return The file format or null if the format is unsupported or not recognized.
   */
  public String identifyFileFormat(String filePath) {
    // Get the index of the last dot in the file path
    int lastDotIndex = filePath.lastIndexOf('.');

    if (lastDotIndex > 0) {
      // Extract the substring after the last dot
      String fileExtension = filePath.substring(lastDotIndex + 1);

      // Convert the file extension to lowercase for consistency
      return fileExtension.toLowerCase();
    } else {
      // No file extension found
      return null;
    }
  }

}