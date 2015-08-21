package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.Plugin;

import java.util.logging.Logger;

/**
 * Created by akom on 8/20/15.
 */
public class PluginImpl extends Plugin {
    private final static Logger LOG = Logger.getLogger(PluginImpl.class.getName());

    public void start() throws Exception {
        LOG.info("NextBuildNumber: I'm awake");
    }
}
