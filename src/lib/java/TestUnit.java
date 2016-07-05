import com.applitools.eyes.Eyes;

import java.io.File;
import java.io.IOException;

public abstract class TestUnit {
    protected final File file_;

    protected TestUnit(File file) {
        file_ = file;
    }

    public abstract void run(Eyes eyes) throws IOException;


    public String name() {
        return file_.getName();
    }
}
