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

/**
 * A builder to construct a health check procedure response.
 *
 * <p>
 * The {@link HealthCheckResponseBuilder} class is reserved for an extension by implementation providers.
 * </p>
 */
public abstract class HealthCheckResponseBuilder {

    /**
     * Sets the name of the health check response.
     *
     * <b>Note:</b> The health check response name is required and needs to be set before the response is constructed.
     *
     * @param name The health check response name
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder name(String name);

    /**
     * Adds additional string data to the health check response.
     * Puts the {@code value} identified by {@code key} to the data section of the health check response.
     * Additional invocations of a {@code withData} method with the same {@code key} override the key-value pair.
     *
     * @param key   the identifier
     * @param value the value
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder withData(String key, String value);

    /**
     * Adds additional numeric data to the health check response.
     * Puts the long {@code value} identified by {@code key} to the data section of the health check response.
     * Additional invocations of a {@code withData} method with the same {@code key} override the key-value pair.
     *
     * @param key   the identifier
     * @param value the value
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder withData(String key, long value);

    /**
     * Adds additional boolean data to the health check response.
     * Puts the boolean {@code value} identified by {@code key} to the data section of the health check response.
     * Additional invocations of a {@code withData} method with the same {@code key} override the key-value pair.
     *
     * @param key   the identifier
     * @param value the value
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder withData(String key, boolean value);

    /**
     * Sets the status of the health check response to {@link HealthCheckResponse.Status#UP}.
     * This implies that the health check was successful.
     *
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder up();

    /**
     * Sets the status of the health check response to {@link HealthCheckResponse.Status#DOWN}.
     * This implies that the health check was <i>not</i> successful.
     *
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder down();

    /**
     * Sets the status of the health check response according to the boolean value {@code up}.
     *
     * @param up the status
     * @return this builder
     */
    public abstract HealthCheckResponseBuilder status(boolean up);

    /**
     * Creates a {@link HealthCheckResponse} from the current builder.
     *
     * @return A new {@link HealthCheckResponse} defined by this builder
     */
    public abstract HealthCheckResponse build();

}
