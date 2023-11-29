package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

public class SwingFeaturesFrame extends JFrame implements ActionListener {

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

  private String fileExtension;
boolean isCurrentImageFiltered= false;

  String sourceName=null;
  String destName="img";

String tempName="img";
String currentName="img";
  boolean applySplitFilter=true;
  String previousFilter=null;
  public String getFileExtension(String filePath) {
    if (filePath == null) {
      return null; // or throw an exception, depending on your requirements
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
    int panelWidth = 450; // Set your desired width
    int panelHeight = 50; // Set your desired height
    sliderPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    JLabel compareLabel = new JLabel("Pull slider to compare");
    compareLabel.setBounds(350, 0, 150, panelHeight);

    // Set the bounds for the slider within the panel

    percentageLabel.setBounds(panelWidth*2+panelHeight*2, 0, 150, panelHeight);
    arrowSlider.setBounds(panelWidth+panelHeight, 0, panelWidth, panelHeight);

    // Add the slider to the new panel
    sliderPanel.add(arrowSlider);
    // Add the label to the slider panel
    sliderPanel.add(percentageLabel);
    sliderPanel.add(compareLabel);
    // Add the new panel to mainPanel
    mainPanel.add(sliderPanel);
    sliderPanel.setVisible(false);
  }

  private void addSlider() {
    System.out.println("in slider panel");
    if(Objects.equals(selectedFilter, "levels-adjust") || Objects.equals(selectedFilter, "color-correct") ||
            Objects.equals(selectedFilter, "blur") || Objects.equals(selectedFilter, "sepia") ||
            Objects.equals(selectedFilter, "sharpen") || Objects.equals(selectedFilter, "luma-component")){
      sliderPanel.setVisible(true);

    }else{
      sliderValue=0;
      arrowSlider.setValue(0);
      sliderPanel.setVisible(false);
    }
  }

  public void updateImageForIndex(int[][][] rgbValues,int index) {
    BufferedImage image = convertRGBtoBufferedImage(rgbValues);

    int scaledWidth = (int)(image.getWidth() * 1.5);
    int scaledHeight = (int)(image.getHeight() * 1.5);

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

  public void setImg(String[] imgList) {
    images = imgList;

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

  public SwingFeaturesFrame() {
    super();
    setTitle("Image Processing");
    setSize(1500, 1200);


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
            "color-correct", "levels-adjust"};
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


  public void addFeatures(ControllerFeatures features){

    fileOpenButtonforLoad.addActionListener(evt -> {
      boolean allowOpen=true;
      if(previousFilter!=null && fileSaveDisplay.getText()==null){

        Object[] options = {"Yes", "No"};

        int result = JOptionPane.showOptionDialog(SwingFeaturesFrame.this,
                "The current image is not saved. Are you sure you want to proceed?",
                "Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
          System.out.println("Yes button pressed");
          allowOpen=true;
        } else if (result == JOptionPane.NO_OPTION) {
          System.out.println("No button pressed");
          allowOpen=false;

        }
      }
      if(allowOpen) {
        String openCommand = openFile();
        System.out.println("openCommand" + openCommand);
        if (openCommand != null && !openCommand.equals("error")) {
          features.loadImage(openCommand, "img");
          sourceName="img";
          destName="img";
          tempName="img";
          fileSaveDisplay.setText(null);
          sliderPanel.setVisible(false);
          JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                  "Image loaded successfully.",
                  "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (openCommand == null || !openCommand.equals("error")) {
          JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                  "Please load an image before applying a filter.",
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    applyFilterButton.addActionListener(evt -> {
      if(fileSaveDisplay.getText()!=null){
        fileSaveDisplay.setText(null);
      }
      if((Objects.equals(previousFilter, "levels-adjust") || Objects.equals(previousFilter, "color-correct") ||
              Objects.equals(previousFilter, "blur") || Objects.equals(previousFilter, "sepia") ||
              Objects.equals(previousFilter, "sharpen") ||  (Objects.equals(previousFilter, "luma-component")))){
        Object[] options = {"Apply", "Cancel"};

        int result = JOptionPane.showOptionDialog(SwingFeaturesFrame.this,
                "Do you want to apply "+previousFilter+" on the image?",
                "Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
          System.out.println("Apply button pressed");
          applySplitFilter=false;
          sourceName=tempName;

        } else if (result == JOptionPane.NO_OPTION) {
          System.out.println("Cancel button pressed");

          applySplitFilter=true;
          isCurrentImageFiltered=false;
        }
      }else{
        applySplitFilter=false;
      }


      String filterCommand = filterOptions(true);
      compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
      bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
       if (fileOpenDisplay.getText()!=null && fileOpenDisplay.getText().equals("File path will appear here")){
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Please load image before applying a filter.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }else if(filterCommand != null && !filterCommand.equals("error")) {



if(sliderPanel.isVisible() && sliderValue!=0  ){

System.out.println("KKKKKK"+tempName);
  System.out.println("KKKKKK"+sourceName);
  System.out.println("KKKKKK"+destName);
  features.applyFeatures(filterCommand, destName);
currentName=sourceName;
}else{
  System.out.println("&&&&&&&&&&&&"+applySplitFilter);
  if(!applySplitFilter){
    features.applyFeatures(filterCommand, destName);
  }else{
    features.applyFeatures(filterCommand, sourceName);
  }
  System.out.println("sajdhjas"+destName);
  System.out.println("HHH"+tempName);
  System.out.println("HHH"+sourceName);
  System.out.println("HHH"+currentName);

}

      }else if(filterCommand == null){
        // Display an error message if the filter command is null (no image loaded or invalid filter)
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Please select a valid filter before applying.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    fileSaveButton.addActionListener(evt -> {
      String saveCommand = saveFile();
      if (saveCommand != null && !saveCommand.equals("error")) {
        features.saveImage(saveCommand);
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Processed Image is saved.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
      }
    });


//    fileSaveButton.addActionListener(evt -> features.saveImage(saveFile()));
    arrowSlider.addChangeListener(e -> {
      System.out.println("Inside arrowSlider");
      sliderValue = arrowSlider.getValue();
      System.out.println("Slider value: " + sliderValue);

      String filterCommand = filterOptions(true);
      System.out.println("%%%%%%%%%%%%"+applySplitFilter);
      System.out.println("XBDHJBHJD+"+currentName);
        System.out.println("AAAAAAA"+tempName);
        System.out.println("AAAAAAA"+sourceName);
        System.out.println("AAAAAAA"+destName);
      if (filterCommand != null && sliderValue!=0) {
        features.applyFeatures(filterCommand, tempName);
      } else { features.applyFeatures(filterCommand, tempName);

      }

    });
  }

  public String openFile(){
    command = null;
    final JFileChooser fchooser = new JFileChooser(".");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images", "jpg","jpeg", "ppm", "png");
    fchooser.setFileFilter(filter);
    int retvalue = fchooser.showOpenDialog(SwingFeaturesFrame.this);
    if (retvalue == JFileChooser.APPROVE_OPTION) {
      File f = fchooser.getSelectedFile();
      fileOpenDisplay.setText(f.getAbsolutePath());
      System.out.println(f.getAbsolutePath());
      fileExtension = getFileExtension(f.getAbsolutePath());
      System.out.println("Selected fileExtension: " + fileExtension);

      if(!Objects.equals("png", fileExtension)&& !Objects.equals("jpg", fileExtension) && !Objects.equals("jpeg", fileExtension) && !Objects.equals("ppm", fileExtension)){
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Please select png/ jpg/ jpeg/ppm image.",
                "Error", JOptionPane.ERROR_MESSAGE);
        fileOpenDisplay.setText(null);
        imageLabel[0].setIcon(null);
        imageLabel[2].setIcon(null);
        command="error";
      }else {
        command = "load " + f.getAbsolutePath() + " img";
        System.out.println(command);

      }
    }
    imageLabel[1].setIcon(null);
    return command;
  }

  public String filterOptions(boolean applyFilter){
    sourceName=isCurrentImageFiltered? destName:sourceName;
    if( applySplitFilter==true && isCurrentImageFiltered==false) {
      sourceName=currentName;
    }

    System.out.println("|||||||||||applyFilter"+applyFilter);
    System.out.println("|||||||||||selectedFilter"+selectedFilter);
    if(previousFilter!=null && !applyFilter){
      sliderPanel.setVisible(false);

    }
      selectedFilter = (String) combobox.getSelectedItem();
      comboboxDisplay.setText("You selected: " + selectedFilter);
      compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
      bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
      String command = null;


    selectedFilter = (String) combobox.getSelectedItem();
    comboboxDisplay.setText("You selected: " + selectedFilter);
    compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
    bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
    command = null;


    if(applyFilter){
      switch (Objects.requireNonNull(selectedFilter)){

        case "<None>":
          command = null;
          break;
        case "horizontal-flip":
          selectedFilter = "horizontal-flip";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          break;
        case "vertical-flip":
          selectedFilter = "vertical-flip";
          command = selectedFilter + " "+sourceName+ " "+sourceName;
          break;
        case "blur":
          selectedFilter = "blur";

          if(sliderPanel.isVisible()  && sliderValue!=0) {
            tempName= sourceName+"-"+selectedFilter;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            destName= sourceName+"-"+selectedFilter+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }

          break;
        case "sharpen":
          selectedFilter = "sharpen";

          if(sliderPanel.isVisible()  && sliderValue!=0) {
            tempName= sourceName+"-"+selectedFilter;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            destName= sourceName+"-"+selectedFilter+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }

          System.out.println("sharpen command" + command);
          break;
        case "red-component":
          selectedFilter = "red-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          break;
        case "blue-component":
          selectedFilter = "blue-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          break;
        case "green-component":
          selectedFilter = "green-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          break;
        case "luma-component":
          selectedFilter = "luma-component";
         /* if (sliderValue != 0) {
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
          }*/

          if(sliderPanel.isVisible()  && sliderValue!=0) {
            tempName= sourceName+"-"+selectedFilter;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            destName= sourceName+"-"+selectedFilter+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }

          break;
        case "sepia":
          selectedFilter = "sepia";

          if(sliderPanel.isVisible()  && sliderValue!=0) {
            tempName= sourceName+"-"+selectedFilter;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            destName= sourceName+"-"+selectedFilter+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }

System.out.println("sepia command" + command);
          break;
        case "compress":
          selectedFilter = "compress";
          String enteredText = compressionPercentage.getText();
          if (!enteredText.isEmpty()) {
            try {
              double numericValue = Double.parseDouble(enteredText);

              if (numericValue < 0 || numericValue > 100) {
                JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                        "Compression Percentage must be between 0 to 100.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                command = "error";
                break;
              }

            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                      "Please enter a valid numeric value for Compression Percentage.",
                      "Error", JOptionPane.ERROR_MESSAGE);
              command = "error";
              break;

            }
              System.out.println("Entered Compression Percentage: " + enteredText);
              command = selectedFilter + " " + enteredText +  " "+sourceName+" "+sourceName;
            //destName=sourceName;

          } else if (enteredText.isEmpty() && applyFilter) {
            JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                    "Please enter a value for Compression Percentage.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            command = "error";

          }
          break;
        case "color-correct": //TODO
          selectedFilter = "color-correct";
       /*   if (sliderValue != 0) {
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
          }*/
          if(sliderPanel.isVisible()  && sliderValue!=0) {
            tempName= sourceName+"-"+selectedFilter;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            destName= sourceName+"-"+selectedFilter+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }
          break;
        case "levels-adjust": //TODO
          selectedFilter = "levels-adjust";
          String bValue = bNumericField.getText();
          String mValue = mNumericField.getText();
          String wValue = wNumericField.getText();

          // Now you can use these values as needed
          System.out.println("B Value: " + bValue);
          System.out.println("M Value: " + mValue);
          System.out.println("W Value: " + wValue);


          if (!bValue.isEmpty() && !mValue.isEmpty() && !wValue.isEmpty()) {
            try {
              double numericValueB = Double.parseDouble(bValue);
              double numericValueM = Double.parseDouble(mValue);
              double numericValueW = Double.parseDouble(wValue);

              if (numericValueB < 0 || numericValueB > 255 || numericValueM < 0 || numericValueM > 255 || numericValueW < 0 || numericValueW > 255) {
                JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                        "B, M, W must be between 0 to 255.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                command = "error";
                break;
              }

            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                      "Please enter a valid numeric value for B, M, W.",
                      "Error", JOptionPane.ERROR_MESSAGE);
              command = "error";
              break;

            }

            if(sliderPanel.isVisible()  && sliderValue!=0) {
              tempName= sourceName+"-"+selectedFilter;
              command= selectedFilter+" " + bValue + " " + mValue + " " + wValue +" "+currentName+" "+tempName+" split "+sliderValue;
              isCurrentImageFiltered=true;
            }else{
              destName= sourceName+"-"+selectedFilter+"1";
              command= selectedFilter+" " + bValue + " " + mValue + " " + wValue +" "+sourceName+" "+destName;
              isCurrentImageFiltered=true;
            }


          } else if ((bValue.isEmpty() || mValue.isEmpty() || wValue.isEmpty()) && applyFilter) {
            JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
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
        addSlider();
    }
    if(applyFilter){
      previousFilter=selectedFilter;
    }

    return command;
  }

  public String saveFile() {
    String command = null;

    if(sliderPanel.isVisible()) {
      if ((Objects.equals(previousFilter, "levels-adjust") || Objects.equals(previousFilter, "color-correct") ||
              Objects.equals(previousFilter, "blur") || Objects.equals(previousFilter, "sepia") ||
              Objects.equals(previousFilter, "sharpen") ||  (Objects.equals(previousFilter, "luma-component")) )){
        Object[] options = {"Apply", "Cancel"};

        int result = JOptionPane.showOptionDialog(SwingFeaturesFrame.this,
                "Do you want to apply " + previousFilter + " on the image?",
                "Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
          System.out.println("Apply button pressed");
          applySplitFilter = false;
          sourceName = tempName;

        } else if (result == JOptionPane.NO_OPTION) {
          System.out.println("Cancel button pressed");
          applySplitFilter = true;
          isCurrentImageFiltered = false;
        }
      } else {
        applySplitFilter = false;
      }
      command = filterOptions(true);
    }

      if (fileOpenDisplay.getText().equals("File path will appear here")) {
      JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
              "Please load an image before attempting to save.",
              "Error", JOptionPane.ERROR_MESSAGE);
    }else {
      final JFileChooser fchooser = new JFileChooser(".");
      int retvalue = fchooser.showSaveDialog(SwingFeaturesFrame.this);
      if (retvalue == JFileChooser.APPROVE_OPTION) {
        File f = fchooser.getSelectedFile();
        fileSaveDisplay.setText(f.getAbsolutePath());
          command = "save " + f.getAbsolutePath() + " "+sourceName;
          System.out.println("Image saved");
      }
    }
    return command;
  }


  @Override
  public void actionPerformed(ActionEvent e) {

  }

}
