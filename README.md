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
