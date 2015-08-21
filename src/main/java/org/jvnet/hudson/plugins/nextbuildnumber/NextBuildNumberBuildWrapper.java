package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by akom on 8/20/15.
 */
public class NextBuildNumberBuildWrapper extends BuildWrapper {

    private static final Logger LOG = Logger.getLogger(NextBuildNumberBuildWrapper.class.getName());

    private final int nextBuildNumber;

    public NextBuildNumberBuildWrapper(int nextBuildNumber) {
        this.nextBuildNumber = nextBuildNumber;
        LOG.warning(getClass().getName() + " instantiated with next build number = " + nextBuildNumber);
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        build.getProject().updateNextBuildNumber(nextBuildNumber);
        return super.setUp(build, launcher, listener);
    }


    public int getNextBuildNumber() {
        return nextBuildNumber;
    }


    /**
     * Registers {@link NextBuildNumberBuildWrapper} as a {@link BuildWrapper}.
     */
    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.DisplayName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
    }
}
