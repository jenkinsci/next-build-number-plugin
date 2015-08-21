package org.jvnet.hudson.plugins.nextbuildnumber;


import hudson.Extension;
import hudson.model.*;
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext;
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides an extension for users of Job DSL plugin.
 * The following syntax will work in the DSL:
 *
 * triggers {
 *     nextBuildNumber(42)
 * }
 *
 * The reason this falls into the triggers context is that it has to occur before the build begins.
 */
@Extension(optional = true)
public class NextBuildNumberJobDslExtension extends ContextExtensionPoint {

    private static final Logger LOG = Logger.getLogger(NextBuildNumberJobDslExtension.class.getName());

    @DslExtensionMethod(context = TriggerContext.class)
    public Object nextBuildNumber(int nextBuildNumber) {
        LOG.fine(getClass().getSimpleName() + ": Saved next build number as " + nextBuildNumber);
        return new NextBuildNumberTrigger(nextBuildNumber);
    }
}
