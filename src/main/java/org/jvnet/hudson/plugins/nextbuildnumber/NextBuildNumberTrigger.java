package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import hudson.tasks.BuildWrapper;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set the nextBuildNumber when the trigger is first started
 *
 */
public class NextBuildNumberTrigger extends Trigger {
    private static final Logger LOG = Logger.getLogger(NextBuildNumberTrigger.class.getName());

    private final int nextBuildNumber;

    public NextBuildNumberTrigger(int nextBuildNumber) {
        this.nextBuildNumber = nextBuildNumber;
    }

    @Override
    public void start(Item project, boolean newInstance) {
        if (!newInstance) {
            return; //this trigger only makes sense in the context of a Job DSL run, beyond that it is useless.
        }
        if (project instanceof Job) {
            LOG.fine(String.format("%s: Setting nextBuildNumber=%d for project %s", getClass().getSimpleName(), nextBuildNumber, project.getFullDisplayName()));
            try {
                ((Job) project).updateNextBuildNumber(nextBuildNumber);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Error setting next build number for build " + project.getFullDisplayName(), e);
            }
        }
        super.start(project, newInstance);
    }

    /**
     * Registers {@link NextBuildNumberTrigger} as a {@link Trigger}.
     */
    @Extension
    public static final class DescriptorImpl extends TriggerDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.DisplayName();
        }

        @Override
        public boolean isApplicable(Item item) {
            return item instanceof Job;
        }

    }
}
