package model;

import java.io.IOException;

/**
 * The `Model.Model.IModel` interface defines the contract for the model
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
   * Parses and executes a command provided as a string.
   *
   * @param command The command to be parsed and executed.
   * @throws IOException If an I/O error occurs during command execution.
   */
  void parseAndExecute(String command) throws IOException;

  /**
   * Executes a script from a file specified by the script filename.
   *
   * @param scriptFilename The name of the script file to be executed.
   */
  void executeScriptFromFile(String scriptFilename);

  /**
   * Retrieves the result of the most recent operation or command execution.
   *
   * @return The result of the operation or command.
   */
  String getResult();

}