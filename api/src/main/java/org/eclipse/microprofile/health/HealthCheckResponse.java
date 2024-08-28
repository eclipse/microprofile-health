/*
 * Copyright (c) 2017-2024 Contributors to the Eclipse Foundation
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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;

/**
 * The response to a health check invocation.
 * <p>
 * The {@link HealthCheckResponse} class is reserved for an extension by implementation providers. An application should
 * use one of the static methods to create a Response instance using a {@link HealthCheckResponseBuilder}. When used on
 * the consuming end, The class can also be instantiated directly.
 * </p>
 */
public class HealthCheckResponse {

    private static final Logger LOGGER = Logger.getLogger(HealthCheckResponse.class.getName());

    private static volatile HealthCheckResponseProvider provider = null;

    private final String name;

    private final Status status;

    private final Map<String, Object> data;

    /**
     * Constructor allowing instantiation from 3rd party framework like MicroProfile Rest client
     * <p>
     * This is deprecated since MicroProfile Health 4.1, and will be removed in the next major version.
     * {@link HealthCheckResponse#HealthCheckResponse(String, Status, Map)} should be used from now on.
     *
     * @param name
     *            Health Check procedure's name
     * @param status
     *            Health Check procedure's status
     * @param data
     *            additional data for Health Check procedure
     */
    @Deprecated(since = "4.1", forRemoval = true)
    public HealthCheckResponse(String name, Status status, Optional<Map<String, Object>> data) {
        this.name = name;
        this.status = status;
        this.data = data.orElse(null);
    }

    /**
     * Constructor allowing instantiation from 3rd party framework like MicroProfile Rest client
     *
     * @param name
     *            Health Check procedure's name
     * @param status
     *            Health Check procedure's status
     * @param data
     *            additional data for Health Check procedure
     */
    public HealthCheckResponse(String name, Status status, Map<String, Object> data) {
        this.name = name;
        this.status = status;
        this.data = data;
    }

    /**
     * Default constructor
     */
    public HealthCheckResponse() {
        this.name = null;
        this.status = null;
        this.data = null;
    }

    /**
     * Used by OSGi environment where the service loader pattern is not supported.
     *
     * @param provider
     *            the provider instance to use.
     */
    public static void setResponseProvider(HealthCheckResponseProvider provider) {
        HealthCheckResponse.provider = provider;
    }

    /**
     * Creates a {@link HealthCheckResponseBuilder} with a name.
     *
     * @param name
     *            the check name
     * @return a new health check builder with a name
     */
    public static HealthCheckResponseBuilder named(String name) {

        return getProvider().createResponseBuilder().name(name);
    }

    /**
     * Creates an empty {@link HealthCheckResponseBuilder}.
     *
     * <b>Note:</b> The health check response name is required and needs to be set before the response is constructed.
     *
     * @return a new, empty health check builder
     */
    public static HealthCheckResponseBuilder builder() {
        return getProvider().createResponseBuilder();
    }

    /**
     * Creates a successful health check with a name.
     *
     * @param name
     *            the check name
     * @return a new sucessful health check response with a name
     */
    public static HealthCheckResponse up(String name) {
        return HealthCheckResponse.named(name).up().build();
    }

    /**
     * Creates a failed health check with a name.
     *
     * @param name
     *            the check name
     * @return a new failed health check response with a name
     */
    public static HealthCheckResponse down(String name) {
        return HealthCheckResponse.named(name).down().build();
    }

    private static HealthCheckResponseProvider getProvider() {
        if (provider == null) {
            synchronized (HealthCheckResponse.class) {
                if (provider != null) {
                    return provider;
                }

                HealthCheckResponseProvider newInstance = find(HealthCheckResponseProvider.class);

                if (newInstance == null) {
                    throw new IllegalStateException("No HealthCheckResponseProvider implementation found!");
                }

                provider = newInstance;
            }
        }
        return provider;
    }

    // the actual contract

    public enum Status {
        UP, DOWN
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Access the map which stores the Health check response data.
     * <p>
     * This is deprecated since MicroProfile Health 4.1, and will be replaced by
     * {@code public Map<String, Object> getData()} in the next major version.
     *
     * @return An {@link Optional} instance to contain the Health check response data.
     */
    @Deprecated(since = "4.1")
    public Optional<Map<String, Object>> getData() {
        return Optional.ofNullable(data);
    }

    public Optional<Object> getData(String key) {
        return Optional.of(data.get(key));
    }

    private static <T> T find(Class<T> service) {

        T serviceInstance = find(service, HealthCheckResponse.getContextClassLoader());

        // alternate classloader
        if (null == serviceInstance) {
            serviceInstance = find(service, HealthCheckResponse.class.getClassLoader());
        }

        // service cannot be found
        if (null == serviceInstance) {
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
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Error loading service " + service.getName() + ".", t);
        }

        return serviceInstance;
    }

    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException ex) {
                LOGGER.log(
                        Level.WARNING,
                        "Unable to get context classloader instance.",
                        ex);
            }
            return cl;
        });
    }
}
