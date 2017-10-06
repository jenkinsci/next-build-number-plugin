/*
 * The MIT License
 *
 * Copyright (c) 2009, Yahoo!, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletException;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.security.Permission;
import jenkins.branch.MultiBranchProject;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author dty
 */
public class NextBuildNumberAction implements Action {
    private final Job job;

    NextBuildNumberAction(Job job) {
        this.job = job;
    }

    public String getIconFileName() {
        if (hasPermission(job)) {
            return "next.png";
        }

        return null;
    }

    public boolean hasPermission(Job job) {
        if (job.getParent() instanceof jenkins.branch.MultiBranchProject) {
            return ((MultiBranchProject) job.getParent()).getACL().hasPermission(getPermission());
        } else {
            return job.getACL().hasPermission(getPermission());
        }
    }

    private void checkPermission(Job job) {
        if (job.getParent() instanceof jenkins.branch.MultiBranchProject) {
            ((MultiBranchProject) job.getParent()).getACL().checkPermission(getPermission());
        } else {
            job.getACL().checkPermission(getPermission());
        }
    }

    public String getDisplayName() {
        return Messages.DisplayName();
    }

    public String getUrlName() {
        return "nextbuildnumber";
    }

    public Job getJob() {
        return job;
    }

    public Permission getPermission() {
        return Job.CONFIGURE;
    }

    public synchronized void doSubmit( StaplerRequest req, StaplerResponse resp ) throws IOException, ServletException {
        checkPermission(job);

        try {
            int buildNumber = Integer.parseInt( req.getParameter("nextBuildNumber"));
            job.updateNextBuildNumber( buildNumber );
            resp.sendRedirect2( job.getAbsoluteUrl() );
        }
        catch ( NumberFormatException e ) {
            throw new ServletException( "Build number must be an integer", e );
        }
    }

    @Extension
    public static class ActionInjector extends TransientActionFactory<Job> {
        @Override
        public Collection<NextBuildNumberAction> createFor(Job p) {
            ArrayList<NextBuildNumberAction> list = new ArrayList<NextBuildNumberAction>();

            list.add( new NextBuildNumberAction(p));

            return list;
        }
        @Override
        public Class type() {
            return Job.class;
        }
    }
}
