/*
 * The MIT License
 *
 * Copyright 2013 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.cli.CLICommandInvoker;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEmptyString.emptyString;

@WithJenkins
class CommandTest {

    private CLICommandInvoker command;

    private JenkinsRule j;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
        command = new CLICommandInvoker(j, new NextBuildNumberCommand());
    }

    @Test
    void updateShouldFailIfJobDoesNotExist() {
        final CLICommandInvoker.Result result = command
                .authorizedTo(Jenkins.READ, Item.READ, Item.CONFIGURE)
                .invokeWithArgs("project", "42");

        assertThat(result.stderr(), containsString("No such job 'project'"));
        assertThat("No output expected", result.stdout(), is(emptyString()));
        assertThat("Command is expected to fail", result.returnCode(), equalTo(3));
    }

    @Test
    void updateShouldFailWithoutJobConfigurePermission() throws Exception {
        j.createFreeStyleProject("project");

        final CLICommandInvoker.Result result = command
                .authorizedTo(Jenkins.READ, Item.READ)
                .invokeWithArgs("project", "42");

        assertThat(result.stderr(), containsString("user is missing the Job/Configure permission"));
        assertThat("No output expected", result.stdout(), is(emptyString()));
        assertThat("Command is expected to fail", result.returnCode(), equalTo(6));
    }

    @Test
    void updateShouldFailIfNewNumberIsLessThanTheOldOne() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        project.updateNextBuildNumber(42);
        project.scheduleBuild2(0).get(); // Next number is 43 now

        final CLICommandInvoker.Result result = command
                .authorizedTo(Jenkins.READ, Item.READ, Item.CONFIGURE)
                .invokeWithArgs("project", "42");

        assertThat("No output expected", result.stdout(), is(emptyString()));
        assertThat("Command is expected to fail", result.returnCode(), equalTo(NextBuildNumberCommand.INVALID_NEXT_BUILD_NUMBER));
        assertThat(result.stderr(), containsString("Provided build number 42 is less than last build number 43"));
        assertThat("Build number has not changed", project.getNextBuildNumber(), equalTo(43));
    }

    @Test
    void updateShouldBumpTheNextBuildNumber() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");

        final CLICommandInvoker.Result result = command
                .authorizedTo(Jenkins.READ, Item.READ, Item.CONFIGURE)
                .invokeWithArgs("project", "42");

        FreeStyleBuild build = project.scheduleBuild2(0).get();

        assertThat("No error output expected", result.stderr(), is(emptyString()));
        assertThat(build.number, equalTo(42));
        assertThat("Command is expected to succeed", result.returnCode(), equalTo(0));
    }
}
