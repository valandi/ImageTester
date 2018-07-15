package com.applitools.ImageTester.TestObjects;

import com.applitools.ImageTester.Interfaces.IResultsReporter;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.images.Eyes;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Batch extends TestUnit {
    private BatchInfo batch_;
    private Queue<Test> tests_ = new LinkedList<Test>();
    private IResultsReporter reporter_;

    public Batch(File file, IResultsReporter reporter) {
        super(file);
        reporter_ = reporter;
    }

    public Batch(BatchInfo batch, IResultsReporter reporter) {
        super(batch.getName());
        batch_ = batch;
        reporter_ = reporter;
    }

    public void run(Eyes eyes) {
        batch_ = batch_ == null ? new BatchInfo(name()) : batch_;
        eyes.setBatch(batch_);
        System.out.printf("Batch: %s\n", name());
        for (Test test : tests_) {
            try {
                test.run(eyes);
            } finally {
                test.dispose();
            }
        }
        reporter_.onBatchFinished(batch_.getName());
        eyes.setBatch(null);
    }

    public void addTest(Test test) {
        tests_.add(test);
    }

    public void dispose() {
        if (tests_ == null) return;
        for (Test test : tests_) {
            test.dispose();
        }
    }
}
