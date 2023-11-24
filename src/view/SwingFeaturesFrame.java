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
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
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
  private JPasswordField pfield;
  private JButton pButton;
  private JLabel pDisplay;
  private JPanel mainPanel;
  private JPanel bmwPanel = new JPanel();
  private JPanel compressPanel = new JPanel();
  private JPanel splitPanel = new JPanel();
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

  private void applyFilter(String filterName) {
    // Implement the logic for applying the filter here
    // You can update the image or perform any other processing
    // For example, to set the third image to "Apple.png"
    System.out.println("hello " + filterName);
    String command = filterName + " img dest";

    System.out.println(command);
    try {
      Controller.parseAndExecute(command);
      command = "save dest.png dest";
      Controller.parseAndExecute(command);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Image saved");
    setImg2(1, "dest.png");

//      imageLabel[1].repaint();
    System.out.println("after setImg2");

  }

//  public void setImg2(int index, String imgPath) {
//    if (index >= 0 && index < images.length) {
//      try {
//        System.out.println("Loading image: " + imgPath);
//        // Load the new image using ImageIO to ensure proper loading
//        BufferedImage newImage = ImageIO.read(new File(imgPath));
//
//        System.out.println("hellooooo");
//
//        // Set the loaded image to the JLabel at the specified index
//        imageLabel[index].setIcon(new ImageIcon(newImage));
//
//        // Repaint the components
//        imagePanel.repaint();
//        imageLabel[index].repaint();
//
//        // Revalidate the containing panel and its hierarchy
//        mainPanel.revalidate();
//        mainPanel.repaint();
//      } catch (IOException e) {
//        e.printStackTrace();
//        // Handle the exception (e.g., log the error or show a message)
//      }
//    } else {
//      // Handle invalid index
//      System.out.println("Invalid index: " + index);
//    }
//  }

  public void setImg2(int index, String imgPath) {
    if (index >= 0 && index < images.length) {
      try {
        System.out.println("Loading image: " + imgPath);

        BufferedImage newImage;

        // Check the file extension to determine the image type
        String extension = imgPath.substring(imgPath.lastIndexOf('.') + 1).toLowerCase();

        if (extension.equals("ppm")) {
          newImage = loadPpmImage(imgPath);
        } else {
          // Load the new image using ImageIO for JPG and PNG
          newImage = ImageIO.read(new File(imgPath));
        }
        System.out.println("loadppm function finished");
        // Set the loaded image to the JLabel at the specified index
        imageLabel[index].setIcon(new ImageIcon(newImage));

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

  private BufferedImage loadPpmImage(String imgPath) throws IOException {
    int[][][] imageRGBData = readImageRGBData(imgPath);
    String ppmContent = convertToPPMFormat(imageRGBData);

    try (InputStream inputStream = new ByteArrayInputStream(ppmContent.getBytes())) {
      return ImageIO.read(inputStream);
    }
  }

  public static int[][][] readImageRGBData(String filename) throws IOException {
    Scanner sc = null;

    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      throw new IOException("File " + filename + " not found!", e);
    }

    StringBuilder builder = new StringBuilder();
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    sc = new Scanner(builder.toString());

    String token = sc.next();
    if (!token.equals("P3")) {
      throw new IOException("Invalid PPM file: plain RAW file should begin with P3");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();

    int[][][] imageRGBData = new int[height][width][3];
    int maxValue = sc.nextInt(); // Read the maximum color value

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        imageRGBData[i][j][0] = sc.nextInt(); // Red component
        imageRGBData[i][j][1] = sc.nextInt(); // Green component
        imageRGBData[i][j][2] = sc.nextInt(); // Blue component
      }
    }
    return imageRGBData;
  }

  private String convertToPPMFormat(int[][][] imageRGBData) {
    StringBuilder ppmContent = new StringBuilder();
    int height = imageRGBData.length;
    int width = imageRGBData[0].length;

    ppmContent.append("P3\n");
    ppmContent.append(width).append(" ").append(height).append("\n");
    ppmContent.append("255\n");

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        ppmContent.append(imageRGBData[i][j][0]).append(" "); // Red component
        ppmContent.append(imageRGBData[i][j][1]).append(" "); // Green component
        ppmContent.append(imageRGBData[i][j][2]).append(" "); // Blue component
      }
      ppmContent.append("\n");
    }

    return ppmContent.toString();
  }



  // Example usage in your code
// Assuming your constructor has already called setImg(images) to initialize the images


  public void setImg(String[] imgList) {
    images = imgList;

    for (int i = 0; i < imageLabel.length; i++) {
      imageLabel[i] = new JLabel();
      imageScrollPane[i] = new JScrollPane(imageLabel[i]);
      imageLabel[i].setIcon(new ImageIcon(images[i]));
      imageScrollPane[i].setPreferredSize(new Dimension(100, 600));
      imagePanel.add(imageScrollPane[i]);

    }
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

    //dialog boxes

    comboboxPanel = new JPanel();
    comboboxPanel.setBorder(BorderFactory.createTitledBorder("Processing Operations:"));
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(comboboxPanel);

    comboboxDisplay = new JLabel("Which filter do you want?");
    comboboxPanel.add(comboboxDisplay);
    String[] options = {"<None>", "horizontal-flip", "vertical-flip", "Blur", "Sharpen", "RGB-split", "Grayscale- Luma", "sepia", "Compress",
            "Color Correct", "Level Adjust", "Split"};
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
        String selectedFilter = (String) combobox.getSelectedItem();

        // Call the applyFilter method with the selected filter option
        applyFilter(selectedFilter);
      }
    });

    mainPanel.add(applyFilterButton);

    JTextField bNumericField = new JTextField(3);
    JTextField mNumericField = new JTextField(3);
    JTextField wNumericField = new JTextField(3);

    bmwPanel.add(new JLabel("Enter B, M, W:"));
    bmwPanel.add(bNumericField);
    bmwPanel.add(mNumericField);
    bmwPanel.add(wNumericField);
    comboboxPanel.add(bmwPanel);
    bmwPanel.setVisible(false);

    JTextField compressionPercentage = new JTextField(3);

    compressPanel.add(new JLabel("Enter compression percentage:"));
    compressPanel.add(compressionPercentage);

    comboboxPanel.add(compressPanel);
    compressPanel.setVisible(false);

    JTextField splitPercentage = new JTextField(3);

    splitPanel.add(new JLabel("Enter compression percentage:"));
    splitPanel.add(splitPercentage);

    comboboxPanel.add(splitPanel);
    splitPanel.setVisible(false);


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
          if (Objects.equals(selectedFilter, "RGB-split")) {

            bmwPanel.setVisible(true);
          } else {
            bmwPanel.setVisible(false);
          }
          if (Objects.equals(selectedFilter, "Compress")) {

            compressPanel.setVisible(true);
          } else {
            compressPanel.setVisible(false);
          }
          if (Objects.equals(selectedFilter, "Split")) {
//            setImg2(2, "Koala.jpg");
          } else {
            splitPanel.setVisible(false);
          }
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
