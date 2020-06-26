/*
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulLiveness;
import org.eclipse.microprofile.health.tck.deployment.SuccessfulReadiness;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.json.JsonObject;
import java.io.IOException;

import static org.eclipse.microprofile.health.tck.DeploymentUtils.createWarFileWithClasses;

/**
 * @author Martin Stefanko
 */
public class JsonSchemaValidationTest extends TCKBase {

    @Deployment
    public static Archive getDeployment() {
        return createWarFileWithClasses(JsonSchemaValidationTest.class.getSimpleName(),
            SuccessfulLiveness.class, SuccessfulReadiness.class);
    }

    /**
     * Verifies that the JSON object returned by the implementation is following the 
     * JSON schema defined by the specification
     */
    @Test
    @RunAsClient
    public void testPayloadJsonVerifiesWithTheSpecificationSchema() throws Exception {
        Response response = getUrlHealthContents();

        Assert.assertEquals(response.getStatus(), 200);

        JsonObject json = readJson(response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaJson = mapper.readTree(Thread.currentThread()
            .getContextClassLoader().getResourceAsStream("health-check-schema.json"));

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(schemaJson);

        ProcessingReport report = schema.validate(toJsonNode(json));
        Assert.assertTrue(report.isSuccess(), "Returned Health JSON does not validate against the specification schema");
    }
    
    private JsonNode toJsonNode(JsonObject jsonObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonObject.toString());
    }
}

