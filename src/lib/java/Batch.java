import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Eyes;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Batch extends TestUnit {
    private Queue<Test> tests_;

    protected Batch(File file) {
        super(file);
        tests_ = new LinkedList<Test>();
    }

    @Override
    public void run(Eyes eyes) {
        eyes.setBatch(new BatchInfo(name()));
        System.out.printf("Batch: %s\n", name());
        for (Test test : tests_) {
            test.run(eyes);
        }
        eyes.setBatch(null);
    }

    public void addTest(Test test) {
        tests_.add(test);
    }
}
