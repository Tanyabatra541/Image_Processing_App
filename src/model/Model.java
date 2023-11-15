package model;

/**
 * The `Model` class represents the application's model. The model interacts with
 * various image operations through the `ImageOperations` interface to perform image-related
 * tasks.
 */
public class Model implements IModel {
  private String input;
  private final String result; // Store the result from processing the file


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
   * Retrieves the result from processing commands, if available.
   *
   * @return The result string obtained from command execution.
   */
  public String getResult() {
    return result;
  }

}