

1. imageContent:
   The `imageContent` class is designed to represent an image along with its associated name and 
   content. This class serves as a container for storing image data.
2. imageOperations:
   The `imageOperations` interface has all the function signatures of the operations that 
   can be performed on an image like horizontal flip, blur, sharpen, etc. This interface is 
   implemented by the `AbstractImage` class.
3. AbstractImage:
   All the operations for images are done in `AbstractImage` class (except the load and save 
   operations), which implements the `imageOperations` interface. This class is extended by the 
   `PNGImage`, `JPGImage`, and `PPMImage` class.
4. PNGImage:
   Extending the `AbstractImage` class is the `PNGImage` class so this means it automatically 
   performs all the operations on PNG images. The PNG image formats utilizes the image operations 
   specified in the `imageOperations` interface that are implemented in the abstract class. The 
   load and save functions for PNG images are overridden in this class.
5. PPMImage:
   Extending the `AbstractImage` class is the `PPMImage` class so this means it automatically
   performs all the operations on PPM images. The PPM image formats utilizes the image operations
   specified in the `imageOperations` interface that are implemented in the abstract class. The
   load and save functions for PPM images are overridden in this class.
6. JPGImage:
   Extending the `PNGImage` class is the `JPGImage` class.
   To capture photos in the JPG file format, the `saveImage` function is overridden and the rest
   of the functions are the same as the PNG class.
7. model.model-view-controller.controller:

   controller.controller (controller.controller): The `controller` class is responsible 
   for controlling the interaction
   between the model and view.
   It implements the ActionListener interface to handle user actions in the view.
   It interacts with the model and the view to update and display data based on user input.

   model.model.IModel (Interface): The `IModel` interface defines methods that the 
   `model` must implement.

   view.IView (Interface): The `IView` interface defines methods that the `view` must 
   implement.
7. `MVCExampleBasicMVC`:
   This class serves as the entry point of the application.
   It initializes the model, view, and controller and sets them up to work together.

The model class is responsible for interpreting the script file and executing the commands 
in it.
model.model command processing can be executed by following a specific protocol.
Using the executeScriptFromFile approach, the model.model class of the model is responsible for
administering and performing commands in a sequential order.
Using image manipulation techniques, the program carries out instructions found in a script
file that it interprets.

Implementing the model.model.imageOperations interface are three classes: model.PPMImage, 
model.model.JPGImage, and PNGJPGImage,
each with their unique image manipulation abilities.

**How to run**:
1. Run the main method in the `Main` class.
2. Enter the file path of the script file in the text field and click execute or click the button 
   Go to command prompt to enter the commands manually.
3. The resulting images will be saved in the specified file mentioned in the script.
4. Make sure to change the file path in the script file to the path of the file you want to save 
   the images in.

**Overall Flow**:

The main method initializes the model, view, and controller.
The user interacts with the view by entering either the file path or clicking the button
that redirects them to the command prompt.
The controller responds to user actions and communicates with the model.
The model processes image commands and saves the resulting images in the specified file.

**Citations**:

test-image: "https://w7.pngwing.com/pngs/306/257/png-transparent-spongebob-squarepants-spongebob-heroes-cartoon-vehicle.png" 
(png image)


