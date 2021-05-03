/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulLiveness;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Prashanth Gunapalasingam
 */
public class HealthCheckResponseValidationTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return createWarFileWithClasses(HealthCheckResponseValidationTest.class.getSimpleName(),
                SuccessfulLiveness.class);
    }

    /**
     * Validates the HealthCheckResponse concrete class definition by verifying if its deserialized properties correctly
     * maps to the JSON schema protocol defined by the specification and the JSON health check response returned by the
     * implementation.
     */
    @Test
    @RunAsClient
    public void testValidateConcreteHealthCheckResponse() throws Exception {
        Response response = getUrlHealthContents();

        Assert.assertEquals(response.getStatus(), 200);

        JsonObject json = readJson(response);
        JsonArray checks = json.getJsonArray("checks");

        // Single check procedure
        JsonObject check = checks.getJsonObject(0);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        HealthCheckResponse hcr = mapper.readValue(check.toString(), HealthCheckResponse.class);

        // Validates the name property from the HealthCheckResponse class
        Assert.assertEquals(
                hcr.getName(),
                "successful-check",
                String.format("Unexpected value for the HealthCheckResponse \"name\" property : %s", hcr.getName()));

        // Validates the status property from the HealthCheckResponse class
        Assert.assertEquals(hcr.getStatus(), HealthCheckResponse.Status.UP,
                "Expected a successful check status for the HealthCheckResponse \"status\" property.");
    }
}
