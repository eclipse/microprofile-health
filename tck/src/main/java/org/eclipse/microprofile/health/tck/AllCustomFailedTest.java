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

import javax.json.JsonArray;
import javax.json.JsonObject;


import org.eclipse.microprofile.health.tck.deployment.FailedCustom;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulCustom;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;

/**
 * @author Prashanth Gunapalasingam
 */
public class AllCustomFailedTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return createWarFileWithClasses(AllCustomFailedTest.class.getSimpleName(),
                                        FailedCustom.class, SuccessfulCustom.class);
    }

    /**
     * Verifies the custom health integration with CDI at the scope of a server runtime, by retrieving all the custom checks.
     */
    @Test
    @RunAsClient
    public void testFailureResponsePayload() {
        Response response = getUrlAllCustomHealthContents();

        // status code
        Assert.assertEquals(response.getStatus(),503);

        JsonObject json = readJson(response);

        // response size
        JsonArray checks = json.getJsonArray("checks");
        Assert.assertEquals(checks.size(), 2, "Expected two check responses");
        
        for (JsonObject check : checks.getValuesAs(JsonObject.class)) {
            String id = check.getString("name");
            switch (id) {
                case "successful-check":
                    verifySuccessStatus(check);
                    break;
                case "failed-check":
                    verifyFailureStatus(check);
                    break;
                default:
                   Assert.fail("Unexpected response payload structure");
            }
        }

        assertOverallFailure(json);
    }

}

