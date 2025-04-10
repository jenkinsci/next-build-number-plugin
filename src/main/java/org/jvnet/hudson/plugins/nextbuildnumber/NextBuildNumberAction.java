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
import java.util.Collection;
import java.util.Collections;

import jakarta.servlet.ServletException;

import com.cloudbees.hudson.plugins.folder.computed.ComputedFolder;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.security.Permission;
import jenkins.branch.MultiBranchProject;
import jenkins.model.TransientActionFactory;
import net.sf.json.JSONException;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 *
 * @author dty
 */
@SuppressWarnings("rawtypes")
public class NextBuildNumberAction implements Action {
    private final Job job;

    NextBuildNumberAction(Job job) {
        this.job = job;
    }

    @Override
    public String getIconFileName() {
        if (hasPermission(job)) {
            return "symbol-arrow-forward-outline plugin-ionicons-api";
        }
        return null;
    }

    public boolean hasPermission(Job job) {
        if (job.getParent() instanceof MultiBranchProject) {
            MultiBranchProject parentProject = (MultiBranchProject) job.getParent();
            boolean hasPermission = parentProject.getACL().hasPermission(getPermission());
            if (!hasPermission && parentProject.getParent() instanceof ComputedFolder) {
                hasPermission = ((ComputedFolder) parentProject.getParent()).getACL().hasPermission(getPermission());
            }
            return hasPermission;
        } else {
            return job.getACL().hasPermission(getPermission());
        }
    }

    private void checkPermission(Job job) {
        if (job.getParent() instanceof MultiBranchProject) {
            MultiBranchProject parentProject = (MultiBranchProject) job.getParent();
            try {
                parentProject.getACL().checkPermission(getPermission());
            } catch (org.springframework.security.access.AccessDeniedException e) {
                if (parentProject.getParent() instanceof ComputedFolder) {
                    ((ComputedFolder) parentProject.getParent()).getACL().checkPermission(getPermission());
                } else {
                    throw e;
                }
            }
        } else {
            job.getACL().checkPermission(getPermission());
        }
    }

    public String getDisplayName() {
        return Messages.displayName();
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

    public synchronized void doSubmit( StaplerRequest2 req, StaplerResponse2 resp ) throws IOException, ServletException {
        checkPermission(job);
        try {
            int buildNumber = req.getSubmittedForm().getInt("nextBuildNumber");
            job.updateNextBuildNumber( buildNumber );
            resp.sendRedirect2( job.getAbsoluteUrl() );
        }
        catch (JSONException e) {
            throw new ServletException( "Build number must be an integer", e );
        }
    }

    @Extension
    public static class ActionInjector extends TransientActionFactory<Job> {
        @Override
        public Collection<NextBuildNumberAction> createFor(Job p) {
            return Collections.singleton(new NextBuildNumberAction(p));
        }
        @Override
        public Class<Job> type() {
            return Job.class;
        }
    }
}
