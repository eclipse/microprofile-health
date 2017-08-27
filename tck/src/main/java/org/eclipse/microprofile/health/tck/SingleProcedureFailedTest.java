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

import org.eclipse.microprofile.health.tck.deployment.FailedCheck;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;
import static org.eclipse.microprofile.health.tck.JsonUtils.asJsonObject;
import static org.eclipse.microprofile.health.tck.TCKConfiguration.getHealthURL;

/**
 * @author Heiko Braun
 */
public class SingleProcedureFailedTest extends SimpleHttp {

    @Deployment
    public static Archive getDeployment() throws Exception {
        return createWarFileWithClasses(FailedCheck.class);
    }

    /**
     * Verifies the health integration with CDI at the scope of a server runtime
     */
    @Test
    @RunAsClient
    public void testFailureResponsePayload() throws Exception {
        Response response = getUrlContents(getHealthURL());

        // status code
        Assert.assertEquals(response.getStatus(), 503);

        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().get()));
        JsonObject json = jsonReader.readObject();
        System.out.println(json);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(),1,"Expected a single check response");

        // single procedure response
        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("name"),
                "failed-check",
                "Expected a CDI health check to be invoked, but it was not present in the response"
        );

        Assert.assertEquals(
                asJsonObject(checks.get(0)).getString("state"),
                "DOWN",
                "Expected a successful check result"
        );

        // overall outcome
        Assert.assertEquals(
                json.getString("outcome"),
                "DOWN",
                "Expected overall outcome to be unsuccessful"
        );
    }
}

