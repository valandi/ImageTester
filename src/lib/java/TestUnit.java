import com.applitools.eyes.Eyes;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public abstract class TestUnit {
    protected final File file_;

    protected TestUnit(File file) {
        file_ = file;
        if (file != null && !file.canRead()) {
            throw new RuntimeException(String.format("Unreadable path/file %s, might be a permission issue!", file.getAbsolutePath()));
        }
    }

    public abstract void run(Eyes eyes) throws IOException;


    public String name() {
        return file_.getName();
    }
}
