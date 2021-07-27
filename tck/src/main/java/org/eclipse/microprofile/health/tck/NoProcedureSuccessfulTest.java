/*
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createEmptyWarFile;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.json.JsonObject;

/**
 * Validates that a health check that includes no procedures returns a 200 status with no body.
 *
 * @author Scott Stark
 */
public class NoProcedureSuccessfulTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return createEmptyWarFile(NoProcedureSuccessfulTest.class.getSimpleName());
    }

    /**
     * Verifies the health integration with CDI at the scope of a server runtime
     */
    @Test
    @RunAsClient
    public void testSuccessResponsePayload() {
        Response response = getUrlHealthContents();

        // status code
        Assert.assertEquals(response.getStatus(), 200);

        JsonObject json = readJson(response);

        assertOverallSuccess(json);
        Assert.assertTrue(json.getJsonArray("checks").isEmpty(), "Expected empty checks array");

    }
}
