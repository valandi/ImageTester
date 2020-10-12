package com.yanirta.TestObjects;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import com.yanirta.lib.Config;
import com.yanirta.lib.Logger;
import com.yanirta.lib.Utils;

import java.awt.image.BufferedImage;
import java.io.File;

public abstract class TestBase implements ITest {
    private static final String FILE_NAME_PROP = "Filename";
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

    @Override
    public RectangleSize viewport(BufferedImage image) {
        if (this.conf_.viewport == null && image != null)
            return new RectangleSize(image.getWidth(), image.getHeight());
        return this.conf_.viewport;
    }

    @Override
    public RectangleSize viewport() {
        return viewport(null);
    }

    public String name() {
        if (conf_.forcedName != null)
            return conf_.forcedName;
        else
            return file_.getName();
    }

    public TestResults runSafe(Eyes eyes) {
        try {
            eyes.addProperty(FILE_NAME_PROP, file_.getName());
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
