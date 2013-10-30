/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.rule.Version;
import org.ocpsoft.rewrite.servlet.config.rule.VersionProvider;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Provider for ResourceVersioningTest.
 * 
 * @author Christian Kaltepoth
 */
public class ResourceVersioningProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(ServletContext context)
   {

      return ConfigurationBuilder.begin()

               // resource versioning using the context version
               .addRule(Version.path("/version/{filename}.txt")
                        .versionProvider(VersionProvider.contextVersion()))

               // resource versioning using the fixed version
               .addRule(Version.path("/fixed/{filename}.txt")
                        .versionProvider(VersionProvider.fixedVersion("v1")))

               // resource versioning using the fixed version but pattern without file extension
               .addRule(Version.path("/extensionless/{anything}")
                        .versionProvider(VersionProvider.fixedVersion("v5")))

               // rule for testing that outbound URLs are correctly rewritten
               .addRule()
               .when(Direction.isInbound().and(Path.matches("/outbound")))
               .perform(new HttpOperation() {
                  @Override
                  public void performHttp(HttpServletRewrite event, EvaluationContext context)
                  {

                     String response = new StringBuilder()
                              .append("version = ")
                              .append(event.getResponse().encodeURL("/version/test.txt"))
                              .append("\n")
                              .append("fixed = ")
                              .append(event.getResponse().encodeURL("/fixed/test.txt"))
                              .append("\n")
                              .append("extensionless = ")
                              .append(event.getResponse().encodeURL("/extensionless/somefile"))
                              .append("\n")
                              .toString();

                     SendStatus.code(200).perform(event, context);
                     Response.write(response).perform(event, context);

                  }
               });

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
