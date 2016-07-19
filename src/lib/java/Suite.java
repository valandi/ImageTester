import com.applitools.eyes.Eyes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.applitools.eyes.RectangleSize;
import org.apache.commons.io.comparator.*;

public class Suite extends TestUnit {
    private Queue<Batch> batches_;
    private Test test_ = null;

    protected Suite(File file) {
        super(file);
        batches_ = new LinkedList<Batch>();
    }

    @Override
    public void run(Eyes eyes) throws IOException {
        if (batches_.isEmpty() && test_ == null) {
            System.out.printf("Nothing to test!\n");
            return;
        }

        for (Batch batch : batches_) {
            batch.run(eyes);
        }

        if (test_ != null) {
            System.out.printf("> No batch <\n");
            test_.run(eyes);
        }
    }

    public static TestUnit build(File curr, String appname, RectangleSize viewport) throws IOException {
        if (!curr.exists()) {
            System.out.printf(String.format("The folder %s doesn't exists\n", curr.getAbsolutePath()));
            return null;
        }

        if (appname == null) {
            appname = "ImageTester";
        }

        if (curr.isFile()) {
            if (PDFTest.supports(curr)) return new PDFTest(curr, appname);
            if (PostscriptTest.supports(curr)) return new PostscriptTest(curr, appname);
            return ImageStep.supports(curr) ? new ImageStep(curr) : null;
        }

        Test currTest = null;
        Batch currBatch = null;
        Suite currSuite = null;

        File[] files = curr.listFiles();
        Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);

        for (File file : files) {
            TestUnit unit = build(file, appname, viewport);
            if (unit instanceof ImageStep) {
                if (currTest == null) currTest = new Test(curr, appname, viewport);
                currTest.addStep((ImageStep) unit);
            } else if (unit instanceof Test) {
                if (currBatch == null) currBatch = new Batch(curr);
                currBatch.addTest((Test) unit);
            } else if (unit instanceof Batch) {
                if (currSuite == null) currSuite = new Suite(curr);
                currSuite.batches_.add((Batch) unit);
            } else if (unit instanceof Suite) {
                Suite suite = (Suite) unit;
                if (currSuite == null) currSuite = new Suite(curr);
                currSuite.batches_.addAll(suite.batches_);
                if (suite.test_ != null) {
                    if (currBatch == null) currBatch = new Batch(curr);
                    currBatch.addTest(suite.test_);
                    suite.test_ = null;
                }
                suite.batches_.clear();
            } else {
                //SKIP
            }
        }

        //Simple cases
        if (currTest == null && currBatch == null && currSuite == null) return null;
        if (currTest != null && currBatch == null && currSuite == null) return currTest;
        if (currTest == null && currBatch != null && currSuite == null) return currBatch;
        if (currTest == null && currBatch == null && currSuite != null) return currSuite;
        //The complicated case
        if (currSuite == null) currSuite = new Suite(curr);
        if (currBatch != null) currSuite.batches_.add(currBatch);
        if (currTest != null) currSuite.test_ = currTest;

        return currSuite;
    }


}
