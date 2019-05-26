package io.jenkins.plugins.analysis.warnings.tasks;

import java.util.List;

import org.junit.Test;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.Run;
import hudson.model.Slave;

import io.jenkins.plugins.analysis.core.model.ResultAction;
import io.jenkins.plugins.analysis.core.testutil.IntegrationTestWithJenkinsPerSuite;

import static edu.hm.hafner.analysis.assertj.Assertions.*;

public class RemoteITest extends IntegrationTestWithJenkinsPerSuite {

    @Test
    public void defaultTest(){
    //StepsITest, IntegrationTest

        WorkflowJob job = createPipeline();

        Slave slave1 = createAgent("mySlave1");
        Slave slave2 = createAgent("mySlave2");

        copySingleFileToAgentWorkspace(slave1, job, "../eclipse.txt", "issues.txt");
        copySingleFileToAgentWorkspace(slave2, job, "../eclipse.txt", "issues.txt");


        Run<?, ?> run = buildSuccessfully(job);
        List<ResultAction> actions = run.getActions(ResultAction.class);

        assertThat(actions).hasSize(2);

        ResultAction first;
        ResultAction second;
        if (actions.get(0).getId().equals("java-1")) {
            first = actions.get(0);
            second = actions.get(1);
        }
        else {
            first = actions.get(1);
            second = actions.get(0);
        }

        assertThat(first.getResult().getIssues()).hasSize(5);
        assertThat(second.getResult().getIssues()).hasSize(3);
    }
}
