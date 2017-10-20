/*
 * The MIT License
 *
 * Copyright (c) 2013, Red Hat, Inc.
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

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import org.kohsuke.args4j.Argument;

/**
 * @author ogondza
 */
@Extension
public class NextBuildNumberCommand extends hudson.cli.CLICommand {

    /*package*/ static final int INVALID_NEXT_BUILD_NUMBER = -2;

    @Argument(index=0, required=true, metaVar="JOB", usage="Name of the job")
    private TopLevelItem item;

    @Argument(index=1, required=true, metaVar="BUILD_NUMBER", usage="Next build number")
    private int number;

    @Override
    public String getShortDescription() {

        return Messages.cliDescription();
    }

    @Override
    public String getName() {

        return "set-next-build-number";
    }

    @Override
    protected int run() throws IOException {

        item.checkPermission(Item.CONFIGURE);

        Job<?, ?> job = ((Job<?, ?>) item);

        job.updateNextBuildNumber(number);

        if (job.getNextBuildNumber() == number) return 0;

        stderr.println(Messages.invalidBuildNumber(
                Integer.toString(number),
                Integer.toString(job.getNextBuildNumber())
        ));
        return INVALID_NEXT_BUILD_NUMBER;
    }
}
