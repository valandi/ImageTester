package com.applitools.ImageTester;

import com.applitools.eyes.Eyes;

import java.io.IOException;

interface ITestable {
    void run(Eyes eyes) throws IOException;

    String name();
}
