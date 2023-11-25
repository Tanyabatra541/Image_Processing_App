package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import controller.Controller;

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

  int sliderValue=0;
  String selectedFilter;

  private void createArrowSlider() {
    JSlider arrowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    JLabel percentageLabel = new JLabel("Split Percentage: " + arrowSlider.getValue() + "%");
    percentageLabel.setBounds(10, 60, 150, 20); // Adjust the bounds as needed

    arrowSlider.addChangeListener(e -> {
      sliderValue = arrowSlider.getValue();
      percentageLabel.setText("Split Percentage: " + sliderValue + "%");
      System.out.println("Slider value: " + sliderValue);
      applyFilter(selectedFilter);
      // Handle slider value change
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

  private void applyFilter(String filterName) {

    System.out.println("hello " + filterName);
    String command = filterName + " img dest";



    if(Objects.equals(filterName, "compress")){
      String enteredText = compressionPercentage.getText();

      System.out.println("Entered Compression Percentage: " + enteredText);
command= filterName +" "+enteredText+ " img dest";
    }else if(Objects.equals(filterName, "levels-adjust")){
      String bValue = bNumericField.getText();
      String mValue = mNumericField.getText();
      String wValue = wNumericField.getText();

// Now you can use these values as needed
      System.out.println("B Value: " + bValue);
      System.out.println("M Value: " + mValue);
      System.out.println("W Value: " + wValue);
      command= filterName +" "+bValue+ " "+ mValue+ " "+ wValue+ " img dest";
    }


    //apply split
    if(sliderValue!=0){
      command = command.concat(" split "+sliderValue);
    }


    try {
      Controller.parseAndExecute(command);
      command = "save dest.png dest";
      Controller.parseAndExecute(command);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Image saved");
    setImg2(1, "dest.png");
    generateHistogram("dest");
//      imageLabel[1].repaint();
    System.out.println("after setImg2");
    addSlider();
   // createArrowSlider();
  }
  private void addSlider(){
    if(Objects.equals(selectedFilter, "levels-adjust") || Objects.equals(selectedFilter, "color-correct") ||
            Objects.equals(selectedFilter, "blur") || Objects.equals(selectedFilter, "sepia") ||
            Objects.equals(selectedFilter, "sharpen")){
      sliderPanel.setVisible(true);
      sliderValue=50;
    }else{
      sliderValue=0;
      sliderPanel.setVisible(false);
    }
  }

  private void generateHistogram(String imgName) {

    System.out.println("histogram " + imgName);
    String command =  "histogram " + imgName+  " hist";

    System.out.println(command);
    try {
      Controller.parseAndExecute(command);
      command = "save hist.png hist";
      Controller.parseAndExecute(command);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Image saved");
    setImg2(2, "hist.png");


    System.out.println("after setImg2");

  }

public static BufferedImage readPPM(String filePath) throws IOException {
  String command ="load "+ filePath+" destPPM";

  System.out.println(command);
  try {
    Controller.parseAndExecute(command);
    command = "save destPPM.png destPPM";
    Controller.parseAndExecute(command);
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
  System.out.println("Image saved");
  return ImageIO.read(new File("destPPM.png"));
}



  public void setImg2(int index, String imgPath) {
    if (index >= 0 && index < images.length) {
      try {
        System.out.println("Loading image: " + imgPath);
        // Load the new image using ImageIO to ensure proper loading
        //BufferedImage newImaBufferedImage newImage = readPPM(imgPath);ge = ImageIO.read(new File(imgPath));
        BufferedImage newImage;

        if (imgPath.toLowerCase().endsWith(".ppm")) {
          // If the file is a PPM, use custom method to read it
          newImage = readPPM(imgPath);
        } else {
          // Otherwise, use ImageIO to read other image formats
          try {
            newImage = ImageIO.read(new File(imgPath));
          } catch (IOException e) {
            e.printStackTrace(); // Handle the exception (e.g., log the error or show a message)
            return; // Exit the method or handle the error as needed
          }
        }


        if(index==2){
          System.out.println("hellooooo");
          // Zoom in the image by 50%
          int scaledWidth = (int) (newImage.getWidth() * 1.5);
          int scaledHeight = (int) (newImage.getHeight() * 1.5);

          // Create a scaled version of the image
          Image scaledImage = newImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

          // Set the loaded and scaled image to the JLabel at the specified index
          imageLabel[index].setIcon(new ImageIcon(scaledImage));

        }else {

          // Set the loaded image to the JLabel at the specified index
          imageLabel[index].setIcon(new ImageIcon(newImage));
        }
// Clear the text (remove the placeholder text)
        imageLabel[index].setText(null);
        // Repaint the components
        imagePanel.repaint();
        imageLabel[index].repaint();

        // Revalidate the containing panel and its hierarchy
        mainPanel.revalidate();
        mainPanel.repaint();
      } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception (e.g., log the error or show a message)
      }
    } else {
      // Handle invalid index
      System.out.println("Invalid index: " + index);
    }
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
    JButton fileOpenButtonforLoad = new JButton("Open a file");
    fileOpenButtonforLoad.setActionCommand("Open file");
    fileOpenButtonforLoad.addActionListener(this);
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
    JComboBox<String> combobox = new JComboBox<String>();
    //the event listener when an option is selected
    combobox.setActionCommand("Filter options");
    combobox.addActionListener(this);
    for (int i = 0; i < options.length; i++) {
      combobox.addItem(options[i]);
    }

    comboboxPanel.add(combobox);
    mainPanel.add(comboboxPanel);

    JButton applyFilterButton = new JButton("Apply Filter");
    applyFilterButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Handle the "Apply Filter" button click event
        selectedFilter = (String) combobox.getSelectedItem();

        // Call the applyFilter method with the selected filter option
        applyFilter(selectedFilter);
      }
    });

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
    JButton fileSaveButton = new JButton("Save a file");
    fileSaveButton.setActionCommand("Save file");
    fileSaveButton.addActionListener(this);
    filesavePanel.add(fileSaveButton);
    fileSaveDisplay = new JLabel("File path will appear here");
    filesavePanel.add(fileSaveDisplay);


  }


  @Override
  public void actionPerformed(ActionEvent arg0) {
    // TODO Auto-generated method stub
    switch (arg0.getActionCommand()) {
      case "Filter options":
        System.out.println("Filter options");
        if (arg0.getSource() instanceof JComboBox) {
          JComboBox<String> box = (JComboBox<String>) arg0.getSource();
          String selectedFilter = (String) box.getSelectedItem();
          comboboxDisplay.setText("You selected: " + selectedFilter);

          System.out.println("Selected option: " + selectedFilter);
          if (Objects.equals(selectedFilter, "compress")) {

            compressPanel.setVisible(true);
          } else {
            compressPanel.setVisible(false);
          }
          if (Objects.equals(selectedFilter, "levels-adjust")) {

            bmwPanel.setVisible(true);
          } else {
            bmwPanel.setVisible(false);
          }


          addSlider();
        }

        break;
      case "Open file": {
        final JFileChooser fchooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg", "gif");
        fchooser.setFileFilter(filter);
        int retvalue = fchooser.showOpenDialog(SwingFeaturesFrame.this);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          fileOpenDisplay.setText(f.getAbsolutePath());
          System.out.println(f.getAbsolutePath());
          try {
            String command = "load " + f.getAbsolutePath() + " img";
            System.out.println(command);
            Controller.parseAndExecute(command);
//            String imgPath = Controller.getLastSavedImagePath();
//            if(imgPath != null ){
            setImg2(0, f.getAbsolutePath());
            generateHistogram("img");
//            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
      break;
      case "Save file": {
        final JFileChooser fchooser = new JFileChooser(".");
        int retvalue = fchooser.showSaveDialog(SwingFeaturesFrame.this);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          fileSaveDisplay.setText(f.getAbsolutePath());

          try {
            String command = "save " + f.getAbsolutePath() + " dest";
            Controller.parseAndExecute(command);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          System.out.println("Image saved");
        }
      }
      break;
    }
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
