package com.yanirta.lib;

import com.yanirta.TestObjects.IDisposable;
import com.yanirta.TestObjects.TestBase;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class TestExecutor {
    private final Logger logger_;
    private final BatchInfo flatBatch_;
    private final String sequenceName_;
    private ExecutorService executorService_;
    private ThreadLocal<Eyes> thEyes_;
    private Queue<Future<ExecutorResult>> results_ = new LinkedList<>();

    public TestExecutor(int threads, EyesFactory eyesFactory, Config conf) {
        this.executorService_ = Executors.newFixedThreadPool(threads);
        this.thEyes_ = ThreadLocal.withInitial(() -> eyesFactory.build());
        this.logger_ = conf.logger;
        this.flatBatch_ = conf.flatBatch;
        this.sequenceName_ = conf.sequenceName;
        if(this.flatBatch_!=null)
            this.flatBatch_.setNotifyOnCompletion(conf.notifyOnComplete);
    }

    public void enqueue(TestBase test, BatchInfo batch) {
        Future<ExecutorResult> f = executorService_.submit(() -> {
            long startTime = System.nanoTime();
            Eyes eyes = thEyes_.get();
            eyes.setBatch(flatBatch_ != null ? flatBatch_ : batch);
            if (sequenceName_ != null && !StringUtils.isEmpty(sequenceName_))
                eyes.getBatch().setSequenceName(sequenceName_);
            TestResults result = test.runSafe(eyes);
            eyes.abortIfNotClosed();
            eyes.setBatch(null);
            if (test instanceof IDisposable)
                ((IDisposable) test).dispose();
            long endTime = System.nanoTime();

            return new ExecutorResult(result, (endTime - startTime));
        });

        results_.add(f);
    }

    public void join() {
        int total = results_.size();
        int curr = 1;
        while (!results_.isEmpty()) {
            try {
                logger_.printProgress(curr++, total);
                ExecutorResult result = results_.remove().get();
                logger_.reportResult(result);
            } catch (Exception e) {
                logger_.reportException(e);
            }
        }

        executorService_.shutdown();
    }
}
