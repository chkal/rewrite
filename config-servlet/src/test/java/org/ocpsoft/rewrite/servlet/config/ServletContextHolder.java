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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This listener is used by ServletContextVersionListenerTest to access the {@link ServletContext} in the integration
 * tests.
 * 
 * @author Christian Kaltepoth
 */
@WebListener
public class ServletContextHolder implements ServletContextListener
{

   private final static Map<ClassLoader, ServletContext> store = new HashMap<ClassLoader, ServletContext>();

   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      store.put(getClassLoader(), sce.getServletContext());
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      store.remove(getClassLoader());
   }

   public static ServletContext getServletContext()
   {
      return store.get(getClassLoader());
   }

   private static ClassLoader getClassLoader()
   {
      return Thread.currentThread().getContextClassLoader();
   }

}
