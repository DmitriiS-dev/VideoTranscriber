package com.videoTranscriber.demo;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        com.videoTranscriber.demo.tests.FileSavingLogicTests.class,
        com.videoTranscriber.demo.tests.VideoProcessingServiceTests.class,
        })

// Stays empty - purpose is to run the tests above
public class CoreServicesTests {
}
