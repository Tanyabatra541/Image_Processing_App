import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class JFrameView extends JFrame implements IView {
  private JLabel display;
  private JButton echoButton, exitButton;
  private JTextField input;

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
  
  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void closeOrDispose() {
    setVisible(false);
    dispose();
  }


  @Override
  public void setListener(ActionListener listener) {
    echoButton.addActionListener(listener);
    exitButton.addActionListener(listener);
  }

  @Override
  public void setEchoOutput(String s) {
    display.setText(s);
  }

  @Override
  public String getInputString() {
    return input.getText();
  }

  @Override
  public void clearInputString() {
    input.setText("");
  }
}
