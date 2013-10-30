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

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * 
 * 
 * 
 * @author Christian Kaltepoth
 */
public abstract class VersionProvider
{

   public abstract String getVersion(Rewrite event, EvaluationContext context);

   
   public static VersionProvider contextVersion()
   {
      return new VersionProvider() {
         @Override
         public String getVersion(Rewrite event, EvaluationContext context)
         {
            if (event instanceof HttpServletRewrite) {
               ServletContext servletContext = ((HttpServletRewrite) event).getServletContext();
               return ServletContextVersionListener.getContextVersion(servletContext);
            }
            throw new IllegalArgumentException("Rewrite event is not a HttpServletRewrite");
         }
      };
   }

   public static VersionProvider currentTime()
   {
      return new VersionProvider() {
         @Override
         public String getVersion(Rewrite event, EvaluationContext context)
         {
            return String.valueOf(System.currentTimeMillis());
         }
      };
   }

   public static VersionProvider fixedVersion(final String version)
   {
      return new VersionProvider() {
         @Override
         public String getVersion(Rewrite event, EvaluationContext context)
         {
            return version;
         }
      };
   }

}
