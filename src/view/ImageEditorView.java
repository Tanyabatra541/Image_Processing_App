package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.ControllerFeatures;

/**
 * ImageEditorView class represents the graphical user interface (GUI) for the image processing
 * application. It includes features for loading, processing, and saving images, along with various
 * controls and display elements.
 */
public class ImageEditorView extends JFrame {
  private final JPanel mainPanel;
  private final JPanel bmwPanel;
  private JPanel compressPanel;
  private final JLabel comboboxDisplay;
  private JLabel fileOpenDisplay;
  private JLabel fileSaveDisplay;
  private JPanel imagePanel;
  private JLabel[] imageLabel;
  private JScrollPane[] imageScrollPane;
  private JTextField compressionPercentage;

  private JTextField bNumericField;
  private JTextField mNumericField;
  private JTextField wNumericField;
  private JPanel sliderPanel;
  private int sliderValue = 0;
  private String selectedFilter = null;
  private JButton fileOpenButtonforLoad;
  private JComboBox<String> combobox;
  private JButton fileSaveButton;
  private JButton applyFilterButton;
  private JSlider arrowSlider;
  private String command = null;
  private JButton testBMWButton;
  private JButton testCompressButton;
  private String fileExtension;

  private String sourceName = "img";
  private String destName = "img";

  private String tempName = "img";
  private String filteredImgName = "filteredImg";
  private String splitImageName = "splitImage";
  private boolean applySplitFilter = true;
  private int action = 0;

