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

import java.util.Collections;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Predicate;

import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;


/**
 * This class a helper to retrieve the Microprofile HealthCheck implementation
 * by using Java {@link ServiceLoader} api.
 * It allows the usage of multiple implementations at the same time.
 *
 * @author Antoine Sabot-Durand
 * @since 3.0
 */
public class HealthCheckResponseProviderResolver {

    private static final Object LOCK = new Object();

    private static volatile HealthCheckResponseProvider provider = null;

    private static volatile Set<HealthCheckResponseProvider> discoveredProviders = null;

    private static volatile Predicate<HealthCheckResponseProvider> providerPredicate = p -> true;

    protected HealthCheckResponseProviderResolver() {
    }

    /**
     * @return the selected {@link HealthCheckResponseProvider} in cache. If not available it tries to resolve it first
     */
    public static HealthCheckResponseProvider getProvider() {
        if (provider == null) {
            if (discoveredProviders == null) {
                synchronized (LOCK) {
                    if (discoveredProviders == null) {
                        findAllProviders();
                    }
                }
            }
            provider = discoveredProviders.stream()
                .filter(providerPredicate)
                .findFirst().orElseThrow(() -> new IllegalStateException("No HealthCheckResponseProvider implementation found!"));
        }
        return provider;
    }

    /**
     *
     * Used to manually set the {@link HealthCheckResponseProvider}.
     * Also used by OSGi environment where the service loader pattern is not supported.
     *
     * @param provider the provider instance to use.
     */
    public static void setProvider(HealthCheckResponseProvider provider) {
        HealthCheckResponseProviderResolver.provider = provider;
    }

    /**
     *
     * Other way than {@link #setProvider} to set the {@link HealthCheckResponseProvider}
     * Setting a custom predicate will reset current provider.
     *
     * @param providerPredicate a predicate to choose the matching provider
     */
    public static void setProviderPredicate(Predicate<HealthCheckResponseProvider> providerPredicate) {
        HealthCheckResponseProviderResolver.providerPredicate = providerPredicate;
        setProvider(null);
    }

    private static void findAllProviders() {
        ServiceLoader<HealthCheckResponseProvider> providerLoader;
        Set<HealthCheckResponseProvider> providers = new HashSet<>();

        Class<HealthCheckResponseProvider> clazz = HealthCheckResponseProvider.class;
        ClassLoader cl = SecurityActions.getContextClassLoader();
        if (cl == null) {
            cl = clazz.getClassLoader();
        }
        providerLoader = SecurityActions.loadService(HealthCheckResponseProvider.class, cl);

        if (!providerLoader.iterator().hasNext()) {
            throw new IllegalStateException("Unable to locate HealthCheckResponseProvider");
        }

        try {
            providerLoader.forEach(providers::add);
        }
        catch (ServiceConfigurationError e) {
            throw new IllegalStateException(e);
        }
        discoveredProviders = Collections.unmodifiableSet(providers);
    }

    protected static void reset() {
        provider = null;
        discoveredProviders =null;
        providerPredicate = p -> true;


    }

}
