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
package org.ocpsoft.rewrite.servlet.config.rule;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;

/**
 * @author Christian Kaltepoth
 */
public class Version implements Rule, Parameterized
{

   private final static String PARAMETER_NAME = "version";
   private final static String PARAMETER_EXPR = "{" + PARAMETER_NAME + "}";

   private Path requestPath;
   private String requestPattern;

   private Path resourcePath;
   private String resourcePattern;
   private ParameterStore store;

   private VersionProvider keyProvider = VersionProvider.contextVersion();

   public static Version path(String path)
   {
      return new Version(path);
   }

   private Version(String pattern)
   {

      this.resourcePattern = pattern;
      this.resourcePath = Path.matches(pattern);

      versionPattern(autoCreateVersionPattern(pattern));

   }

   public Version versionPattern(String pattern)
   {
      this.requestPattern = pattern;
      this.requestPath = Path.matches(pattern);
      return this;
   }

   private String autoCreateVersionPattern(String pattern)
   {
      Matcher matcher = Pattern.compile("^(.*)(\\.\\w+)$").matcher(pattern);
      if (matcher.matches()) {
         return matcher.group(1) + "-" + PARAMETER_EXPR + matcher.group(2);
      }
      return pattern + "-" + PARAMETER_EXPR;
   }

   public Version versionProvider(VersionProvider keyProvider)
   {
      this.keyProvider = keyProvider;
      return this;
   }

   @Override
   public boolean evaluate(Rewrite event, EvaluationContext context)
   {

      if (event instanceof HttpInboundServletRewrite) {
         if (requestPath.evaluate(event, context)) {
            return true;
         }
      }

      if (event instanceof HttpOutboundServletRewrite) {
         if (resourcePath.evaluate(event, context)) {
            return true;
         }
      }

      return false;

   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {

      if (event instanceof HttpInboundServletRewrite) {
         Forward forward = Forward.to(resourcePattern);
         forward.setParameterStore(store);
         forward.perform(event, context);
      }

      if (event instanceof HttpOutboundServletRewrite) {

         String version = keyProvider.getVersion(event, context);

         Evaluation.property(PARAMETER_NAME).submit(event, context, version);

         Substitute substitute = Substitute.with(requestPattern);
         substitute.setParameterStore(store);
         substitute.perform(event, context);

      }

   }

   @Override
   public String getId()
   {
      return null;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return requestPath.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      this.store = store;
      requestPath.setParameterStore(store);
      resourcePath.setParameterStore(store);
   }

}
