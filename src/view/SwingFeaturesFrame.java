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

public class SwingFeaturesFrame extends JFrame implements ActionListener, ItemListener, ListSelectionListener {

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

/*    arrowSlider.addChangeListener(e -> {
      sliderValue = arrowSlider.getValue();
      percentageLabel.setText("Split Percentage: " + sliderValue + "%");
      System.out.println("Slider value: " + sliderValue);
    });*/
    arrowSlider.setMajorTickSpacing(20);
    arrowSlider.setMinorTickSpacing(5);
    arrowSlider.setPaintTicks(true);
    arrowSlider.setPaintLabels(true);

    // Assuming imageLabel[1] is set at this point
    JLabel imageLabel = this.imageLabel[1];

    // Create a new panel to hold the slider
    sliderPanel = new JPanel(null); // Use absolute positioning
    int panelWidth = 400; // Set your desired width
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


      //sliderValue=0;
//arrowSlider.setValue(0);
   //   sliderValue=0;

     // sliderValue=0;
    }else{
      sliderValue=0;
      sliderPanel.setVisible(false);
    }
  }

  public void updateImageForIndex(int[][][] rgbValues,int index) {
    BufferedImage image = convertRGBtoBufferedImage(rgbValues);

//    double scalingFactor = ( 370 / (double) Math.max(image.getWidth(), image.getHeight()));

    int scaledWidth = (int)(image.getWidth() * 1.5);
    int scaledHeight = (int)(image.getHeight() * 1.5);

    // Create a scaled version of the image
    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);




    // Zoom in the image by 50%
//    int scaledWidth = (int) (image.getWidth() * 0.6);
//    int scaledHeight = (int) (image.getHeight() * 0.6);

    // Create a scaled version of the image
//    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

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
      imageScrollPane[i].setPreferredSize(new Dimension(100, 550));
      imagePanel.add(imageScrollPane[i]);
    }
    imageLabel[0].setText("Please upload image");
    imageLabel[1].setText("Please select filter");
    imageLabel[2].setText("Please upload image");
    imageLabel[0].setBorder(BorderFactory.createTitledBorder("Original Image"));
    imageLabel[1].setBorder(BorderFactory.createTitledBorder("Processed Image"));
    imageLabel[2].setBorder(BorderFactory.createTitledBorder("Current Histogram"));
