package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.model.Job;
import hudson.security.AuthorizationStrategy;
import org.htmlunit.html.HtmlForm;
import org.junit.jupiter.api.BeforeEach;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithJenkins
class JobTestBase {

    protected JenkinsRule jenkins;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        jenkins = rule;
        jenkins.getInstance().setAuthorizationStrategy(new AuthorizationStrategy.Unsecured());
    }

    @SuppressWarnings("rawtypes")
    protected void performNextBuildNumberChange(Job project, String currenNumber, String changeToNumber) throws Exception {
        try (JenkinsRule.WebClient client = jenkins.createWebClient()) {
            HtmlForm form = client.getPage(project, "nextbuildnumber").getFormByName("nextbuildnumber");
            assertEquals(currenNumber, form.getInputByName("nextBuildNumber").getDefaultValue());
            form.getInputByName("nextBuildNumber").setValue(changeToNumber);
            jenkins.submit(form);

            form = client.getPage(project, "nextbuildnumber").getFormByName("nextbuildnumber");
            assertEquals(changeToNumber, form.getInputByName("nextBuildNumber").getDefaultValue());
        }
    }
}
