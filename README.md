# Image Tester
A Cli tool to perform Applitools visual UI validation.
Based on images or PDFs.

This tool bridges the gap in integration between frameworks that are able to take the screenshots
of their applications and the ability to use those screenshots as an input to visual validations.

With Images Tester, all that  required in order to perform visual test on an application is to prepare
a folder with the recent screenshots in a folder on your disk and run the tool on it.

Please make sure you signed up at [Applitools](applitools.com) and you have your Applitools APIKEY.

# Example
Running the tool on a set of images located in a certain path using terminal/cmd

\> `java -jar BitmapTester -k <APIKEY> -f /path/to/folder/`
