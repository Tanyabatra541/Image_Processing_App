import java.io.IOException;

public interface IModel {
  void setString(String i);

  String getString();

//  void processFileContents(String fileContents);

  void parseAndExecute(String command) throws IOException;
  void executeScriptFromFile(String scriptFilename);
  String getResult();

}
