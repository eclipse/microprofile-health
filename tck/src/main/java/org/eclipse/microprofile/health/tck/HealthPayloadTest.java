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

import org.eclipse.microprofile.health.tck.deployment.ApplicationConfig;
import org.eclipse.microprofile.health.tck.deployment.CDIHealthCheck;
import org.eclipse.microprofile.health.tck.deployment.FailedChecks;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Heiko Braun
 */
@RunWith(Arquillian.class)
public class HealthPayloadTest extends SimpleHttp {

    @Deployment
    public static Archive getDeployment() throws Exception {
        WebArchive deployment = ShrinkWrap.create(WebArchive.class, "myapp.war");
        deployment.addClass(SimpleHttp.class);
        deployment.addClass(ApplicationConfig.class);
        deployment.addClass(FailedChecks.class);
        deployment.addClass(CDIHealthCheck.class);
        return deployment;
    }

    /**
     * Verifies the health integration with CDI at the scope of a server runtime
     */
    @Test
    @RunAsClient
    @Ignore //for now
    public void testGlobalScopeCDIIntegration() throws Exception {
        Response response = getUrlContents("http://localhost:8080/health");
        String responsePayload = response.getBody().get();
        Assert.assertTrue(
                "Expected a CDI health check to be invoked, but it was not present in the response",
                responsePayload.contains("CDI")
        );
    }

    /**
     * Verifies the health integration with CDI at the scope of a single deployment
     */
    @Test
    @RunAsClient
    public void testLocalScopeCDIIntegration() throws Exception {
        Response response = getUrlContents("http://localhost:8080/tck/health");
        Assert.assertEquals(200, response.getStatus());

        String responsePayload = response.getBody().get();
        System.out.println(responsePayload);
        Assert.assertTrue(
                "Expected a CDI health check to be invoked, but it was not present in the response",
                responsePayload.contains("CDI")
        );
    }

    /**
     * Verifies the health integration with JAX-RS at the scope of a server runtime
     */
    @Test
    @RunAsClient
    public void testGlobalScopeJAXRSIntegration() throws Exception {
        // aggregator
        Response response = getUrlContents("http://localhost:8080/health");

        Assert.assertTrue("Response payload is missing", response.getBody().isPresent());
        System.out.println(response.getBody().get());

        String responsePayload = response.getBody().get();

        Assert.assertTrue(
                responsePayload.contains("first") &&
                        responsePayload.contains("second")
        );

        // aggregator / failed
        response = getUrlContents("http://localhost:8080/health", true);
        Assert.assertEquals("Expected 503", 503, response.getStatus());
        Assert.assertTrue("Response payload is missing", response.getBody().isPresent());
        responsePayload = response.getBody().get();
        Assert.assertTrue(responsePayload.contains("first") && responsePayload.contains("UP"));
    }

    /**
     * Verifies the health integration with JAX-RS at the scope of a single deployment
     */
    @Test
    @RunAsClient
    public void testLocalScopeJAXRSIntegration() throws Exception {

        // aggregator / with auth
        Response response = getUrlContents("http://localhost:8080/health");

        Assert.assertTrue("Response payload is missing", response.getBody().isPresent());
        System.out.println(response.getBody().get());

        String responsePayload = response.getBody().get();

        Assert.assertTrue(
                responsePayload.contains("first") &&
                    responsePayload.contains("second")
        );

        // direct / failure
        response = getUrlContents("http://localhost:8080/tck/failed/first", false);
        Assert.assertEquals("Expected 503", 503, response.getStatus());

        // direct / success
        response = getUrlContents("http://localhost:8080/tck/failed/second", false);
        Assert.assertEquals("Expected 200", 200, response.getStatus());

    }
}

