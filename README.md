# Image Tester
A Cli tool to perform Applitools visual UI validation.
Based on images or PDFs.

This tool bridges the gap in integration between frameworks that are able to take screenshots
of their applications and the ability to perform visual validation using Applitools based on them.

With Images Tester, all that required in order to perform visual test on application is to prepare
a folder with the recent screenshots on your disk and run the tool on it.

Please make sure you signed up at [Applitools](applitools.com) and you have your Applitools APIKEY.

# Example
Running the tool on a set of images located in a certain path using terminal/cmd

`\> java -jar BitmapTester -k <APIKEY> -f /path/to/folder/`

# Supporting regions
To validate only a specific regions of a particular image the folder must contain .regions file with the same name as the image.
ie. If my image file is 'step1.png' then the regions file should be 'step1.regions'.
Internally the format should be csv 4 columns representing left,top,width,height as follow:

step1.regions:
```
0,0,100,200
500,100,240,123
```
Empty line will be considered as the entire image.
