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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.servlet.config.rule.ServletContextVersionListener;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * Test for {@link ServletContextVersionListener}. This test verifies, that the listener creates a unique resource
 * version identifier which can be accessed using
 * {@link ServletContextVersionListener#getContextVersion(ServletContext)}.
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class ServletContextVersionListenerTest
{

   @Deployment
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addClass(ServletContextHolder.class);
   }

   @Test
   public void testResourceVersionFromServletContext()
   {

      ServletContext context = ServletContextHolder.getServletContext();
      assertNotNull("Could not find servlet context", context);

      String version = ServletContextVersionListener.getContextVersion(context);
      assertNotNull("No context version found", version);
      assertTrue("Key not matching regex", version.matches("[a-zA-Z0-9]+"));

   }

}
