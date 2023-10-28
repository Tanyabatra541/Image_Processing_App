import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller implements ActionListener {
  private final IModel model;
  private final IView view;

  public Controller(IModel m, IView v) {
    model = m;
    view = v;
    view.setListener(this);
    view.display();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "Execute Button":
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
            model.processFileContents(fileContents.toString());

            // Display any output or result from the model in the view
            String result = model.getResult(); // This method depends on your model structure
            System.out.println(result);
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
        break;
      case "Exit Button":
        System.exit(0);
        break;
    }
  }




}
