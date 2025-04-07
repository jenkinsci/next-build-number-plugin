package org.jvnet.hudson.plugins.nextbuildnumber;


import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Build;
import hudson.model.FreeStyleProject;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithJenkins
class JobTest extends JobTestBase {

    @Test
    void testFreeStyleProject() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject("jobTest");
        Build<?,?> build = project.scheduleBuild2(0).get();
        assertEquals(1, build.getNumber());

        performNextBuildNumberChange(project, "2","222");

        build = project.scheduleBuild2(0).get();
        assertEquals(222, build.getNumber());
    }

    @Test
    void testMatrixProject() throws Exception {
        MatrixProject project = jenkins.createProject(MatrixProject.class);
        MatrixBuild build = project.scheduleBuild2(0).get();
        assertEquals(1, build.getNumber());

        performNextBuildNumberChange(project, "2","222");

        build = project.scheduleBuild2(0).get();
        assertEquals(222, build.getNumber());
    }

    @Test
    void testPipelineProject() throws Exception {
        WorkflowJob project = jenkins.createProject(WorkflowJob.class);
        WorkflowRun build = project.scheduleBuild2(0).get();
        assertEquals(1, build.getNumber());

        performNextBuildNumberChange(project, "2","222");

        build = project.scheduleBuild2(0).get();
        assertEquals(222, build.getNumber());
    }
}
