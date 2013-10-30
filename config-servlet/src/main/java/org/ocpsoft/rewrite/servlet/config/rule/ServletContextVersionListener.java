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

import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.ocpsoft.rewrite.servlet.spi.ContextListener;

/**
 * Implementation of {@link ContextListener} which creates a random string during startup which can be used for resource
 * versioning.
 * 
 * @see Version
 * @see VersionProvider
 * 
 * @author Christian Kaltepoth
 */
public class ServletContextVersionListener implements ContextListener
{

   private static final String KEY = ServletContextVersionListener.class.getName() + ".VERSION_KEY";

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public void contextInitialized(ServletContextEvent event)
   {
      event.getServletContext().setAttribute(KEY, gernateVersion());
   }

   public static String getContextVersion(ServletContext context)
   {
      return (String) context.getAttribute(KEY);
   }

   protected String gernateVersion()
   {
      return UUID.randomUUID().toString().substring(0, 8);
   }

   @Override
   public void contextDestroyed(ServletContextEvent event)
   {
      // nothing
   }

}
