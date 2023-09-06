package org.jvnet.hudson.plugins.nextbuildnumber;

import org.htmlunit.html.HtmlForm;
import hudson.model.Job;
import hudson.security.AuthorizationStrategy;
import org.junit.Before;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class JobTestBase {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Before
    public void setUp() {
        jenkins.getInstance().setAuthorizationStrategy(new AuthorizationStrategy.Unsecured());
    }

    @SuppressWarnings("rawtypes")
    protected void performNextBuildNumberChange(Job project, String currenNumber, String changeToNumber) throws Exception {
        HtmlForm form = jenkins.createWebClient().getPage(project, "nextbuildnumber").getFormByName("nextbuildnumber");
        assertEquals(currenNumber, form.getInputByName("nextBuildNumber").getDefaultValue());
        form.getInputByName("nextBuildNumber").setValue(changeToNumber);
        jenkins.submit(form);

        form = jenkins.createWebClient().getPage(project, "nextbuildnumber").getFormByName("nextbuildnumber");
        assertEquals(changeToNumber, form.getInputByName("nextBuildNumber").getDefaultValue());
    }
}
