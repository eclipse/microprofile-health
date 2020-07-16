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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This utility class is used to optimize invocation made through the SecurityManager
 *
 * @author Antoine Sabot-durand
 */

final class SecurityActions {

    private static final Logger LOGGER = Logger.getLogger(SecurityActions.class.getName());

    private SecurityActions() {

    }

    /**
     * Obtaining a service loader with Security Manager support
     *
     * @param service class to load
     * @param classLoader to use
     * @param <T> service type
     * @return the service loader
     */
    static <T> ServiceLoader<T> loadService(Class<T> service, ClassLoader classLoader) {
        return AccessController.doPrivileged(
                (PrivilegedAction<ServiceLoader<T>>) () -> ServiceLoader.load(service, classLoader)
        );
    }

    static ClassLoader getContextClassLoader() {
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
