/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package org.eclipse.microprofile.health.tck;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Heiko Braun
 * @since 29/03/16
 */
public abstract class TCKBase extends Arquillian {

    private static final Logger LOG = Logger.getLogger(TCKBase.class.getName());

    @ArquillianResource
    private URI uri;

    @BeforeMethod
    public void beforeMethod(Method method) {
        LOG.info(String.format("Running test: %s#%s", method.getDeclaringClass().getSimpleName(), method.getName()));
    }
    
    Response getUrlHealthContents() {
        return getUrlContents(this.uri + "/health", false);
    }

    Response getUrlCustomHealthContents(String name) {
        return getUrlContents(this.uri + "/health/group/" + name , false);
    }

    Response getUrlLiveContents() {
        return getUrlContents(this.uri + "/health/live", false);
    }

    Response getUrlReadyContents() {
        return getUrlContents(this.uri + "/health/ready", false);
    }

    private Response getUrlContents(String theUrl, boolean useAuth) {
        return getUrlContents(theUrl, useAuth, true);
    }

    private Response getUrlContents(String theUrl, boolean useAuth, boolean followRedirects) {

        StringBuilder content = new StringBuilder();
        int code;

        try {

            HttpClientBuilder builder = HttpClientBuilder.create();
            if (!followRedirects) {
                builder.disableRedirectHandling();
            }

            if (useAuth) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "password");
                provider.setCredentials(AuthScope.ANY, credentials);
                builder.setDefaultCredentialsProvider(provider);
            }

            HttpClient client = builder.build();

            HttpResponse response = client.execute(new HttpGet(theUrl));
            code = response.getStatusLine().getStatusCode();

            if (response.getEntity() != null) {

                BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent())
                );

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
            }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(code, content.toString());
    }

    JsonObject readJson(Response response) {
        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().get()));
        JsonObject json = jsonReader.readObject();
        System.out.println(json);
        
        return json;
    }

    void assertSuccessfulCheck(JsonObject check, String expectedName) {
        assertCheckName(check, expectedName);
        verifySuccessStatus(check);
    }

    void assertFailureCheck(JsonObject check, String expectedName) {
        assertCheckName(check, expectedName);
        verifyFailureStatus(check);
    }

    private void assertCheckName(JsonObject check, String expectedName) {
        Assert.assertEquals(
            check.getString("name"),
            expectedName,
            String.format("Expected a CDI health check '%s' to be invoked, " +
                "but it was not present in the response", expectedName)
        );
    }

    void verifySuccessStatus(JsonObject check) {
        Assert.assertEquals(
            check.getString("status"),
            "UP",
            "Expected a successful check result"
        );

    }

    void verifyFailureStatus(JsonObject check) {
        Assert.assertEquals(
            check.getString("status"),
            "DOWN",
            "Expected a failed check result"
        );
    }

    void assertOverallSuccess(JsonObject json) {
        Assert.assertEquals(
            json.getString("status"),
            "UP",
            "Expected overall status to be successful"
        );
    }
    
    void assertOverallFailure(JsonObject json) {
        Assert.assertEquals(
            json.getString("status"),
            "DOWN",
            "Expected overall status to be unsuccessful"
        );
    }

    public class Response {
        public Response(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public int getStatus() {
            return status;
        }

        public Optional<String> getBody() {
            return (body!=null && !body.equals("")) ? Optional.of(body) : Optional.empty();
        }

        private int status;

        private String body;
    }
}
