import java.io.IOException;

public interface IModel {
  void setString(String i);

  String getString();

  void processFileContents(String fileContents);
  String getResult();

}
