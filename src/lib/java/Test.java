import com.applitools.eyes.Eyes;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yanir on 23/12/2015.
 */
public class Test extends TestUnit {

    protected final String appname_;
    private Queue<ImageStep> steps_;
    private RectangleSize viewportSize_;

    protected Test(File file, String appname) {
        this(file, appname, null);
    }

    protected Test(File file, String appname, RectangleSize viewportSize) {
        super(file);
        steps_ = new LinkedList<ImageStep>();
        appname_ = appname;
        viewportSize_ = viewportSize;
    }

    @Override
    public void run(Eyes eyes) {
        String res = null;
        Exception ex = null;
        TestResults result = null;
        try {
            eyes.open(appname_, name(), viewportSize_);
            for (ImageStep step : steps_) {
                step.run(eyes);
            }
            result = eyes.close(false);
            res = result.isNew() ? "New" : (result.isPassed() ? "Passed" : "Failed");
        } catch (IOException e) {
            res = "Error";
            ex = e;
        } finally {
            System.out.printf("\t[%s] - %s\n", res, name());
            if (!result.isPassed() && !result.isNew()) {
                System.out.printf("\tReview the result: %s\n", result.getUrl());
            }
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }

    public void addStep(ImageStep step) {
        steps_.add(step);
    }
}