  /**
   * Constructs an instance of the ImageEditorView class, initializing the GUI components,
   * layout, and event handlers.
   */
  public ImageEditorView() {
    super();
    setTitle("Image Processing");
    setSize(1500, 1200);


    mainPanel = new JPanel();
    //for elements to be arranged vertically within this panel
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    //scroll bars around this main panel
    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    add(mainScrollPane);
    JButton helpButton = new JButton("Help");
    helpButton.setToolTipText("Click for help");
    helpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showHelpDialog();
      }
    });


    mainPanel.add(helpButton, BorderLayout.NORTH);


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
    imagePanel = new JPanel();
    imagePanel.setBorder(BorderFactory.createTitledBorder("Processing your image:"));
    imagePanel.setLayout(new GridLayout(1, 0, 10, 10));

    mainPanel.add(imagePanel);
    imageLabel = new JLabel[3];
    imageScrollPane = new JScrollPane[3];

    setImg();
    createArrowSlider();

    JPanel comboboxPanel = new JPanel();
    comboboxPanel.setBorder(BorderFactory.createTitledBorder("Processing Operations:"));
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(comboboxPanel);

    comboboxDisplay = new JLabel("Which filter do you want?");
    comboboxPanel.add(comboboxDisplay);
    String[] options = {"<None>", "horizontal-flip", "vertical-flip", "blur", "sharpen",
        "red-component", "blue-component", "green-component", "luma-component", "sepia",
        "compress", "color-correct", "levels-adjust"};
    combobox = new JComboBox<String>();
    for (int i = 0; i < options.length; i++) {
      combobox.addItem(options[i]);
    }

    comboboxPanel.add(combobox);
    mainPanel.add(comboboxPanel);

    applyFilterButton = new JButton("Apply Filter");
    applyFilterButton.setToolTipText("Click to apply the selected filter");

    mainPanel.add(applyFilterButton);

    bNumericField = new JTextField(3);
    mNumericField = new JTextField(3);
    wNumericField = new JTextField(3);
    bmwPanel = new JPanel();
    bmwPanel.add(new JLabel("Enter B, M, W:"));
    bmwPanel.add(bNumericField);
    bmwPanel.add(mNumericField);
    bmwPanel.add(wNumericField);
    testBMWButton = new JButton("Test these values!");

    bmwPanel.add(testBMWButton);
    testBMWButton.addActionListener(e -> filterOptions(false));
    comboboxPanel.add(bmwPanel);

    bmwPanel.setVisible(false);

    compressionPercentage = new JTextField(3);
    compressPanel = new JPanel();
    compressPanel.add(new JLabel("Enter compression percentage:"));
    compressPanel.add(compressionPercentage);
    testCompressButton = new JButton("Test for this percentage!");

    compressPanel.add(testCompressButton);
    testCompressButton.addActionListener(e -> filterOptions(false));
    comboboxPanel.add(compressPanel);
    compressPanel.setVisible(false);

    combobox.addItemListener(e -> filterOptions(false));

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

  private String getFileExtension(String filePath) {
    if (filePath == null) {
      return null;
    }
    int lastDotIndex = filePath.lastIndexOf(".");
    if (lastDotIndex == -1) {
      return ""; // No file extension found
    }
    return filePath.substring(lastDotIndex + 1).toLowerCase();
  }


  private void createArrowSlider() {
    arrowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    JLabel percentageLabel = new JLabel("Split Percentage: " + arrowSlider.getValue() + "%");
    percentageLabel.setBounds(10, 60, 150, 20);

    arrowSlider.addChangeListener(e -> {
      sliderValue = arrowSlider.getValue();
      percentageLabel.setText("Split Percentage: " + sliderValue + "%");
      // System.out.println("Slider value: " + sliderValue);
    });
    arrowSlider.setMajorTickSpacing(20);
    arrowSlider.setMinorTickSpacing(5);
    arrowSlider.setPaintTicks(true);
    arrowSlider.setPaintLabels(true);

    // Create a new panel to hold the slider
    sliderPanel = new JPanel(null); // Use absolute positioning
    int panelWidth = 450; // Set your desired width
    int panelHeight = 50; // Set your desired height
    sliderPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    JLabel compareLabel = new JLabel("Pull slider to compare");
    compareLabel.setBounds(350, 0, 150, panelHeight);

    // Set the bounds for the slider within the panel

    percentageLabel.setBounds(panelWidth * 2 + panelHeight * 2, 0, 150, panelHeight);
    arrowSlider.setBounds(panelWidth + panelHeight, 0, panelWidth, panelHeight);

    // Add the slider to the new panel
    sliderPanel.add(arrowSlider);
    // Add the label to the slider panel
    sliderPanel.add(percentageLabel);
    sliderPanel.add(compareLabel);
    // Add the new panel to mainPanel
    mainPanel.add(sliderPanel);
    sliderPanel.setVisible(false);
  }

  /**
   * Updates the display of an image at the specified index with the provided RGB values.
   *
   * @param rgbValues The RGB values representing the image.
   * @param index     The index at which the image should be updated.
   */
  public void updateImageForIndex(int[][][] rgbValues, int index) {
    BufferedImage image = convertRGBtoBufferedImage(rgbValues);

    int scaledWidth = (int) (image.getWidth() * 1.5);
    int scaledHeight = (int) (image.getHeight() * 1.5);

    // Create a scaled version of the image
    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

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

  private void setImg() {

    for (int i = 0; i < imageLabel.length; i++) {
      imageLabel[i] = new JLabel();
      imageScrollPane[i] = new JScrollPane(imageLabel[i]);
      imageLabel[i].setIcon(new ImageIcon("path/to/placeholder-image.png"));
      imageLabel[i].setHorizontalAlignment(JLabel.CENTER);
      imageLabel[i].setVerticalAlignment(JLabel.CENTER);
      imageScrollPane[i].setPreferredSize(new Dimension(100, 550));
      imagePanel.add(imageScrollPane[i]);
    }
    imageLabel[0].setText("Please upload image");
    imageLabel[1].setText("Please select filter");
    imageLabel[2].setText("Please upload image");
    imageLabel[0].setBorder(BorderFactory.createTitledBorder("Original Image"));
    imageLabel[1].setBorder(BorderFactory.createTitledBorder("Processed Image"));
    imageLabel[2].setBorder(BorderFactory.createTitledBorder("Current Histogram"));

  }

  private void showHelpDialog() {
    // Create a JDialog for the help popup
    JDialog helpDialog = new JDialog(this, "Help", true);
    helpDialog.setLayout(new BorderLayout());

    // Add your help text with HTML formatting
    String helpText = "<html><body style='width: 300px; text-align: center;'>" +
            "<h2>Struggling with how to process your image?</h2>" +
            "<p>Follow these steps:</p>" +
            "<ol>" +
            "<li>Load an image of your choice (PNG/JPG/JPEG/PPM) by clicking the 'Open' " +
            "button.</li>" +
            "<li>Change the filter in the dropdown to see how the filter will look on your " +
            "image.</li>" +
            "<li>Hit the 'Apply Filter' button to apply filters of your choice to the " +
            "image.</li>" +
            "</ol>" +
            "<p>(Some images have a special feature to compare with the previous image to help " +
            "you decide " +
            "if you like the filter. Be sure to hit 'Apply Filter' if you like the filter!) </p>" +
            "</body></html>";

    JEditorPane helpTextPane = new JEditorPane("text/html", helpText);
    helpTextPane.setEditable(false);

    // Add an OK button
    JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> helpDialog.dispose());  // Close the dialog when OK is clicked

    // Add components to the dialog
    helpDialog.add(new JScrollPane(helpTextPane), BorderLayout.CENTER);
    helpDialog.add(okButton, BorderLayout.SOUTH);

    // Set dialog properties
    helpDialog.setSize(400, 350);
    helpDialog.setLocationRelativeTo(this);
    helpDialog.setVisible(true);
  }


  /**
   * Adds an action listener to the specified button. The action listener should be notified when
   * the button is clicked. The action listener is responsible for calling the appropriate method
   * in the ControllerFeatures instance.
   *
   * @param features The ControllerFeatures instance containing methods for interacting with the
   *                 image processing application.
   */
  public void addFeatures(ControllerFeatures features) {

    fileOpenButtonforLoad.addActionListener(evt -> {
      boolean allowOpen = true;
      if (sourceName != null && fileSaveDisplay.getText() == null) {

        Object[] options = {"Yes", "No"};

        int result = JOptionPane.showOptionDialog(ImageEditorView.this,
                "The current image is not saved. Are you sure you want to proceed?",
                "Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
          //System.out.println("Yes button pressed");
          allowOpen = true;
        } else if (result == JOptionPane.NO_OPTION) {
          //System.out.println("No button pressed");
          allowOpen = false;

        }
      }
      if (allowOpen) {
        String openCommand = openFile();
        if (openCommand != null && !openCommand.equals("error")) {
          features.loadImage(openCommand, "img");
          //features.applyFeatures(null, "img");
          sourceName = "img";
          destName = "img";
          tempName = "img";
          fileSaveDisplay.setText(null);
          sliderPanel.setVisible(false);
          imageLabel[1].setText("Please select filter");

          imageLabel[1].setIcon(new ImageIcon("path/to/placeholder-image.png"));
          //imageLabel[1].setText("Please upload image");
          JOptionPane.showMessageDialog(ImageEditorView.this,
                  "Image loaded successfully!",
                  "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (openCommand == null || !openCommand.equals("error")) {
          JOptionPane.showMessageDialog(ImageEditorView.this,
                  "Please load an image before applying a filter.",
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      command = null;
    });

    applyFilterButton.addActionListener(evt -> {
      boolean allowApply = true;
      if (Objects.equals(fileOpenDisplay.getText(), "File path will appear here")) {
        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Please load an image before applying a filter.",
                "Error", JOptionPane.ERROR_MESSAGE);
      } else if (selectedFilter != null && !Objects.equals(selectedFilter, "<None>")) {
        if (Objects.equals(selectedFilter, "levels-adjust")) {
          String bValue = bNumericField.getText();
          String mValue = mNumericField.getText();
          String wValue = wNumericField.getText();

          allowApply = !bValue.isEmpty() && !mValue.isEmpty() && !wValue.isEmpty();
        } else if (Objects.equals(selectedFilter, "compress")) {
          String percentageTextValue = compressionPercentage.getText();

          allowApply = !percentageTextValue.isEmpty();
        }
        if (allowApply) {
          Object[] options = {"Apply", "Cancel"};
          int result = JOptionPane.showOptionDialog(ImageEditorView.this,
                  "Do you want to apply " + selectedFilter + " on the image?",
                  "Error", JOptionPane.YES_NO_OPTION,
                  JOptionPane.ERROR_MESSAGE, null, options, options[0]);

          if (result == JOptionPane.YES_OPTION) {
            //System.out.println("Apply button pressed");
            if ((Objects.equals(selectedFilter, "levels-adjust")
                    || Objects.equals(selectedFilter, "color-correct") ||
                    Objects.equals(selectedFilter, "blur")
                    || Objects.equals(selectedFilter, "sepia") ||
                    Objects.equals(selectedFilter, "sharpen")
                    || (Objects.equals(selectedFilter, "luma-component")))) {
              System.out.println("splitImageName" + splitImageName);
              sourceName = splitImageName;

            } else {
              sourceName = filteredImgName;
            }
          } else if (result == JOptionPane.NO_OPTION) {
            if (sliderPanel.isVisible()) {
              sliderValue = 0;
              arrowSlider.setValue(0);
            }

          }
          System.out.println("##source:" + sourceName);
          System.out.println("source:" + tempName);
          System.out.println("source:" + destName);
          System.out.println("source:" + splitImageName);
          System.out.println("source:" + filteredImgName);
          features.applyFeatures(null, sourceName);
        } else {
          JOptionPane.showMessageDialog(ImageEditorView.this,
                  "Please enter valid values.",
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      } else {

        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Please select a valid filter.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }

    });
    fileSaveButton.addActionListener(evt -> {

      String saveCommand = saveFile();
      if (saveCommand != null && !saveCommand.equals("error")) {

        features.saveImage(saveCommand);
        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Processed Image is saved.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
      }
      command = null;

    });


    arrowSlider.addChangeListener(e -> {

      sliderValue = arrowSlider.getValue();
      //System.out.println("Slider value: " + sliderValue);

      String filterCommand = filterOptions(true);
      System.out.println("**source:" + filterCommand);
      System.out.println("**source:" + sourceName);
      System.out.println("source:" + tempName);
      System.out.println("source:" + destName);
      System.out.println("source:" + splitImageName);
      System.out.println("source:" + filteredImgName);
      features.applyFeatures(filterCommand, tempName);

    });

    combobox.addItemListener(e -> {

      applySplitFilter = false;

      if ((e.getStateChange() == ItemEvent.SELECTED) && !(Objects.equals(fileOpenDisplay.getText(),
              "File path will appear here"))) {
        selectedFilter = (String) combobox.getSelectedItem();
        if (!Objects.equals(selectedFilter, "<None>")) {
          String filterCommand = null;
          compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
          bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
          if (!(Objects.equals(selectedFilter, "compress"))) {
            if (!(Objects.equals(selectedFilter, "levels-adjust"))) {
              if ((Objects.equals(selectedFilter, "color-correct") ||
                      Objects.equals(selectedFilter, "blur") || Objects.equals(selectedFilter,
                      "sepia") || Objects.equals(selectedFilter, "sharpen")
                      || (Objects.equals(selectedFilter, "luma-component")))) {

                JOptionPane.showMessageDialog(ImageEditorView.this,
                        "Slide Arrow to view the changes!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                sliderValue = 0;
                arrowSlider.setValue(0);
                sliderPanel.setVisible(true);
                tempName = "tempName";
                splitImageName = selectedFilter + "Split";
                filterCommand = filterOptions(true);
                System.out.println("%%" + filterCommand);
                System.out.println("%%" + tempName);
                System.out.println("%%" + splitImageName);
                System.out.println("%%" + sourceName);
                System.out.println("%%" + destName);

                if (!Objects.equals(filterCommand, "error")) {
                  features.applyFeatures(filterCommand, splitImageName);
                }
              } else {
                sliderValue = 0;
                arrowSlider.setValue(0);
                sliderPanel.setVisible(false);

                filterCommand = filterOptions(true);
                features.applyFeatures(filterCommand, filteredImgName);
              }
            } else {
              JOptionPane.showMessageDialog(ImageEditorView.this,
                      "Hit Test to view the changes with given B,M,W values!",
                      "Success", JOptionPane.INFORMATION_MESSAGE);
            }
          }
        } else {
          JOptionPane.showMessageDialog(ImageEditorView.this,
                  "Please select a valid filter.",
                  "Error", JOptionPane.ERROR_MESSAGE);
        }

      }
    });


    testBMWButton.addActionListener(e -> {

      applySplitFilter = false;
      selectedFilter = (String) combobox.getSelectedItem();
      String filterCommand = null;

      String bValue = bNumericField.getText();
      String mValue = mNumericField.getText();
      String wValue = wNumericField.getText();

      if (!bValue.isEmpty() && !mValue.isEmpty() && !wValue.isEmpty()) {
        tempName = "tempName";
        splitImageName = selectedFilter + "Split";
        filterCommand = filterOptions(true);
        if (!Objects.equals(filterCommand, "error")) {
          features.applyFeatures(filterCommand, splitImageName);
          JOptionPane.showMessageDialog(ImageEditorView.this,
                  "Slide Arrow to view the changes!",
                  "Success", JOptionPane.INFORMATION_MESSAGE);
          sliderPanel.setVisible(true);
        }
      } else {
        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Please enter a valid numeric value for B, M, W.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }


    });

    testCompressButton.addActionListener(e -> {

      applySplitFilter = false;


      selectedFilter = (String) combobox.getSelectedItem();
      String filterCommand = null;


      String percentageTextValue = compressionPercentage.getText();

      if (!percentageTextValue.isEmpty()) {
        sliderValue = 0;
        arrowSlider.setValue(0);
        sliderPanel.setVisible(false);
        filterCommand = filterOptions(true);
        features.applyFeatures(filterCommand, filteredImgName);

      } else {
        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Please enter a valid Compression Percentage.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

  }

  private String openFile() {
    command = null;
    final JFileChooser fchooser = new JFileChooser(".");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images", "jpg", "jpeg", "ppm", "png");
    fchooser.setFileFilter(filter);
    int retvalue = fchooser.showOpenDialog(ImageEditorView.this);
    if (retvalue == JFileChooser.APPROVE_OPTION) {
      File f = fchooser.getSelectedFile();
      fileOpenDisplay.setText(f.getAbsolutePath());
      fileExtension = getFileExtension(f.getAbsolutePath());

      if (!Objects.equals("png", fileExtension) && !Objects.equals("jpg", fileExtension)
              && !Objects.equals("jpeg", fileExtension) && !Objects.equals("ppm",
              fileExtension)) {
        JOptionPane.showMessageDialog(ImageEditorView.this,
                "Please select png/ jpg/ jpeg/ppm image.",
                "Error", JOptionPane.ERROR_MESSAGE);
        fileOpenDisplay.setText(null);
        imageLabel[0].setIcon(null);
        imageLabel[2].setIcon(null);
        command = "error";
      } else {
        command = "load '" + f.getAbsolutePath() + "' img";
      }
    }
    imageLabel[1].setIcon(null);
    return command;
  }

  private String filterOptions(boolean applyFilter) {
    selectedFilter = (String) combobox.getSelectedItem();
    comboboxDisplay.setText("You selected: " + selectedFilter);
    command = null;

    if (applyFilter) {
      switch (Objects.requireNonNull(selectedFilter)) {

        case "<None>":
          command = null;
          break;
        case "horizontal-flip":
          selectedFilter = "horizontal-flip";
          filteredImgName = "filteredImg-" + action;
          command = selectedFilter + " " + sourceName + " " + filteredImgName;
          break;
        case "vertical-flip":
          filteredImgName = "filteredImg-" + action;
          selectedFilter = "vertical-flip";
          command = selectedFilter + " " + sourceName + " " + filteredImgName;
          break;
        case "blur":
          selectedFilter = "blur";
          if (sliderPanel.isVisible() && sliderValue != 0) {
            tempName = sourceName + "-" + selectedFilter;
            command = selectedFilter + " " + sourceName + " " + tempName + " split " + sliderValue;

          } else {
            destName = sourceName + "-" + selectedFilter + "1";
            command = selectedFilter + " " + sourceName + " " + splitImageName;

          }
          applySplitFilter = true;
          break;
        case "sharpen":
          selectedFilter = "sharpen";
          if (sliderPanel.isVisible() && sliderValue != 0) {
            tempName = sourceName + "-" + selectedFilter;
            command = selectedFilter + " " + sourceName + " " + tempName + " split " + sliderValue;
          } else {
            destName = sourceName + "-" + selectedFilter + "1";
            command = selectedFilter + " " + sourceName + " " + splitImageName;
          }
          applySplitFilter = true;
          break;
        case "red-component":
          selectedFilter = "red-component";
          filteredImgName = "filteredImg-" + action;
          command = selectedFilter + " " + sourceName + " " + filteredImgName;
          break;
        case "blue-component":
          selectedFilter = "blue-component";
          filteredImgName = "filteredImg-" + action;
          command = selectedFilter + " " + sourceName + " " + filteredImgName;
          break;
        case "green-component":
          selectedFilter = "green-component";
          filteredImgName = "filteredImg-" + action;
          command = selectedFilter + " " + sourceName + " " + filteredImgName;
          break;
        case "luma-component":
          selectedFilter = "luma-component";
          if (sliderPanel.isVisible() && sliderValue != 0) {
            tempName = sourceName + "-" + selectedFilter;
            command = selectedFilter + " " + sourceName + " " + tempName + " split " + sliderValue;

          } else {
            destName = sourceName + "-" + selectedFilter + "1";
            command = selectedFilter + " " + sourceName + " " + splitImageName;

          }
          applySplitFilter = true;
          break;
        case "sepia":
          selectedFilter = "sepia";
          if (sliderPanel.isVisible() && sliderValue != 0) {
            tempName = sourceName + "-" + selectedFilter;
            command = selectedFilter + " " + sourceName + " " + tempName + " split " + sliderValue;
          } else {
            destName = sourceName + "-" + selectedFilter + "1";
            command = selectedFilter + " " + sourceName + " " + splitImageName;
          }
          applySplitFilter = true;
          break;
        case "compress":
          selectedFilter = "compress";
          String enteredText = compressionPercentage.getText();
          if (!enteredText.isEmpty()) {
            try {
              double numericValue = Double.parseDouble(enteredText);

              if (numericValue < 0 || numericValue > 100) {
                JOptionPane.showMessageDialog(ImageEditorView.this,
                        "Compression Percentage must be between 0 to 100.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                command = "error";
                break;
              }

            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(ImageEditorView.this,
                      "Please enter a valid numeric value for Compression Percentage.",
                      "Error", JOptionPane.ERROR_MESSAGE);
              command = "error";
              break;

            }
            command = selectedFilter + " " + enteredText + " " + sourceName + " " + filteredImgName;
          } else if (enteredText.isEmpty() && applyFilter) {
            JOptionPane.showMessageDialog(ImageEditorView.this,
                    "Please enter a value for Compression Percentage.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            command = "error";
          }
          break;
        case "color-correct": //TODO
          selectedFilter = "color-correct";
          if (sliderPanel.isVisible() && sliderValue != 0) {
            tempName = sourceName + "-" + selectedFilter;
            command = selectedFilter + " " + sourceName + " " + tempName + " split " + sliderValue;

          } else {
            destName = sourceName + "-" + selectedFilter + "1";
            command = selectedFilter + " " + sourceName + " " + splitImageName;

          }
          applySplitFilter = true;
          break;
        case "levels-adjust":
          selectedFilter = "levels-adjust";
          String bValue = bNumericField.getText();
          String mValue = mNumericField.getText();
          String wValue = wNumericField.getText();

          if (!bValue.isEmpty() && !mValue.isEmpty() && !wValue.isEmpty()) {
            try {
              double numericValueB = Double.parseDouble(bValue);
              double numericValueM = Double.parseDouble(mValue);
              double numericValueW = Double.parseDouble(wValue);

              if (numericValueB < 0 || numericValueB > 255 || numericValueM < 0
                      || numericValueM > 255 || numericValueW < 0 || numericValueW > 255) {
                JOptionPane.showMessageDialog(ImageEditorView.this,
                        "B, M, W must be between 0 to 255.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                command = "error";
                break;
              }

            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(ImageEditorView.this,
                      "Please enter a valid numeric value for B, M, W.",
                      "Error", JOptionPane.ERROR_MESSAGE);
              command = "error";
              break;
            }
            if (sliderPanel.isVisible() && sliderValue != 0) {
              tempName = sourceName + "-" + selectedFilter;
              command = selectedFilter + " " + bValue + " " + mValue + " " + wValue + " "
                      + sourceName + " " + tempName + " split " + sliderValue;

            } else {
              destName = sourceName + "-" + selectedFilter + "1";
              command = selectedFilter + " " + bValue + " " + mValue + " " + wValue + " "
                      + sourceName + " " + splitImageName;
            }
            applySplitFilter = true;
          } else if ((bValue.isEmpty() || mValue.isEmpty() || wValue.isEmpty()) && applyFilter) {
            JOptionPane.showMessageDialog(ImageEditorView.this,
                    "Please enter a value for B, M, W.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            command = "error";
          }
          break;
        default:
          selectedFilter = "None";
          break;
      }
      comboboxDisplay.setText("You selected: " + selectedFilter);
    }
    action++;
    return command;
  }

  private String saveFile() {
    String command = null;
    if (fileOpenDisplay.getText().equals("File path will appear here")) {
      JOptionPane.showMessageDialog(ImageEditorView.this,
              "Please load an image before attempting to save.",
              "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      final JFileChooser fchooser = new JFileChooser(".");
      int retvalue = fchooser.showSaveDialog(ImageEditorView.this);
      if (retvalue == JFileChooser.APPROVE_OPTION) {
        File f = fchooser.getSelectedFile();
        fileSaveDisplay.setText(f.getAbsolutePath());
        command = "save '" + f.getAbsolutePath() + "' " + sourceName;
      }
    }
    return command;
  }

}