package com.blazemeter.taurus.junit4;

import com.blazemeter.taurus.junit.CustomListener;
import com.blazemeter.taurus.junit.Sample;
import com.blazemeter.taurus.junit.TaurusReporter;
import com.blazemeter.taurus.junit.Utils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.logging.Logger;

public class JUnit4Listener extends RunListener {

    private static final Logger log = Logger.getLogger(JUnit4Listener.class.getName());
    private final CustomListener listener;

    public JUnit4Listener(TaurusReporter reporter) {
        super();
        listener = new CustomListener(reporter);
    }

    public void testRunStarted(Description description) {
        log.info("Run Started: " + description.getDisplayName());
    }

    public void testRunFinished(Result result) throws Exception {
        log.info("Run Finished, successful=" + result.wasSuccessful() + ", run count=" + result.getRunCount());
    }

    public void testStarted(Description description) throws Exception {
        listener.startSample(description.getMethodName(), description.getClassName());
    }

    public void testFinished(Description description) throws Exception {
        listener.finishSample();
    }

    public void testFailure(Failure failure) throws Exception {
        log.severe(String.format("failed %s", failure.toString()));
        listener.getPendingSample().setStatus(Sample.STATUS_BROKEN);
        String exceptionName = failure.getException().getClass().getName();
        listener.getPendingSample().setErrorMessage(exceptionName + ": " + failure.getMessage());
        listener.getPendingSample().setErrorTrace(Utils.getStackTrace(failure.getException()));
    }

    public void testAssumptionFailure(Failure failure) {
        log.severe(String.format("assert failed %s", failure.toString()));
        listener.getPendingSample().setStatus(Sample.STATUS_FAILED);
        String exceptionName = failure.getException().getClass().getName();
        listener.getPendingSample().setErrorMessage(exceptionName + ": " + failure.getMessage());
        listener.getPendingSample().setErrorTrace(Utils.getStackTrace(failure.getException()));
    }

    public void testIgnored(Description description) throws Exception {
        log.warning(String.format("ignored %s", description.getDisplayName()));
        testStarted(description);
        listener.finishSample(Sample.STATUS_SKIPPED, description.getDisplayName(), null);
    }

}