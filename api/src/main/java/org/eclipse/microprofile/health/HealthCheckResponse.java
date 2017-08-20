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

package org.eclipse.microprofile.health;

import org.eclipse.microprofile.health.spi.HealthCheckResponseProviderResolver;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * The response to a health check invocation.
 * <p>
 * The Response class is reserved for an extension by implementation providers.
 * An application should use one of the static methods to create a Response instance using a ResponseBuilder.
 * </p>
 *
 */
public abstract class HealthCheckResponse {

    private static final Logger LOGGER = Logger.getLogger(HealthCheckResponse.class.getName());

    public static HealthCheckResponseBuilder builder(String name) {
        HealthCheckResponseProviderResolver factory = HealthCheckResponseProviderResolver.instance();
        return factory.createResponseBuilder().name(name);
    }

    // the actual contract

    public enum State { UP, DOWN }

    public abstract String getName();

    public abstract State getState();

    public abstract Optional<Map<String, Object>> getAttributes();

}
