package controller;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.ActionEvent;
import org.junit.Before;
import org.junit.Test;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.View;

import model.IModel;
import model.Model;
import model.PNGImage;
import view.IView;
import view.JFrameView;
import java.io.IOException;


public class ControllerTest {

  private static PNGImage pngJpgImage;

  private Model model;
  private IView view;
  private Controller controller;

  @Before
  public void setUp() {
    pngJpgImage = new PNGImage();
    model = new Model();
    view = new JFrameView(null);
    controller = new Controller(model, view);
  }

  @Test
  public void testLoad() throws IOException {
    controller.parseAndExecute("load C:/Users/Lenovo/Documents/CS5010_PDP/Assignmnet4/script/png/manhattan-dark.png img");
    assertTrue(pngJpgImage.getImageMap().containsKey("img"));


  }
}