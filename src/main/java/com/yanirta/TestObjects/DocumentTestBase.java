package com.yanirta.TestObjects;

import com.yanirta.lib.Config;
import com.yanirta.lib.Utils;

import java.io.File;
import java.util.List;

public abstract class DocumentTestBase extends TestBase {
    protected List<Integer> pageList_;

    public DocumentTestBase(File file, Config conf) {
        super(file, conf);
        this.pageList_ = Utils.parsePagesNotation(conf.pages);
    }

    @Override
    public String name() {
        if (config().forcedName != null)
            return file().getName();

        //Else
        String pagesText = "";
        if (config().pages != null && config().includePageNumbers)
            pagesText = " pages [" + config().pages + "]";
        return file().getName() + pagesText;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
