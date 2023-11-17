# Image Processing Application

1. ImageContent:
   The `ImageContent` class is designed to represent an image along with its associated name and 
   content. This class serves as a container for storing image data.
2. ImageOperations:
   The `ImageOperations` interface has all the function signatures of the operations that 
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
7. Histogram:
   The `Histogram` class represents a histogram for RGB color values and offers functionality for 
   creating histogram images, adding color values to the histogram, calculating the maximum count 
   of color values, and finding peak values in the histogram arrays. This class facilitates the 
   analysis and visualization of color distribution in an image through histograms.
8. Compression:
   The `Compression` class provides methods for compressing images using Haar Wavelet Transform 
   and thresholding techniques. It initializes, pads, and transforms the color channels of an image 
   based on the specified compression percentage, applies thresholding, and performs inverse 
   transformations to achieve image compression while maintaining visual quality. The class 
   ultimately returns the compressed RGB data of the image.

Model-View-Controller:
- Controller:
   The `Controller` class serves as the controller in the Model-View-Controller (MVC) architectural 
   pattern. The class handles various image processing operations based on user input, such as 
   loading and saving images, applying filters, adjusting levels, and generating histograms and 
   tells the model what to do and sends the control to the model based on the user input. 
   Additionally, it supports the execution of image processing scripts from a text file.
- Model:
   The `Model` class is responsible for executing the commands in the script file or the command 
   passed through the command line. The model command processing can be executed by following a 
   specific protocol. Using the `executeScriptFromFile` approach, the `Model` class of the model is 
   responsible for administering and performing commands in a sequential order. Using image 
   manipulation techniques, the program carries out instructions found in a script file that it 
   interprets.

`Main`:
   This class serves as the entry point of the application.
   It initializes the model, view, and controller and sets them up to work together.

**How to run without JAR file**:
1. Run the main method in the `Main` class.
2. Enter the commands one by one in the command prompt until the user types "exit" to exit the 
   program.
3. If not implementing interactive command approach, enter the command "-file <file path>" to 
   execute the script file. It will automatically execute the file commands and exit the program.

**Overall Flow**:

The main method initializes the model, view, and controller.
The runProgram function is called from the main which gives the control to the controller.
Now, the controller asks the user to enter the command or the file path and the control goes to the
model to perform the operations typed by the user. The controller tells the model what to do.
If the user enters the command, the controller executes the command and asks the user to enter
another command until the user types "exit" to exit the program.
If the user enters the file path, the controller executes the commands in the file and exits the
program.
The images are then saved in the res/ folder.

**Parts of the program that are complete**:
1. The program can perform all the operations on PNG, JPG, JPEG and PPM images.
2. The program can perform all the operations on PNG, JPG, JPEG and PPM images using the interactive 
   command approach.
3. The program can perform all the operations on PNG, JPG, JPEG and PPM images using the script file
4. MVC architecture is implemented.
5. Supports all the operations mentioned in the assignment document.
6. Supports all the operations from the previous assignment also.
7. JAR file is created and working.

**Design changes and justifications**:

- No change made in the `JPGImage`, `PNGImage`, `PPMImage`, and `ImageContent` classes.
- The `Histogram` class was added to the model to facilitate the analysis and visualization of  
  RGB values in an image through histograms.
- The `Compression` class was added to the model to provide methods for compressing images using 
  Haar Wavelet Transform and thresholding techniques.
- The compression and histogram operations were added to the `ImageOperations` interface and 
  implemented in the `AbstractImage` class.
- In the controller, additional switch cases were added for: 
  - histogram
  - compression
  - -file
  - color-correct/color-correct split 
  - levels-adjust/ levels-adjust split
- To add the split functionality in the existing model, an additional parameter "splitPercentage" 
  was added to levels-adjust, color-correct, blur, sharpen, sepia, and greyscale. But in order to 
  make program scalable, the main method was made private which takes in the splitPercentage, source 
  image, and destination image and then two method-overloaded public functions were added, one 
  which takes in the split functionality into account, the other which does not take split into 
  account (0 is sent as the splitPercentage parameter). This way our previous test cases will not 
  change and works perfectly, and also both the split and non-split functionality is implemented.
  The user can choose whether they want the split functionality to be implemented or not.
   


**Citations**:

test-image: "https://w7.pngwing.com/pngs/306/257/png-transparent-spongebob-squarepants-spongebob-heroes-cartoon-vehicle.png" 
(png image)


