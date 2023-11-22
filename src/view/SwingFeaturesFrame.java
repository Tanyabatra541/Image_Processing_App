package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
  private JPanel bmwPanel= new JPanel();
  private JPanel compressPanel= new JPanel();
  private JPanel splitPanel= new JPanel();
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
  private String[] images= new String[]{};
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
  public void setImg2(int index, String imgPath) {
/*    ImageIcon newIcon = new ImageIcon(imgPath);
    if (newIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
      imageLabel[index].setIcon(newIcon);

    } else {
      System.out.println("Failed to load the image: " + imgPath);
    }*/
    if (index >= 0 && index < images.length) {
      images[index] = imgPath;
      imageLabel[index].setIcon(new ImageIcon(imgPath));
      imagePanel.repaint();
      imageLabel[index].repaint();

      // Revalidate the containing panel and its hierarchy
      mainPanel.revalidate();
      mainPanel.repaint();
    } else {
      // Handle invalid index
      System.out.println("Invalid index: " + index);
    }
  }

  // Example usage in your code
// Assuming your constructor has already called setImg(images) to initialize the images



  public void setImg(String[] imgList){
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

    //text area
    //JTextArea textArea = new JTextArea(10, 20);
    //textArea.setBorder(BorderFactory.createTitledBorder("Regular text area"));
    //mainPanel.add(textArea);

    //text area with a scrollbar
    //JTextArea sTextArea = new JTextArea(10, 20);
    //JScrollPane scrollPane = new JScrollPane(sTextArea);
    //sTextArea.setLineWrap(true);
    ////scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    //scrollPane.setBorder(BorderFactory.createTitledBorder("Scrollable text area"));
    //mainPanel.add(scrollPane);

    //password fields

    /*JPanel pPanel = new JPanel();
    pPanel.setBorder(BorderFactory.createTitledBorder("Using Password fields"));
    mainPanel.add(pPanel);

    pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.PAGE_AXIS));
    pfield = new JPasswordField(10);
    pPanel.add(pfield);
    pButton = new JButton("Echo password");
    pButton.addActionListener(this);
    pButton.setActionCommand("password button");
    pPanel.add(pButton);
    pDisplay = new JLabel("Password will appear here");
    pDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    pPanel.add(pDisplay);*/

    //checkboxes

    /*JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setBorder(BorderFactory.createTitledBorder("Checkboxes"));

    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.PAGE_AXIS));

    JCheckBox[] checkBoxes = new JCheckBox[5];
    ButtonGroup group = new ButtonGroup();
    for (int i = 0; i < checkBoxes.length; i++) {
      checkBoxes[i] = new JCheckBox("Option " + (i + 1));
      checkBoxes[i].setSelected(false);
      checkBoxes[i].setActionCommand("CB" + (i + 1));
      checkBoxes[i].addItemListener(this);
      //	group.add(checkBoxes[i]);
      checkBoxPanel.add(checkBoxes[i]);
    }
    checkboxDisplay = new JLabel("Which one did the user touch?");
    checkBoxPanel.add(checkboxDisplay);
    mainPanel.add(checkBoxPanel);*/


    //radio buttons
    /*JPanel radioPanel = new JPanel();
    radioPanel.setBorder(BorderFactory.createTitledBorder("Radio buttons"));

    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));

    JRadioButton[] radioButtons = new JRadioButton[5];

    //buttons groups are used to combine radio buttons. Only one radio
    // button in each group can be selected.
    ButtonGroup rGroup1 = new ButtonGroup();
    ButtonGroup rGroup2 = new ButtonGroup();

    for (int i = 0; i < radioButtons.length; i++) {
      radioButtons[i] = new JRadioButton("Option " + (i + 1));
      //radioButtons[i].setSelected(false);

      radioButtons[i].setActionCommand("RB" + (i + 1));
      radioButtons[i].addActionListener(this);
      if (i < 2)
        rGroup1.add(radioButtons[i]);
      else
        rGroup2.add(radioButtons[i]);
      radioPanel.add(radioButtons[i]);

    }
    radioButtons[4].doClick();
    radioDisplay = new JLabel("Which one did the user select?");
    radioPanel.add(radioDisplay);
    mainPanel.add(radioPanel);
*/
    //combo boxes

    /*JPanel comboboxPanel = new JPanel();
    comboboxPanel.setBorder(BorderFactory.createTitledBorder("Combo boxes"));
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(comboboxPanel);

    comboboxDisplay = new JLabel("Cold Stone Creamery: Which size do you "
            + "want?");
    comboboxPanel.add(comboboxDisplay);
    String[] options = {"Like it", "Love it", "Gotta have it"};
    JComboBox<String> combobox = new JComboBox<String>();
    //the event listener when an option is selected
    combobox.setActionCommand("Size options");
    combobox.addActionListener(this);
    for (int i = 0; i < options.length; i++) {
      combobox.addItem(options[i]);
    }

    comboboxPanel.add(combobox);
*/
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

    //Selection lists
  /*  JPanel selectionListPanel = new JPanel();
    selectionListPanel.setBorder(BorderFactory.createTitledBorder("Selection lists"));
    selectionListPanel.setLayout(new BoxLayout(selectionListPanel, BoxLayout.X_AXIS));
    mainPanel.add(selectionListPanel);

    DefaultListModel<String> dataForListOfStrings = new DefaultListModel<>();
    dataForListOfStrings.addElement("Apple");
    dataForListOfStrings.addElement("Bear");
    dataForListOfStrings.addElement("Cave");
    dataForListOfStrings.addElement("Decorate");
    dataForListOfStrings.addElement("Exciting");
    dataForListOfStrings.addElement("Flicker");
    listOfStrings = new JList<>(dataForListOfStrings);
    listOfStrings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listOfStrings.addListSelectionListener(this);
    selectionListPanel.add(listOfStrings);


    DefaultListModel<Integer> dataForListOfIntegers = new DefaultListModel<>();
    for (int i = 0; i < 1000; i++)
      dataForListOfIntegers.addElement(i);
    listOfIntegers = new JList<>(dataForListOfIntegers);
    listOfIntegers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listOfIntegers.addListSelectionListener(this);
    selectionListPanel.add(new JScrollPane(listOfIntegers));*/






    //color chooser
   /* JPanel colorChooserPanel = new JPanel();
    colorChooserPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(colorChooserPanel);
    JButton colorChooserButton = new JButton("Choose a color");
    colorChooserButton.setActionCommand("Color chooser");
    colorChooserButton.addActionListener(this);
    colorChooserPanel.add(colorChooserButton);
    colorChooserDisplay = new JLabel("      ");
    colorChooserDisplay.setOpaque(true); //so that background color shows up
    colorChooserDisplay.setBackground(Color.WHITE);
    colorChooserPanel.add(colorChooserDisplay);
*/
    //file open
    /*JPanel fileopenPanel = new JPanel();
    fileopenPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(fileopenPanel);
    JButton fileOpenButton = new JButton("Open a file");
    fileOpenButton.setActionCommand("Open file");
    fileOpenButton.addActionListener(this);
    fileopenPanel.add(fileOpenButton);
    fileOpenDisplay = new JLabel("File path will appear here");
    fileopenPanel.add(fileOpenDisplay);
*/


    //JOptionsPane message dialog
    /*JPanel messageDialogPanel = new JPanel();
    messageDialogPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(messageDialogPanel);

    JButton messageButton = new JButton("Click for a message");
    messageButton.setActionCommand("Message");
    messageButton.addActionListener(this);
    messageDialogPanel.add(messageButton);

    //JOptionsPane input dialog
    JPanel inputDialogPanel = new JPanel();
    inputDialogPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(inputDialogPanel);

    JButton inputButton = new JButton("Click to enter username");
    inputButton.setActionCommand("Input");
    inputButton.addActionListener(this);
    inputDialogPanel.add(inputButton);

    inputDisplay = new JLabel("Default");
    inputDialogPanel.add(inputDisplay);

    //JOptionsPane options dialog
    JPanel optionsDialogPanel = new JPanel();
    optionsDialogPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(optionsDialogPanel);

    JButton optionButton = new JButton("Click to enter options");
    optionButton.setActionCommand("Option");
    optionButton.addActionListener(this);
    optionsDialogPanel.add(optionButton);

    optionDisplay = new JLabel("Default");
    optionsDialogPanel.add(optionDisplay);
*/

    //dialog boxes

    comboboxPanel = new JPanel();
    comboboxPanel.setBorder(BorderFactory.createTitledBorder("Processing Operations:"));
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.PAGE_AXIS));
    mainPanel.add(comboboxPanel);

    comboboxDisplay = new JLabel("Which filter do you want?");
    comboboxPanel.add(comboboxDisplay);
    String[] options = {"<None>", "horizontal-flip", "vertical-Flip","Blur","Sharpen","RGB-split","Grayscale- Luma","sepia","Compress",
            "Color Correct","Level Adjust","Split"};
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

    /*JFormattedTextField numericField1;
    JFormattedTextField numericField2;
    JFormattedTextField numericField3;

    try {
      MaskFormatter formatter = new MaskFormatter("###"); // Accepts only numeric values with 3 digit
      numericField1 = new JFormattedTextField(formatter);
      numericField2 = new JFormattedTextField(formatter);
      numericField3 = new JFormattedTextField(formatter);
    } catch (java.text.ParseException ex) {
      numericField1 = new JFormattedTextField();
      numericField2 = new JFormattedTextField();
      numericField3 = new JFormattedTextField();
    }

    numericField1.setColumns(3); // Set the desired width
    numericField2.setColumns(3);
    numericField3.setColumns(3);
 // Add components to the panel

    //mainPanel.add(comboBox);
    bmwPanel.add(new JLabel("Enter a B, M, W::"));
    bmwPanel.add(numericField1);
    bmwPanel.add(numericField2);
    bmwPanel.add(numericField3);
    mainPanel.add(bmwPanel);*/

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
      /*case "password button":
        pDisplay.setText(new String(pfield.getPassword()));
        pfield.setText("");
        break;
      case "RB1":
        radioDisplay.setText("Radio button 1 was selected");
        break;

      case "RB2":
        radioDisplay.setText("Radio button 2 was selected");
        break;

      case "RB3":
        radioDisplay.setText("Radio button 3 was selected");
        break;

      case "RB4":
        radioDisplay.setText("Radio button 4 was selected");
        break;

      case "Size options":
        if (arg0.getSource() instanceof JComboBox) {
          JComboBox<String> box = (JComboBox<String>) arg0.getSource();
          comboboxDisplay.setText("You selected: " + (String) box.getSelectedItem());


        }
        break;
      case "Color chooser":
        Color col = JColorChooser.showDialog(SwingFeaturesFrame.this, "Choose a color", colorChooserDisplay.getBackground());
        colorChooserDisplay.setBackground(col);
        break;*/
      case "Filter options":
        System.out.println("Filter options");
        if (arg0.getSource() instanceof JComboBox) {
          JComboBox<String> box = (JComboBox<String>) arg0.getSource();
          String selectedFilter = (String) box.getSelectedItem();
          comboboxDisplay.setText("You selected: " + selectedFilter);
          System.out.println("Selected option: " + selectedFilter);
          if (Objects.equals(selectedFilter, "RGB-split")){

            bmwPanel.setVisible(true);
          }else{
            bmwPanel.setVisible(false);
          }
          if (Objects.equals(selectedFilter, "Compress")){

            compressPanel.setVisible(true);
          }else{
            compressPanel.setVisible(false);
          }
          if (Objects.equals(selectedFilter, "Split")){
//            setImg2(2, "Koala.jpg");
          }else{
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
    /*  case "Message":
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this, "This is a demo message", "Message", JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this, "You are about to be deleted.", "Last Chance", JOptionPane.WARNING_MESSAGE);
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this, "You have been deleted.", "Too late", JOptionPane.ERROR_MESSAGE);
        JOptionPane.showMessageDialog(SwingFeaturesFrame.this, "Please start again.", "What to do next", JOptionPane.INFORMATION_MESSAGE);
        break;
      case "Input":
        inputDisplay.setText(JOptionPane.showInputDialog("Please enter your username"));
        break;
      case "Option": {
        String[] options = {"Uno", "Dos", "Tres", "Cuatro", "Cinco", "seis", "siete", "ocho", "nueve", "dies"};
        int retvalue = JOptionPane.showOptionDialog(SwingFeaturesFrame.this, "Please choose number", "Options", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[4]);
        optionDisplay.setText(options[retvalue]);
      }
      break;*/
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
