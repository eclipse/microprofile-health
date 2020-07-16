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
package org.eclipse.microprofile.health;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Testing {@link HealthCheckResponseProvider} resolution
 */
public class HealthCheckResponseProviderResolverTest {

    private static final String SERVICE_PATH = System.getProperty("serviceDir");
    private static final String SERVICE_FILE_NAME = SERVICE_PATH + HealthCheckResponseProvider.class.getName();


     private static class ProviderResolverChild extends HealthCheckResponseProviderResolver {

         static void resetResolver() {
             reset();
         }
     }

    @BeforeMethod
    public void setUp() throws Exception {
        ProviderResolverChild.resetResolver();
        Files.deleteIfExists(FileSystems.getDefault().getPath(SERVICE_FILE_NAME));
    }

    private FileWriter getServiceFileWriter() throws IOException {
        File file = new File(SERVICE_FILE_NAME);
        file.getParentFile().mkdirs();
        return new FileWriter(file);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testWithoutServiceFile() throws Exception {
        HealthCheckResponse.builder();
    }


    @Test
    public void testWithOneGoodProvider() throws Exception {
        FileWriter fw = getServiceFileWriter();
        fw.write(DummyResponseProvider1.class.getName());
        fw.close();
        Assert.assertEquals(HealthCheckResponseProviderResolver.getProvider().getClass(),DummyResponseProvider1.class);
    }

    @Test
    public void testWithTwoGoodProvider() throws Exception {
        FileWriter fw = getServiceFileWriter();
        fw.write(DummyResponseProvider1.class.getName());
        fw.write('\n');
        fw.write(DummyResponseProvider2.class.getName());
        fw.close();
        Assert.assertTrue(HealthCheckResponseProviderResolver.getProvider().getClass().equals(DummyResponseProvider1.class) ||
                              HealthCheckResponseProviderResolver.getProvider().getClass().equals(DummyResponseProvider2.class));
    }

    @Test
    public void testPredicates() throws Exception {
        FileWriter fw = getServiceFileWriter();
        fw.write(DummyResponseProvider1.class.getName());
        fw.write('\n');
        fw.write(DummyResponseProvider2.class.getName());
        fw.close();
        HealthCheckResponseProviderResolver.setProviderPredicate( p -> p.getClass().equals(DummyResponseProvider1.class));
        Assert.assertEquals(HealthCheckResponseProviderResolver.getProvider().getClass(),DummyResponseProvider1.class);
        HealthCheckResponseProviderResolver.setProviderPredicate( p -> p.getClass().equals(DummyResponseProvider2.class));
        Assert.assertEquals(HealthCheckResponseProviderResolver.getProvider().getClass(),DummyResponseProvider2.class);
    }

}
