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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A builder to construct a health procedure response
 */
public class HealthResponse {


    private final String name;

    private Optional<Map<String, Object>> attributes = Optional.empty();

    private HealthStatus.State state;

    private HealthResponse(String name) {
        this.name = name;
    }

    public static HealthResponse named(String name) {
        return new HealthResponse(name);
    }

    public HealthStatus up() {
        assertNamed();
        HealthStatusImpl status = new HealthStatusImpl(
                this.name,
                this.state,
                this.attributes
        );
        return status;
    }

    public HealthStatus down() {
        assertNamed();
        this.state = HealthStatus.State.DOWN;
        HealthStatusImpl status = new HealthStatusImpl(
                this.name,
                this.state,
                this.attributes
        );
        return status;
    }

    private void assertNamed() {
        if (this.name == null) {
            throw new IllegalStateException("HealthResponse need to be named");
        }
    }

    public HealthResponse withAttribute(String key, String value) {
        Map<String, Object> payload = getPayloadWrapper();
        payload.put(key, value);
        return this;
    }

    public HealthResponse withAttribute(String key, long value) {
        Map<String, Object> payload = getPayloadWrapper();
        payload.put(key, value);
        return this;
    }

    public HealthResponse withAttribute(String key, boolean value) {
        Map<String, Object> payload = getPayloadWrapper();
        payload.put(key, value);
        return this;
    }

    private Map<String, Object> getPayloadWrapper() {
        if (!this.attributes.isPresent()) {
            this.attributes = Optional.of(new HashMap<>());
        }
        return this.attributes.get();
    }

    public Optional<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public HealthStatus.State getState() {
        return state;
    }

    public final class  HealthStatusImpl implements HealthStatus {

        private final String name;
        private final State state;
        private final Optional<Map<String, Object>> attributes;

        public HealthStatusImpl(String name, HealthStatus.State state, Optional<Map<String, Object>> attributes) {

            this.name = name;
            this.state = state;
            this.attributes = attributes;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public Optional<Map<String, Object>> getAttributes() {
            return null;
        }
    }
}
