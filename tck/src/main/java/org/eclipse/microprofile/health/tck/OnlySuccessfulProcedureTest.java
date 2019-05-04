/*
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.microprofile.health.tck.deployment.SuccessfulHealth;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulLiveness;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulReadiness;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;
import static org.eclipse.microprofile.health.tck.JsonUtils.asJsonObject;

/**
 * @author Antoine Sabot-Durand
 */
public class OnlySuccessfulProcedureTest extends SimpleHttp {

    @Deployment
    public static Archive getDeployment() throws Exception {
        return createWarFileWithClasses(SuccessfulReadiness.class,
                                        SuccessfulLiveness.class,
                                        SuccessfulHealth.class);
    }

    /**
     * Verifies the health integration with CDI at the scope of a server runtime
     */
    @Test
    @RunAsClient
    public void testSuccessfulLivenessResponsePayload() throws Exception {
        Response response = getUrlLiveContents();

        // status code
        Assert.assertEquals(response.getStatus(), 200);

        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().get()));
        JsonObject json = jsonReader.readObject();
        System.out.println(json);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(),1,"Expected a single check response");

        // single procedure response
        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("name"),
                "successful-check",
                "Expected a CDI Liveness health check to be invoked, but it was not present in the response"
        );

        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("status"),
                "UP",
                "Expected a successful check result"
        );

        // overall outcome
        Assert.assertEquals(
                json.getString("status"),
                "UP",
                "Expected overall status to be unsuccessful"
        );
    }


    @Test
    @RunAsClient
    public void testSuccessfulReadinessResponsePayload() throws Exception {
        Response response = getUrlReadyContents();

        // status code
        Assert.assertEquals(response.getStatus(), 200);

        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().get()));
        JsonObject json = jsonReader.readObject();
        System.out.println(json);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(),1,"Expected a single check response");

        // single procedure response
        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("name"),
                "successful-check",
                "Expected a CDI Readiness health check to be invoked, but it was not present in the response"
        );

        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("status"),
                "UP",
                "Expected a successful check result"
        );

        // overall outcome
        Assert.assertEquals(
                json.getString("status"),
                "UP",
                "Expected overall status to be unsuccessful"
        );
    }
}

