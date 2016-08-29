/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 HyperHQ Inc
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

package sh.hyper.hyperslaves.spec;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import sh.hyper.hyperslaves.spi.ContainerDriver;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class ImageIdContainerDefinition extends ContainerDefinition {

    private final String image;

    private final String size;

    private final boolean forcePull;

    @DataBoundConstructor
    public ImageIdContainerDefinition(String image, String size, boolean forcePull) {
        this.image = image;
        this.size = size;
        this.forcePull = forcePull;
    }

    @Override
    public String getSize() {
        return size;
    }

    public String getImage() {
        return image;
    }


    @Override
    public String getImage(ContainerDriver driver, TaskListener listener) throws IOException, InterruptedException {

        boolean pull = forcePull;
        final Launcher launcher = new Launcher.LocalLauncher(listener);
        boolean result = driver.checkImageExists(launcher, image);

        if (!result) {
            // Could be a hyper failure, but most probably image isn't available
            pull = true;
        }

        if (pull) {
            listener.getLogger().println("Pulling docker image " + image);
            driver.pullImage(launcher, image);
        }

        return image;
    }

    @Extension(ordinal = 99)
    public static class DescriptorImpl extends Descriptor<ContainerDefinition> {

        @Override
        public String getDisplayName() {
            return "Docker image";
        }
    }
}