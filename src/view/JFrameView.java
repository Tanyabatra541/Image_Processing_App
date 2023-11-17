package view;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * This class is an implementation of the `IView` interface that provides a graphical user
 * interface (GUI) based on the `JFrame` window. It allows users to input text, display output,
 * and interact with buttons.
 */
public class JFrameView extends JFrame implements IView {
  private final JLabel display;
  private final JButton echoButton;
  private final JButton exitButton;
  private final JTextField input;

  /**
   * Constructs a `JFrameView` with the specified caption for the window.
   *
   * @param caption The caption or title of the window.
   */
  public JFrameView(String caption) {
    super(caption);

    setSize(500, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    this.setLayout(new FlowLayout());

    display = new JLabel("Enter file path:");


    this.add(display);

    //the textField
    input = new JTextField(10);
    this.add(input);

    //echobutton
    echoButton = new JButton("Run script File");
    echoButton.setActionCommand("Execute Button");
    this.add(echoButton);

    //exit button
    exitButton = new JButton("Go to Command Prompt");
    exitButton.setActionCommand("Exit Button");
    this.add(exitButton);

    pack();
  }

  /**
   * Displays the `JFrameView`, making it visible to the user.
   */
  @Override
  public void display() {
    setVisible(true);
  }

  /**
   * Closes or disposes of the `JFrameView`, typically for closing the view window.
   */
  @Override
  public void closeOrDispose() {
    setVisible(false);
    dispose();
  }


  /**
   * Sets an `ActionListener` to handle actions in the view.
   *
   * @param listener The `ActionListener` to be set for handling actions.
   */
  @Override
  public void setListener(ActionListener listener) {
    echoButton.addActionListener(listener);
    exitButton.addActionListener(listener);
  }

  /**
   * Sets the label to display the specified string content.
   *
   * @param s The string content to be displayed.
   */
  @Override
  public void setEchoOutput(String s) {
    display.setText(s);
  }

  /**
   * Retrieves the input string from the text input field.
   *
   * @return The text retrieved from the input field.
   */
  @Override
  public String getInputString() {
    return input.getText();
  }

  /**
   * Clears the content of the input string in the text input field.
   */
  @Override
  public void clearInputString() {
    input.setText("");
  }
}