//    imageScrollPane[1].remove(imageLabel[1]);
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
//    fileOpenButtonforLoad.addActionListener(evt -> features.loadImage(openFile(), "img"));
    fileOpenButtonforLoad.addActionListener(evt -> {
      String openCommand = openFile();
      System.out.println("openCommand"+openCommand);
      if (openCommand != null && !openCommand.equals("error")) {
        features.loadImage(openCommand, "img");
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Image loaded successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
      } else if (openCommand == null || !openCommand.equals("error"))  {
        // Display an error message if the open command is null (no image loaded)
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Please load an image before applying a filter.",
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    applyFilterButton.addActionListener(evt -> {
      System.out.println("Inside apply filter"+selectedFilter);
      System.out.println("Inside apply filter"+(String) combobox.getSelectedItem());
      System.out.println("Inside apply filter"+previousFilter);
      if((Objects.equals(previousFilter, "levels-adjust") || Objects.equals(previousFilter, "color-correct") ||
              Objects.equals(previousFilter, "blur") || Objects.equals(previousFilter, "sepia") ||
              Objects.equals(previousFilter, "sharpen"))){
        Object[] options = {"Apply", "Cancel"};

        int result = JOptionPane.showOptionDialog(SwingFeaturesFrame.this,
                "Do you want to apply "+selectedFilter+" on the image?",
                "Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
          System.out.println("Apply button pressed");
          applySplitFilter=false;
          sourceName=tempName;
          // Add your code for the "Apply" action here
        } else if (result == JOptionPane.NO_OPTION) {
          System.out.println("Cancel button pressed");
          System.out.println("YYYYYY"+destName);
          System.out.println("YYYYY"+tempName);
          System.out.println("YYYYY"+sourceName);
          System.out.println("YYYYY"+currentName);
          //sourceName=currentName;
          // Add your code for the "Cancel" action here
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

           System.out.println("EEEEEEEEE"+sliderPanel.isVisible());

           System.out.println("slider value" + sliderValue);
/*           if(sliderValue==0) {

             features.applyFeatures(filterCommand, "img");
           }*//*else{
              features.applyFeatures(filterCommand, "img");
           }*/
         System.out.println("&&&&&&&&&&&&"+applySplitFilter);
if(sliderPanel.isVisible() && sliderValue!=0 && !applySplitFilter ){

System.out.println("KKKKKK"+tempName);
  System.out.println("KKKKKK"+sourceName);
  System.out.println("KKKKKK"+destName);
  features.applyFeatures(filterCommand, destName);
currentName=sourceName;
}else{
  System.out.println("sajdhjas"+destName);
  System.out.println("HHH"+tempName);
  System.out.println("HHH"+sourceName);
  System.out.println("HHH"+currentName);

  features.applyFeatures(filterCommand, currentName);

  //currentName=destName;
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
     // isCurrentImageFiltered= false;
      String filterCommand = filterOptions(true);
      System.out.println("%%%%%%%%%%%%"+applySplitFilter);
      System.out.println("XBDHJBHJD+"+currentName);
        System.out.println("AAAAAAA"+tempName);
        System.out.println("AAAAAAA"+sourceName);
        System.out.println("AAAAAAA"+destName);
      if (filterCommand != null && sliderValue!=0) {
        features.applyFeatures(filterCommand, tempName);
      } else { features.applyFeatures(filterCommand, tempName);
        // Display an error message if the filter command is null (no image loaded or invalid filter)
       /* JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                "Please move the slider",
                "Error", JOptionPane.ERROR_MESSAGE);*/
      }

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
       // sliderValue = 0;
       // arrowSlider.setValue(0);
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

   // applySplitFilter=false;
    //destName=isCurrentImageFiltered? destName:"img";

 /*   destName="img";
    if(applyFilter==true){
      destName="splitImg";
    }
*/
    System.out.println("|||||||||||applyFilter"+applyFilter);
    System.out.println("|||||||||||selectedFilter"+selectedFilter);
    /*if((Objects.equals(selectedFilter, "levels-adjust") || Objects.equals(selectedFilter, "color-correct") ||
            Objects.equals(selectedFilter, "blur") || Objects.equals(selectedFilter, "sepia") ||
            Objects.equals(selectedFilter, "sharpen")) && !selectedFilter.equals((String) combobox.getSelectedItem())){
      Object[] options = {"Apply", "Cancel"};

      int result = JOptionPane.showOptionDialog(SwingFeaturesFrame.this,
              "Do you want to apply "+selectedFilter+" on the image?",
              "Error", JOptionPane.YES_NO_OPTION,
              JOptionPane.ERROR_MESSAGE, null, options, options[0]);

      if (result == JOptionPane.YES_OPTION) {
        System.out.println("Apply button pressed");
        // Add your code for the "Apply" action here
      } else if (result == JOptionPane.NO_OPTION) {
        System.out.println("Cancel button pressed");
        // Add your code for the "Cancel" action here
      }
    }else{
      applySplitFilter=true;
    }*/
    //String previousFilter = selectedFilter;
  /*  if(previousFilter!=null && applyFilter){
      System.out.println("######33ßdsjhg");
      JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
              "The current image is not saved. Are you sure you want to proceed?",
              "Error", JOptionPane.ERROR_MESSAGE);
      command="error";

    }*/
    //else {
 /*   if(split==true){

    }*/

    if(previousFilter!=null && !applyFilter){
      sliderPanel.setVisible(false);

    }
    System.out.println("dest" + destName);
    System.out.println("source" + sourceName);
    System.out.println("currentName" + currentName);
      System.out.println("previousFilter" + previousFilter);
      selectedFilter = (String) combobox.getSelectedItem();
      comboboxDisplay.setText("You selected: " + selectedFilter);
      System.out.println("Selected option: " + selectedFilter);
      compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
      bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
      String command = null;

    System.out.println("boolean value" + applyFilter);
    selectedFilter = (String) combobox.getSelectedItem();
    comboboxDisplay.setText("You selected: " + selectedFilter);
//    System.out.println("Selected option: " + selectedFilter);
    compressPanel.setVisible(Objects.equals(selectedFilter, "compress"));
    bmwPanel.setVisible(Objects.equals(selectedFilter, "levels-adjust"));
    command = null;
    System.out.println("Filter options");
    //currentName=sourceName;
    if(applyFilter){
      switch (Objects.requireNonNull(selectedFilter)){

        case "<None>":
          command = null;
          break;
        case "horizontal-flip":
          selectedFilter = "horizontal-flip";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          //isCurrentImageFiltered=false;
          //destName="img";
          //destName=sourceName;
          break;
        case "vertical-flip":
          selectedFilter = "vertical-flip";
          command = selectedFilter + " "+sourceName+ " "+sourceName;//" img img";
          //destName="img";
          //destName=sourceName;
          break;
        case "blur":
          selectedFilter = "blur";
         /* if (sliderValue != 0) {
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
          }*/
    /*      if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" img"+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;

            isCurrentImageFiltered=true;
          }*/
          if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }

/*
          if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" img"+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=false;
          }*/
          break;
        case "sharpen":
          selectedFilter = "sharpen";
        /*  if (sliderValue != 0) {
            System.out.println("in the if condition");
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
          }*/
          if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=true;
          }


       /*   if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" "+sourceName+" "+tempName+" split "+sliderValue;
            isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
            isCurrentImageFiltered=false;
          }*/
          System.out.println("sharpen command" + command);
          break;
        case "red-component":
          selectedFilter = "red-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          destName="img";
          break;
        case "blue-component":
          selectedFilter = "blue-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          destName="img";
          break;
        case "green-component":
          selectedFilter = "green-component";
          command = selectedFilter + " "+sourceName+" "+sourceName;
          destName="img";
          break;
        case "luma-component": //TODO
          selectedFilter = "luma-component";
          if (sliderValue != 0) {
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
          }
          break;
        case "sepia":
          selectedFilter = "sepia";
          if(sliderPanel.isVisible()  && sliderValue!=0) {
            String first= selectedFilter.split(" ")[0].toLowerCase();
            tempName= sourceName+"-"+first;
            command= selectedFilter+" "+currentName+" "+tempName+" split "+sliderValue;
isCurrentImageFiltered=true;
          }else{
            String first= selectedFilter.split(" ")[0].toLowerCase();
            destName= sourceName+"-"+first+"1";
            command= selectedFilter+" "+sourceName+" "+destName;
isCurrentImageFiltered=true;
          }

/*
          if (sliderValue != 0) {
            System.out.println("in the if condition");
            command = selectedFilter + " splitImg splitImg split "+sliderValue;
//            command = selectedFilter + " img dest split " + sliderValue;
            isCurrentImageFiltered=false;
          } else {
            command = selectedFilter + " img img";
            isCurrentImageFiltered=true;
          }*/
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
              // Handle the exception as needed (e.g., show an error message)
            }
              System.out.println("Entered Compression Percentage: " + enteredText);
              command = selectedFilter + " " + enteredText +  " "+sourceName+" "+sourceName;
            destName=sourceName;
             /* if (sliderValue != 0) {
                command = command.concat(" split " + sliderValue);
              }
            System.out.println("Entered Compression Percentage: " + enteredText);
            command = selectedFilter + " " + enteredText + " img dest";
            if (sliderValue != 0) {
              command = command.concat(" split " + sliderValue);
            }
*/
          } else if (enteredText.isEmpty() && applyFilter) {
            JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                    "Please enter a value for Compression Percentage.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            command = "error";
            // Handle the case where the entered text is empty (e.g., show an error message)
          }
          break;
        case "color-correct": //TODO
          selectedFilter = "color-correct";
          if (sliderValue != 0) {
            command = selectedFilter + " img dest split " + sliderValue;
          } else {
            command = selectedFilter + " img img";
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
              // Handle the exception as needed (e.g., show an error message)
            }

           // command = selectedFilter + " " + bValue + " " + mValue + " " + wValue + " img img";

            if(sliderPanel.isVisible()  && sliderValue!=0) {
              String first= selectedFilter.split(" ")[0].toLowerCase();
              tempName= sourceName+"-"+first;
              command= selectedFilter+" " + bValue + " " + mValue + " " + wValue +" img"+" "+tempName+" split "+sliderValue;
              isCurrentImageFiltered=true;
            }else{
              String first= selectedFilter.split(" ")[0].toLowerCase();
              destName= sourceName+"-"+first+"1";
              command= selectedFilter+" " + bValue + " " + mValue + " " + wValue +" "+sourceName+" "+destName;
              isCurrentImageFiltered=true;
            }

          /*  if (sliderValue != 0) {
              command = command.concat(" split " + sliderValue);
            }*/
          } else if ((bValue.isEmpty() || mValue.isEmpty() || wValue.isEmpty()) && applyFilter) {
            JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                    "Please enter a value for B, M, W.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            command = "error";
            // Handle the case where the entered text is empty (e.g., show an error message)
          }


          break;
        default:
          selectedFilter = "None";
          break;
      }
      comboboxDisplay.setText("You selected: " + selectedFilter);

      System.out.println("Selected option: " + selectedFilter);

//sliderValue=0;
//arrowSlider.setValue(0);
        addSlider();



    }
    if(applyFilter){
      previousFilter=selectedFilter;
    }

    return command;
  }

  public String saveFile() {
    String command = null;
    if (fileOpenDisplay.getText().equals("File path will appear here")) {
      JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
              "Please load an image before attempting to save.",
              "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      final JFileChooser fchooser = new JFileChooser(".");
      int retvalue = fchooser.showSaveDialog(SwingFeaturesFrame.this);
      if (retvalue == JFileChooser.APPROVE_OPTION) {
        File f = fchooser.getSelectedFile();
        fileSaveDisplay.setText(f.getAbsolutePath());
        String ext = getFileExtension(f.getAbsolutePath());
        if(!Objects.equals(ext, fileExtension)){
          JOptionPane.showMessageDialog(SwingFeaturesFrame.this,
                  "Please save in same file format ("+fileExtension+").",
                  "Error", JOptionPane.ERROR_MESSAGE);
          fileSaveDisplay.setText(null);
command="error";
        }else {
          command = "save " + f.getAbsolutePath() + " img";
          System.out.println("Image saved");
        }
      }
    }


    return command;
  }


  @Override
  public void itemStateChanged(ItemEvent arg0) {
    // TODO Auto-generated method stub
//    String who = ((JCheckBox) arg0.getItemSelectable()).getActionCommand();
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

  @Override
  public void actionPerformed(ActionEvent e) {

  }

}
