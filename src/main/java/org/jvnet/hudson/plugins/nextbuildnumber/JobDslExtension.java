package org.jvnet.hudson.plugins.nextbuildnumber;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;

import java.io.IOException;

@Extension(optional = true)
public class JobDslExtension extends ContextExtensionPoint {
  @DslExtensionMethod(context = PropertiesContext.class)
  public Object nextBuildNumber(int number, DslEnvironment dslEnvironment) {
    dslEnvironment.put("nextBuildNumber", number);
    return null;
  }

  @Override
  public void notifyItemCreated(Item item, DslEnvironment dslEnvironment) {
    notifyItemUpdated(item, dslEnvironment);
  }

  @Override
  public void notifyItemUpdated(Item item, DslEnvironment dslEnvironment) {
    if (item instanceof Job && dslEnvironment.get("nextBuildNumber") instanceof Integer) {
      try {
        ((Job) item).updateNextBuildNumber((Integer) dslEnvironment.get("nextBuildNumber"));
      } catch (IOException e) {
        throw new RuntimeException("could not update build number for " + item.getFullName(), e);
      }
    }
  }
}
