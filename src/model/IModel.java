package model;


/**
 * The `IModel` interface defines the contract for the model
 * component of the application,
 * responsible for handling and processing data and commands.
 */
public interface IModel {

  /**
   * Sets the input string in the model.
   *
   * @param i The input string to be set in the model.
   */
  void setString(String i);

  /**
   * Retrieves the stored input string from the model.
   *
   * @return The stored input string.
   */
  String getString();

  /**
   * Retrieves the result of the most recent operation or command execution.
   *
   * @return The result of the operation or command.
   */
  String getResult();


}