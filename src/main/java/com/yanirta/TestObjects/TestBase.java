package com.yanirta.TestObjects;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import com.yanirta.lib.Config;
import com.yanirta.lib.Logger;
import com.yanirta.lib.Utils;

import java.io.File;

public abstract class TestBase implements ITest {
    private static final String ORIGINAL_NAME = "originalName";
    private final File file_;
    private final Config conf_;

    public TestBase(File file, Config conf) {
        this.file_ = file;
        this.conf_ = conf;
    }

    public File file() {
        return file_;
    }

    public Config config() {
        return conf_;
    }

    public String appName() {
        return this.conf_.appName;
    }

    public RectangleSize viewport() {
        return this.conf_.viewport;
    }

    public String name() {
        if (conf_.forcedName != null)
            return conf_.forcedName;
        else
            return file_.getName();
    }

    public TestResults runSafe(Eyes eyes) {
        try {
            if (conf_.forcedName != null)//In case the name is overridden, we will use property to store the name.
                eyes.addProperty(ORIGINAL_NAME, file_.getName());
            TestResults res = run(eyes);
            Utils.handleResultsDownload(conf_.eyesUtilsConf, res);
            return res;
        } catch (Exception e) {
            logger().reportException(e);
        } finally {
            eyes.abortIfNotClosed();
            eyes.clearProperties();
        }

        return null;
    }

    public Logger logger() {
        return conf_.logger;
    }
}
