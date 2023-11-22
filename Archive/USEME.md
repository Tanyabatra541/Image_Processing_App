# USE-ME file

This document provides a comprehensive guide on the script commands supported by the image
processing application. Each command is detailed with examples and specific conditions, if any.

## Script Commands

- *Load an Image for PNG*:
    - `load <image_path> <image_name>`
    - Example: `load res/image.png testImage`
- *Load an Image for JPG*:
    - `load <image_path> <image_name>`
    - Example: `load res/image.jpg testImage`
- *Load an Image for PPM*:
    - `load <image_path> <image_name>`
    - Example: `load res/image.ppm testImage`
- *Save an Image for PNG*:
    - `save <image_path> <image_name>`
    - Example: `save res/testImage.png testImage`
- *Save an Image for JPG*:
    - `save <image_path> <image_name>`
    - Example: `save res/testImage.jpg testImage`
- *Save an Image for PPM*:
    - `save <image_path> <image_name>`
    - Example: `save res/testImage.ppm testImage`
- *Brighten an Image*:
    - `brighten <increment> <source_image> <dest_image>`
    - Example: `brighten 50 testImage testImage-brighter`
- *Darken an Image*:
    - ` brighten <-increment> <source_image> <dest_image>`
    - Example: `brighten -50 testImage testImage-dark`
- *Horizontal Flip*:
    - `horizontal-flip <source_image> <dest_image>`
    - Example: `hhorizontal-flip testImage testImage-horizontal`
- *Vertical Flip*:
    - `vertical-flip <source_image> <dest_image>`
    - Example: `vertical-flip testImage testImage-vertical`
- *Blur an Image*:
    - `blur <source_image> <dest_image>`
    - Example: `blur testImage testImage-blur`
- *Sharpen an Image*:
    - `sharpen <source_image> <dest_image>`
    - Example: `sharpen testImage testImage-sharp`
- *Sepia an Image*:
    - `sepia <source_image> <dest_image>`
    - Example: `sepia testImage testImage-sepia`
- *Red Component of an Image*:
    - `red-component <source_image> <dest_image>`
    - Example: `red-component testImage red-component-testImage`
- *Green Component of an Image*:
    - `green-component <source_image> <dest_image>`
    - Example: `green-component testImage green-component-testImage` 
- *Blue Component of an Image*:
    - `blue-component <source_image> <dest_image>`
    - Example: `blue-component testImage blue-component-testImage`
- *Value Component of an Image*:
    - `value-component <source_image> <dest_image>`
    - Example: `value-component testImage value-component-testImage`
- *Intensity Component of an Image*:
    - `intensity-component <source_image> <dest_image>`
    - Example: `intensity-component testImage intensity-component-testImage`
- *Luma Component of an Image*:
    - `luma-component <source_image> <dest_image>`
    - Example: `luma-component testImage luma-component-testImage`
- *RGB Split of an Image*:
    - `rgb-split <source_image> <dest_red_image> <dest_green_image> <dest_blue_image>`
    - Example: `rgb-split testImage red-split-testImage green-split-testImage blue-split-testImage`
- *RGB Combine of an Image*:
    - `rgb-combine <combined_image> <red_image> <green_image> <blue_image>`
    - Example: `rgb-combine rgb-combine-testImage red-split-testImage green-split-testImage blue-split-testImage`
- *Compress an Image*:
    - `compress <compression_percentage> <source_image> <dest_image>`
    - Example: `compress 50 testImage compress-testImage`
- *Histogram of an Image*:
    - `histogram <source_image> <dest_image>`
    - Example: `histogram testImage histogram-testImage`
- *Color Correct an Image*:
    - `color-correct <source_image> <dest_image>`
    - Example: `color-correct testImage color-correct-testImage`
- *Levels Adjust an Image*:
   - `levels-adjust <b> <m> <w> <source_image> <dest_image>`
   - Example: `levels-adjust 20 100 255 testImage levels-adjust-testImage`
- *Levels Adjust an Image with Split*:
   - `levels-adjust <b> <m> <w> <source_image> <dest_image> split <splitPercentage>`
   - Example: `levels-adjust 20 100 255 testImage levels-adjust-split-testImage split 50`
- *Color Correct an Image with Split*:
   - `color-correct <source_image> <dest_image> split <splitPercentage>`
   - Example: `color-correct testImage color-correct-split-testImage split 50`
- *Blur an Image with Split*:
   - `blur <source_image> <dest_image> split <splitPercentage>`
   - Example: `blur testImage blur-split split 50`
- *Sharpen an Image with Split*:
   - `sharpen <source_image> <dest_image> split <splitPercentage>`
   - Example: `sharpen testImage sharpen-split split 50`
- *Sepia an Image with Split*:
   - `sepia <source_image> <dest_image> split <splitPercentage>`
   - Example: `sepia testImage sepia-split split 50`
- *Greyscale an Image with Split*:
   - `greyscale <source_image> <dest_image> split <splitPercentage>`
   - Example: `greyscale testImage greyscale-split split 50`
- *Run script file*:
   - `-file <file_path>`
   - Example: `-file res/scriptFile.txt`

Commands can be executed for different image formats (PPM, JPG, JPEG, PNG) with the same syntax.

Conditions:
1. An image can only be loaded if it exists in the file path specified.
2. Only file extensions accepted in load are PNG, JPG, JPEG, PPM and txt(for script file). 
3. Only file extensions accepted in save are PNG, JPG, JPEG and PPM.
4. Filtration operation can be performed only on an image name which has been already loaded.
5. The red, blue and green images for the RGB combine command should have already been loaded 
   before the combine command is executed.
6. Split can only be performed on color correct, level adjust, blur, sharpen, sepia and greyscale.
7. Any percentages should lie between 0 and 100.
8. The b, m and w values of level adjust should lie between 0 and 255.
