package org.jvnet.hudson.plugins.nextbuildnumber;


import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

import java.util.logging.Logger;

@Extension(optional = true)
public class NextBuildNumberJobDslExtension extends ContextExtensionPoint {

    private static final Logger LOG = Logger.getLogger(NextBuildNumberJobDslExtension.class.getName());

    public NextBuildNumberJobDslExtension() {
        LOG.warning("Instantiated class " + getClass().getName());
    }


    @DslExtensionMethod(context = WrapperContext.class)
    public Object nextBuildNumber(int nextBuildNumber) {
        return new NextBuildNumberBuildWrapper(nextBuildNumber);
    }
}
