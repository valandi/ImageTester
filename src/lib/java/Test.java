import com.applitools.eyes.Eyes;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yanir on 23/12/2015.
 */
public class Test extends TestUnit {

    protected final String appname_;
    private Queue<ITestable> steps_;
    protected RectangleSize viewportSize_;

    protected Test(File file, String appname) {
        this(file, appname, null);
    }

    protected Test(File file, String appname, RectangleSize viewportSize) {
        super(file);
        steps_ = new LinkedList<ITestable>();
        appname_ = appname;
        viewportSize_ = viewportSize;
    }

    public void run(Eyes eyes) {
        eyes.open(appname_, name(), viewportSize_);
        for (ITestable step : steps_) {
            try {
                step.run(eyes);
            } catch (Throwable e) {
                System.out.printf("Error in Step %s: \n %s \n This step will be skipped!", step.name(), e.getMessage());
                e.printStackTrace();
            }
        }
        TestResults result = eyes.close(false);
        String res = result.isNew() ? "New" : (result.isPassed() ? "Passed" : "Failed");
        System.out.printf("\t[%s] - %s\n", res, name());
        if (!result.isPassed() && !result.isNew())
            System.out.printf("\tResult url: %s\n", result.getUrl());
    }

    public void addStep(ImageStep step) {
        steps_.add(step);
    }

    public void addSteps(Collection<ITestable> steps) {
        steps_.addAll(steps);
    }
}
