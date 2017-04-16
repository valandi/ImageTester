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
import java.util.Queue;

public class Test extends TestUnit {

    protected final String appname_;
    protected RectangleSize viewportSize_;
    private Queue<ITestable> steps_;
    private String viewKey_;
    private String destinationFolder_;
    private boolean downloadDiffs_;
    private boolean getImages_;
    private boolean getGifs_;

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

    private void handleResultsDownload(TestResults results) throws Exception {
        if (downloadDiffs_ || getGifs_ || getImages_) {
            if (viewKey_ == null) throw new RuntimeException("The view-key cannot be null");


            if (downloadDiffs_ && !results.isNew() && !results.isPassed())
                new DownloadDiffs(results.getUrl(), destinationFolder_, viewKey_).run();
            if (getGifs_ && !results.isNew() && !results.isPassed())
                new AnimatedDiffs(results.getUrl(), destinationFolder_, viewKey_).run();
            if (getImages_)
                new DownloadImages(results.getUrl(), destinationFolder_, viewKey_, false, false).run();
        }
    }

    public void addStep(ImageStep step) {
        steps_.add(step);
    }

    public void addSteps(Collection<ITestable> steps) {
        steps_.addAll(steps);
    }

    public void setViewKey(String viewKey) {
        this.viewKey_ = viewKey;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder_ = destinationFolder;
    }

    public void setDownloadDiffs(boolean downloadDiffs) {
        this.downloadDiffs_ = downloadDiffs;
    }

    public void setGetImages(boolean getImages) {
        this.getImages_ = getImages;
    }

    public void setGetGifs(boolean getGifs) {
        this.getGifs_ = getGifs;
    }

    protected void printTestResults(TestResults result){
        String res = result.isNew() ? "New" : (result.isPassed() ? "Passed" : "Failed");
        System.out.printf("\t[%s] - %s", res, name());
        if (!result.isPassed() && !result.isNew())
            System.out.printf("\tResult url: %s", result.getUrl());
        System.out.println();

    }
}
