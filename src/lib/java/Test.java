import com.applitools.Commands.AnimatedDiffs;
import com.applitools.Commands.DownloadDiffs;
import com.applitools.Commands.DownloadImages;
import com.applitools.eyes.Eyes;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Test extends TestUnit {

    protected final String appname_;
    protected RectangleSize viewportSize_;
    private Queue<ITestable> steps_;
    private EyesUtilitiesConfig eyesUtilitiesConfig_;
    private List<Integer> pagesToInclude_;

    public List<Integer> getPagesToInclude() {
        return pagesToInclude_;
    }

    public void setPagesToInclude(List<Integer> pagesToInclude) {
        this.pagesToInclude_ = pagesToInclude;
    }



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
        try {
            TestResults result = eyes.close(false);
            printTestResults(result);
            handleResultsDownload(result);
        } catch (EyesException e) {
            System.out.printf("Error closing test %s \nPath: %s \nReason: %s \n",
                    name(),
                    file_.getAbsolutePath(),
                    e.getMessage());

            System.out.println("Aborting...");
            try {
                eyes.abortIfNotClosed();
                System.out.printf("Aborted!");
            } catch (Throwable ex) {
                System.out.printf("Error while aborting: %s", ex.getMessage());
                System.out.println("I don't have any idea what just happened.");
                System.out.println("Please try reaching our support at support@applitools.com");
            }
        } catch (Exception e) {
            System.out.println("Oops, something went wrong!");
            System.out.print(e);
            e.printStackTrace();
        }
    }

    protected void handleResultsDownload(TestResults results) throws Exception {
        if (eyesUtilitiesConfig_.getDownloadDiffs() || eyesUtilitiesConfig_.getGetGifs() || eyesUtilitiesConfig_.getGetImages()) {
            if (eyesUtilitiesConfig_.getViewKey() == null) throw new RuntimeException("The view-key cannot be null");
            if (eyesUtilitiesConfig_.getDownloadDiffs() && !results.isNew() && !results.isPassed())
                new DownloadDiffs(results.getUrl(), eyesUtilitiesConfig_.getDestinationFolder(), eyesUtilitiesConfig_.getViewKey()).run();
            if (eyesUtilitiesConfig_.getGetGifs() && !results.isNew() && !results.isPassed())
                new AnimatedDiffs(results.getUrl(), eyesUtilitiesConfig_.getDestinationFolder(), eyesUtilitiesConfig_.getViewKey()).run();
            if (eyesUtilitiesConfig_.getGetImages())
                new DownloadImages(results.getUrl(), eyesUtilitiesConfig_.getDestinationFolder(), eyesUtilitiesConfig_.getViewKey(), false, false).run();
        }
    }

    public void addStep(ImageStep step) {
        steps_.add(step);
    }

    public void addSteps(Collection<ITestable> steps) {
        steps_.addAll(steps);
    }

    protected void printTestResults(TestResults result){
        String res = result.getSteps()>0 ? (result.isNew() ? "New" : (result.isPassed() ? "Passed" : "Failed")) : "Empty";
        System.out.printf("\t[%s] - %s", res, name());
        if (!result.isPassed() && !result.isNew())
            System.out.printf("\tResult url: %s", result.getUrl());
        System.out.println();

    }

    public void setEyesUtilitiesConfig(EyesUtilitiesConfig eyesUtilitiesConfig){
        eyesUtilitiesConfig_=eyesUtilitiesConfig;
    }



}
