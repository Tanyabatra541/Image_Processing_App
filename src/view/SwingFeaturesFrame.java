package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;
import controller.ControllerFeatures;

/**
 * This class opens the main window, that has different elements illustrated in
 * it. It also doubles up as all the listeners for simplicity. Such a design is
 * not recommended in general.
 */

public class SwingFeaturesFrame extends JFrame implements ItemListener, ListSelectionListener {

  private JPanel mainPanel;
  private JPanel bmwPanel = new JPanel();
  private JPanel compressPanel = new JPanel();
  private JPanel comboboxPanel;
  private JScrollPane mainScrollPane;

  private JLabel checkboxDisplay;
  private JLabel radioDisplay;
  private JLabel comboboxDisplay;
  private JLabel colorChooserDisplay;
  private JLabel fileOpenDisplay;
  private JLabel fileSaveDisplay;
  private JLabel inputDisplay;
  private JLabel optionDisplay;

  private JList<String> listOfStrings;
  private JList<Integer> listOfIntegers;
  private String[] images = new String[]{};
  private JPanel imagePanel = new JPanel();
  JLabel[] imageLabel;
  JScrollPane[] imageScrollPane;
  JTextField compressionPercentage;

  JTextField bNumericField;

  JTextField splitField;
  JTextField mNumericField;
  JTextField wNumericField;
  JPanel sliderPanel;

  int sliderValue = 0;
  String selectedFilter;
  private JButton fileOpenButtonforLoad;
  private JComboBox<String> combobox;
  private JButton fileSaveButton;
  private JButton applyFilterButton;
  private JSlider arrowSlider;

  private String command;

  private void createArrowSlider() {
    arrowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    JLabel percentageLabel = new JLabel("Split Percentage: " + arrowSlider.getValue() + "%");
    percentageLabel.setBounds(10, 60, 150, 20); // Adjust the bounds as needed

    arrowSlider.addChangeListener(e -> {
      sliderValue = arrowSlider.getValue();
      percentageLabel.setText("Split Percentage: " + sliderValue + "%");
      System.out.println("Slider value: " + sliderValue);
    });
    arrowSlider.setMajorTickSpacing(20);
    arrowSlider.setMinorTickSpacing(5);
    arrowSlider.setPaintTicks(true);
    arrowSlider.setPaintLabels(true);

    // Assuming imageLabel[1] is set at this point
    JLabel imageLabel = this.imageLabel[1];

    // Create a new panel to hold the slider
    sliderPanel = new JPanel(null); // Use absolute positioning
    int panelWidth = 600; // Set your desired width
    int panelHeight = 50; // Set your desired height
    sliderPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

    // Set the bounds for the slider within the panel
    //arrowSlider.setBounds(554, 0, panelWidth - 150, panelHeight);
    percentageLabel.setBounds(panelWidth*2, 0, 150, panelHeight);


    arrowSlider.setBounds(panelWidth-panelHeight, 0, panelWidth, panelHeight);


    // Set the bounds for the slider within the panel
    // arrowSlider.setBounds(554, 0, 600 , sliderHeight);

    // Add the slider to the new panel
    sliderPanel.add(arrowSlider);
    // Add the label to the slider panel
    sliderPanel.add(percentageLabel);

    // Add the new panel to mainPanel
    mainPanel.add(sliderPanel);
    sliderPanel.setVisible(false);
  }

  private void addSlider() {
    System.out.println("in slider panel");
    if(Objects.equals(selectedFilter, "levels-adjust") || Objects.equals(selectedFilter, "color-correct") ||
            Objects.equals(selectedFilter, "blur") || Objects.equals(selectedFilter, "sepia") ||
            Objects.equals(selectedFilter, "sharpen")){
      sliderPanel.setVisible(true);
     // sliderValue=0;
    }else{
      sliderValue=0;
      sliderPanel.setVisible(false);
    }
  }

