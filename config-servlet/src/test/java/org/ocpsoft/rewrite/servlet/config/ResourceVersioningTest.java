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

import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

@RunWith(Arquillian.class)
public class ResourceVersioningTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addClass(ResourceVersioningProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, ResourceVersioningProvider.class)
               .addAsWebResource(new StringAsset("foobar"), "version/test.txt")
               .addAsWebResource(new StringAsset("foobar"), "fixed/test.txt")
               .addAsWebResource(new StringAsset("foobar"), "extensionless/somefile");
   }

   @Test
   public void contextOriginalUrlStillWorks() throws Exception
   {
      HttpAction<HttpGet> action = get("/version/test.txt");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void contextVersionedUrlIsForwarded() throws Exception
   {
      HttpAction<HttpGet> action = get("/version/test-0020f54d.txt");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void contextAppendedVersionDoesntWork() throws Exception
   {
      HttpAction<HttpGet> action = get("/version/test.txt-0020f54d");
      assertThat(action.getStatusCode(), Matchers.equalTo(404));
   }

   @Test
   public void contextVersionAddedOutbound() throws Exception
   {
      HttpAction<HttpGet> action = get("/outbound");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(),
               containsPattern("version = /version/test-[a-z0-9]+\\.txt"));
   }

   @Test
   public void fixedOriginalUrlStillWorks() throws Exception
   {
      HttpAction<HttpGet> action = get("/fixed/test.txt");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void fixedVersionedUrlIsForwarded() throws Exception
   {
      HttpAction<HttpGet> action = get("/fixed/test-v1.txt");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void fixedAppendedVersionDoesntWork() throws Exception
   {
      HttpAction<HttpGet> action = get("/fixed/test.txt-v1");
      assertThat(action.getStatusCode(), Matchers.equalTo(404));
   }

   @Test
   public void fixedVersionAddedOutbound() throws Exception
   {
      HttpAction<HttpGet> action = get("/outbound");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(),
               Matchers.containsString("fixed = /fixed/test-v1.txt"));
   }

   @Test
   public void extensionlessOriginalUrlStillWorks() throws Exception
   {
      HttpAction<HttpGet> action = get("/extensionless/somefile");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void extensionlessVersionedUrlIsForwarded() throws Exception
   {
      HttpAction<HttpGet> action = get("/extensionless/somefile-v5");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(), Matchers.containsString("foobar"));
   }

   @Test
   public void extensionlessVersionAddedOutbound() throws Exception
   {
      HttpAction<HttpGet> action = get("/outbound");
      assertThat(action.getStatusCode(), Matchers.equalTo(200));
      assertThat(action.getResponseContent(),
               Matchers.containsString("extensionless = /extensionless/somefile-v5"));
   }

   /**
    * @return
    */
   private Matcher<String> containsPattern(final String pattern)
   {
      return new BaseMatcher<String>() {

         @Override
         public boolean matches(Object obj)
         {
            return Pattern.compile(pattern).matcher(obj.toString()).find();
         }

         @Override
         public void describeTo(Description desc)
         {
            desc.appendValue(pattern);
         }

      };
   }

}
