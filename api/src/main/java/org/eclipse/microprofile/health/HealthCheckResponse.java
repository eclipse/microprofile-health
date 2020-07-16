/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * The response to a health check invocation.
 * <p>
 * The {@link HealthCheckResponse} class is reserved for an extension by implementation providers.
 * An application should use one of the static methods to create a Response instance using a
 * {@link HealthCheckResponseBuilder}.
 * When used on the consuming end, The class can also be instantiated directly.
 * </p>
 */
public class HealthCheckResponse {

    private static final Logger LOGGER = Logger.getLogger(HealthCheckResponse.class.getName());

    private final String name;

    private final Status status;

    private final Optional<Map<String, Object>> data;

    /**
     * Constructor allowing instantiation from 3rd party framework like MicroProfile Rest client
     * @param name Health Check procedure's name
     * @param status Health Check procedure's status
     * @param data additional data for Health Check procedure
     */
    public HealthCheckResponse(String name, Status status, Optional<Map<String, Object>> data) {
        this.name = name;
        this.status = status;
        this.data = data;
    }

    /**
     * Default constructor
     */
    public HealthCheckResponse() {
        name = null;
        status = null;
        data = null;
    }

    /**
     * Used by OSGi environment where the service loader pattern is not supported.
     *
     * @param provider the provider instance to use.
     * @deprecated use {{@link HealthCheckResponseProviderResolver#setProvider}} instead
     */
    public static void setResponseProvider(HealthCheckResponseProvider provider) {
        HealthCheckResponseProviderResolver.setProvider(provider);
    }

    /**
     * Creates a {@link HealthCheckResponseBuilder} with a name.
     *
     * @param name the check name
     * @return a new health check builder with a name
     */
    public static HealthCheckResponseBuilder named(String name) {

        return HealthCheckResponseProviderResolver.getProvider().createResponseBuilder().name(name);
    }

    /**
     * Creates an empty {@link HealthCheckResponseBuilder}.
     *
     * <b>Note:</b> The health check response name is required and needs to be set before the response is constructed.
     *
     * @return a new, empty health check builder
     */
    public static HealthCheckResponseBuilder builder() {
        return HealthCheckResponseProviderResolver.getProvider().createResponseBuilder();
    }

    /**
     * Creates a successful health check with a name.
     *
     * @param name the check name
     * @return a new sucessful health check response with a name
     */
    public static HealthCheckResponse up(String name) {
        return HealthCheckResponse.named(name).up().build();
    }

    /**
     * Creates a failed health check with a name.
     *
     * @param name the check name
     * @return a new failed health check response with a name
     */
    public static HealthCheckResponse down(String name) {
        return HealthCheckResponse.named(name).down().build();
    }

    // the actual contract

    public enum Status {UP, DOWN}

    public String getName(){
        return name;
    }

    public Status getStatus(){
        return status;
    }

    public Optional<Map<String, Object>> getData() {
        return data;
    }

}