  public void updateImageForIndex(int[][][] rgbValues,int index) {
    BufferedImage image = convertRGBtoBufferedImage(rgbValues);
    // Zoom in the image by 50%
    int scaledWidth = (int) (image.getWidth() * 1.5);
    int scaledHeight = (int) (image.getHeight() * 1.5);

    // Create a scaled version of the image
    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

    // Set the loaded and scaled image to the JLabel at the specified index
    imageLabel[index].setIcon(new ImageIcon(scaledImage));
    imageLabel[index].setText(null);
    // Repaint the components
    imagePanel.repaint();
    imageLabel[index].repaint();
    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private BufferedImage convertRGBtoBufferedImage(int[][][] rgbData) {
    int height = rgbData.length;
    int width = rgbData[0].length;

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = rgbData[y][x][0];
        int g = rgbData[y][x][1];
        int b = rgbData[y][x][2];

        int rgb = (r << 16) | (g << 8) | b;

        bufferedImage.setRGB(x, y, rgb);
      }
    }

    return bufferedImage;
  }

  public void setImg(String[] imgList) {
    images = imgList;

    for (int i = 0; i < imageLabel.length; i++) {
      imageLabel[i] = new JLabel();
      imageScrollPane[i] = new JScrollPane(imageLabel[i]);
      // imageLabel[i].setIcon(new ImageIcon(images[i]));
      imageLabel[i].setIcon(new ImageIcon("path/to/placeholder-image.png"));

      imageLabel[i].setHorizontalAlignment(JLabel.CENTER); // Set text alignment to the center
      imageLabel[i].setVerticalAlignment(JLabel.CENTER);
      imageScrollPane[i].setPreferredSize(new Dimension(100, 600));
      imagePanel.add(imageScrollPane[i]);
    }
    imageLabel[0].setText("Please upload image");
    imageLabel[1].setText("Please select filter");
    imageLabel[2].setText("Please upload image");
    imageLabel[0].setBorder(BorderFactory.createTitledBorder("Original Image"));
    imageLabel[1].setBorder(BorderFactory.createTitledBorder("Processed Image"));
    imageLabel[2].setBorder(BorderFactory.createTitledBorder("Current Histogram"));
  }

  public SwingFeaturesFrame() {
    super();
    setTitle("Image Processing");
    setSize(1700, 1700);


    mainPanel = new JPanel();
    //for elements to be arranged vertically within this panel
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    //scroll bars around this main panel
    mainScrollPane = new JScrollPane(mainPanel);
    add(mainScrollPane);

    JPanel dialogBoxesPanelForLoad = new JPanel();
    dialogBoxesPanelForLoad.setBorder(BorderFactory.createTitledBorder("Load new Image:"));
    dialogBoxesPanelForLoad.setLayout(new BoxLayout(dialogBoxesPanelForLoad, BoxLayout.PAGE_AXIS));
    mainPanel.add(dialogBoxesPanelForLoad);

    JPanel fileopenPanelForLoad = new JPanel();
    fileopenPanelForLoad.setLayout(new FlowLayout());
    dialogBoxesPanelForLoad.add(fileopenPanelForLoad);
    fileOpenButtonforLoad = new JButton("Open a file");
    fileopenPanelForLoad.add(fileOpenButtonforLoad);
    fileOpenDisplay = new JLabel("File path will appear here");
    fileopenPanelForLoad.add(fileOpenDisplay);


    //show an image with a scrollbar

    //a border around the panel with a caption
    imagePanel.setBorder(BorderFactory.createTitledBorder("Processing your image:"));
    imagePanel.setLayout(new GridLayout(1, 0, 10, 10));
    //imagePanel.setMaximumSize(null);
    mainPanel.add(imagePanel);
    images = new String[]{"Jellyfish.jpg", "Koala.jpg", "Penguins.jpg"};
    imageLabel = new JLabel[images.length];
    imageScrollPane = new JScrollPane[images.length];

    setImg(images);
    createArrowSlider();
    //dialog boxes

    comboboxPanel = new JPanel();
    comboboxPanel.setBorder(BorderFactory.createTitledBorder("Processing Operations:"));
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(comboboxPanel);

    comboboxDisplay = new JLabel("Which filter do you want?");
    comboboxPanel.add(comboboxDisplay);
    String[] options = {"<None>", "horizontal-flip", "vertical-flip", "blur", "sharpen", "red-component", "blue-component", "green-component", "luma-component", "sepia", "compress",
            "color-correct", "levels-adjust", "Split"};
    combobox = new JComboBox<String>();
    for (int i = 0; i < options.length; i++) {
      combobox.addItem(options[i]);
    }

    comboboxPanel.add(combobox);
    mainPanel.add(comboboxPanel);

    applyFilterButton = new JButton("Apply Filter");

    mainPanel.add(applyFilterButton);

    bNumericField = new JTextField(3);
    mNumericField = new JTextField(3);
    wNumericField = new JTextField(3);

    bmwPanel.add(new JLabel("Enter B, M, W:"));
    bmwPanel.add(bNumericField);
    bmwPanel.add(mNumericField);
    bmwPanel.add(wNumericField);
    comboboxPanel.add(bmwPanel);
    bmwPanel.setVisible(false);

    compressionPercentage = new JTextField(3);

    compressPanel.add(new JLabel("Enter compression percentage:"));
    compressPanel.add(compressionPercentage);

    comboboxPanel.add(compressPanel);
    compressPanel.setVisible(false);


//dialog boxes
    JPanel dialogBoxesPanel = new JPanel();
    dialogBoxesPanel.setBorder(BorderFactory.createTitledBorder("Save Processed Image:"));
    dialogBoxesPanel.setLayout(new BoxLayout(dialogBoxesPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(dialogBoxesPanel);


    //file save
    JPanel filesavePanel = new JPanel();
    filesavePanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(filesavePanel);
    fileSaveButton = new JButton("Save a file");
    filesavePanel.add(fileSaveButton);
    fileSaveDisplay = new JLabel("File path will appear here");
    filesavePanel.add(fileSaveDisplay);

    setVisible(true);

  }


  public void addFeatures(ControllerFeatures features){
    fileOpenButtonforLoad.addActionListener(evt -> features.loadImage(openFile(), "img"));
    applyFilterButton.addActionListener(evt -> features.applyFeatures(filterOptions(), "dest"));
    fileSaveButton.addActionListener(evt -> features.saveImage(saveFile()));
    arrowSlider.addChangeListener(e -> {
      sliderValue = arrowSlider.getValue();
      System.out.println("Slider value: " + sliderValue);
      features.applyFeatures(filterOptions(), "dest");

    });
  }

  public String openFile(){
    command = null;
    final JFileChooser fchooser = new JFileChooser(".");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images", "jpg", "ppm", "png");
    fchooser.setFileFilter(filter);
    int retvalue = fchooser.showOpenDialog(SwingFeaturesFrame.this);
    if (retvalue == JFileChooser.APPROVE_OPTION) {
      File f = fchooser.getSelectedFile();
      fileOpenDisplay.setText(f.getAbsolutePath());
      System.out.println(f.getAbsolutePath());

        command = "load " + f.getAbsolutePath() + " img";
        System.out.println(command);
      sliderValue = 0;
      arrowSlider.setValue(0);

    }
    return command;
  }

  public String filterOptions(){
    selectedFilter = (String) combobox.getSelectedItem();
    comboboxDisplay.setText("You selected: " + selectedFilter);

    System.out.println("Selected option: " + selectedFilter);
    compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
    bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
    String command = null;
    System.out.println("Filter options");
      switch (Objects.requireNonNull(selectedFilter)){
        case "horizontal-flip":
          selectedFilter = "horizontal-flip";
          command = selectedFilter + " img dest";
          break;
        case "vertical-flip":
          selectedFilter = "vertical-flip";
          command = selectedFilter + " img dest";
          break;
        case "blur":
          selectedFilter = "blur";
          command = selectedFilter + " img dest";
          break;
        case "sharpen":
          selectedFilter = "sharpen";
          if (sliderValue != 0) {
            System.out.println("in the if condition");
            command = selectedFilter + " img dest split "+sliderValue;
          } else {
            command = selectedFilter + " img dest";
          }
          System.out.println("sharpen command" + command);
          break;
        case "red-component":
          selectedFilter = "red-component";
          command = selectedFilter + " img dest";
          break;
        case "blue-component":
          selectedFilter = "blue-component";
          command = selectedFilter + " img dest";
          break;
        case "green-component":
          selectedFilter = "green-component";
          command = selectedFilter + " img dest";
          break;
        case "luma-component":
          selectedFilter = "luma-component";
          command = selectedFilter + " img dest";
          break;
        case "sepia":
          selectedFilter = "sepia";
          if (sliderValue != 0) {
            System.out.println("in the if condition");
            command = selectedFilter + " img dest split "+sliderValue;
          } else {
            command = selectedFilter + " img dest";
          }
          System.out.println("sepia command" + command);
          break;
        case "compress":
          selectedFilter = "compress";
          String enteredText = compressionPercentage.getText();

          System.out.println("Entered Compression Percentage: " + enteredText);
          command = selectedFilter + " " + enteredText + " img dest";
          if (sliderValue != 0) {
            command = command.concat(" split " + sliderValue);
          }
          break;
        case "color-correct":
          selectedFilter = "color-correct";
          command = selectedFilter + " img dest";
          break;
        case "levels-adjust":
          selectedFilter = "levels-adjust";
          String bValue = bNumericField.getText();
          String mValue = mNumericField.getText();
          String wValue = wNumericField.getText();

          // Now you can use these values as needed
          System.out.println("B Value: " + bValue);
          System.out.println("M Value: " + mValue);
          System.out.println("W Value: " + wValue);
          command = selectedFilter + " " + bValue + " " + mValue + " " + wValue + " img dest";
          if (sliderValue != 0) {
            command = command.concat(" split " + sliderValue);
          }
          break;
        case "Split":
          selectedFilter = "Split";
          break;
        default:
          selectedFilter = "None";
          break;
      }
      comboboxDisplay.setText("You selected: " + selectedFilter);

      System.out.println("Selected option: " + selectedFilter);
    addSlider();
    return command;
  }

  public String saveFile(){
    String command = null;
    final JFileChooser fchooser = new JFileChooser(".");
    int retvalue = fchooser.showSaveDialog(SwingFeaturesFrame.this);
    if (retvalue == JFileChooser.APPROVE_OPTION) {
      File f = fchooser.getSelectedFile();
      fileSaveDisplay.setText(f.getAbsolutePath());
      command = "save " + f.getAbsolutePath() + " dest";
      System.out.println("Image saved");
    }
    return command;
  }

  @Override
  public void itemStateChanged(ItemEvent arg0) {
    // TODO Auto-generated method stub
    String who = ((JCheckBox) arg0.getItemSelectable()).getActionCommand();

    switch (who) {
      case "CB1":
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          checkboxDisplay.setText("Check box 1 was selected");
        } else {
          checkboxDisplay.setText("Check box 1 was deselected");
        }
        break;
      case "CB2":
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          checkboxDisplay.setText("Check box 2 was selected");
        } else {
          checkboxDisplay.setText("Check box 2 was deselected");
        }
        break;
      case "CB3":
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          checkboxDisplay.setText("Check box 3 was selected");
        } else {
          checkboxDisplay.setText("Check box 3 was deselected");
        }
        break;
      case "CB4":
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          checkboxDisplay.setText("Check box 4 was selected");
        } else {
          checkboxDisplay.setText("Check box 4 was deselected");
        }
        break;

      case "CB5":
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          checkboxDisplay.setText("Check box 5 was selected");
        } else {
          checkboxDisplay.setText("Check box 5 was deselected");
        }
        break;

    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    // We don't know which list called this callback, because we're using it
    // for two lists.  In practice, you should use separate listeners
    JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
            "The source object is " + e.getSource(), "Source", JOptionPane.PLAIN_MESSAGE);
    // Regardless, the event information tells us which index was selected
    JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
            "The changing index is " + e.getFirstIndex(), "Index", JOptionPane.PLAIN_MESSAGE);
    // This gets us the string value that's currently selected
    JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
            "The current string item is " + this.listOfStrings.getSelectedValue(), "Selected string", JOptionPane.PLAIN_MESSAGE);
    // This gets us the integer value that's currently selected
    JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
            "The current number item is " + this.listOfIntegers.getSelectedValue(), "Selected integer", JOptionPane.PLAIN_MESSAGE);
  }
}
