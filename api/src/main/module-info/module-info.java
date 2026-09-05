/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

/**
 * MicroProfile Health API.
 *
 * <p>
 * {@code @Liveness}, {@code @Readiness} and {@code @Startup} are meta-annotated with
 * {@code jakarta.inject.Qualifier} and ship {@code AnnotationLiteral} subclasses. The Inject and CDI
 * modules are optional at resolution time ({@code static}): {@code HealthCheck} and
 * {@code HealthCheckResponse} can be used without a CDI container. When they are present they are
 * re-exported ({@code transitive}) so that consumers using the qualifiers or their literals read them
 * without declaring the dependency themselves.
 */
module org.eclipse.microprofile.health {
    requires java.logging;
    requires static transitive jakarta.cdi;
    requires static transitive jakarta.inject;

    exports org.eclipse.microprofile.health;
    exports org.eclipse.microprofile.health.spi;

    uses org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;
}
