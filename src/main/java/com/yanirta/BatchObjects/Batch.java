package com.yanirta.BatchObjects;

import com.applitools.eyes.BatchInfo;
import com.yanirta.lib.Config;

import java.io.File;

public class Batch extends BatchBase {

    public Batch(File file, Config conf) {
        super(new BatchInfo(file.getName()), conf.notifyOnComplete);
    }

    public Batch(String name, Config conf) {
        super(new BatchInfo(name), conf.notifyOnComplete);
    }
}
