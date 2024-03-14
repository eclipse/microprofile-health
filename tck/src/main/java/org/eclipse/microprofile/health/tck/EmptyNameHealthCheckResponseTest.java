/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import org.eclipse.microprofile.health.tck.deployment.EmptyNameCheck;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

/**
 * @author Fabio Burzigotti
 */
public class EmptyNameHealthCheckResponseTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return DeploymentUtils.createWarFileWithClasses(EmptyNameHealthCheckResponseTest.class.getSimpleName(),
                EmptyNameCheck.class, TCKBase.class);
    }

    /**
     * Verifies that defining a HealthCheckResponse with an empty name will cause the runtime to throw a
     * {@link jakarta.enterprise.inject.spi.DeploymentException}
     */
    @Test
    @RunAsClient
    public void testStartupFailsWithException() {
        Response response = getUrlStartedContents();

        // status code
        Assert.assertEquals(response.getStatus(), 503);

        JsonObject json = readJson(response);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(), 1, "Expected a single check response");

        assertOverallFailure(json);
    }
}
