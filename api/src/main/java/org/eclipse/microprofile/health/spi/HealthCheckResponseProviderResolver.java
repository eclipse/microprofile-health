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

package org.eclipse.microprofile.health.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

/**
 * Created by hbraun on 07.07.17.
 */
public abstract class HealthCheckResponseProviderResolver {
    private static volatile HealthCheckResponseProviderResolver instance = null;
    private static final Logger LOGGER = Logger.getLogger(HealthCheckResponseProviderResolver.class.getName());

    /**
     * Set the SPIFactory instance. It is used by OSGi environment while service loader
     * pattern is not supported.
     *
     * @param instance the instance instance to use.
     */
    public static void setFactory(HealthCheckResponseProviderResolver instance) {
        HealthCheckResponseProviderResolver.instance = instance;
    }

    public static HealthCheckResponseProviderResolver instance() {
        if (instance == null) {
            synchronized (HealthCheckResponseProviderResolver.class) {
                if (instance != null) {
                    return instance;
                }

                HealthCheckResponseProviderResolver newInstance = find(HealthCheckResponseProviderResolver.class);

                if (newInstance == null) {
                    throw new IllegalStateException("No ResponseBuilderProvider implementation found!");
                }

                instance = newInstance;
            }
        }
        return instance;
    }

    protected HealthCheckResponseProviderResolver() {
    }

    public abstract HealthCheckResponseBuilder createResponseBuilder();

    private static <T> T find(Class<T> service) {

        T serviceInstance = find(service, HealthCheckResponseProviderResolver.getContextClassLoader());

        // alternate classloader
        if(null==serviceInstance) {
            serviceInstance = find(service, HealthCheckResponseProviderResolver.class.getClassLoader());
        }

        // service cannot be found
        if(null==serviceInstance) {
            throw new IllegalStateException("Unable to find service " + service.getName());
        }

        return serviceInstance;
    }

    private static <T> T find(Class<T> service, ClassLoader cl) {

        T serviceInstance = null;

        try {
            ServiceLoader<T> services = ServiceLoader.load(service, cl);

            for (T spi : services) {
                if (serviceInstance != null) {
                    throw new IllegalStateException(
                            "Multiple service implementations found: "
                                    + spi.getClass().getName() + " and "
                                    + serviceInstance.getClass().getName());
                }
                serviceInstance = spi;
            }
        }
        catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Error loading service " + service.getName() + ".", t);
        }

        return serviceInstance;
    }



    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread().getContextClassLoader();
            }
            catch (SecurityException ex) {
                LOGGER.log(
                        Level.WARNING,
                        "Unable to get context classloader instance.",
                        ex);
            }
            return cl;
        });
    }
}
