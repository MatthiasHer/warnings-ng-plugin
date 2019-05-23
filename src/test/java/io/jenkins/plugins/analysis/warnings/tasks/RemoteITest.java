package io.jenkins.plugins.analysis.warnings.tasks;

import org.junit.Test;

import hudson.model.Slave;

import io.jenkins.plugins.analysis.core.testutil.IntegrationTestWithJenkinsPerSuite;

public class RemoteITest extends IntegrationTestWithJenkinsPerSuite {

    @Test
    public void defaultTest(){


        Slave slave = createAgent("mySlave");
    }
}
