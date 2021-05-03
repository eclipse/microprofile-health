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

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.eclipse.microprofile.health.tck.deployment.DelegateHealth;
import org.eclipse.microprofile.health.tck.deployment.DelegationTarget;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Heiko Braun
 */
public class DelegateHealthSuccessfulTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return createWarFileWithClasses(DelegateHealthSuccessfulTest.class.getSimpleName(),
                DelegateHealth.class, DelegationTarget.class);
    }

    /**
     * Verifies CDI scoped beans can be used as delegate to resolve the system state
     */
    @Test
    @RunAsClient
    public void testSuccessfulDelegateInvocation() {
        Response response = getUrlHealthContents();

        // status code
        Assert.assertEquals(response.getStatus(), 200);

        JsonObject json = readJson(response);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(), 1, "Expected a single check response");

        // single procedure response
        assertSuccessfulCheck(checks.getJsonObject(0), "delegate-check");

        assertOverallSuccess(json);
    }
}
