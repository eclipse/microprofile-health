/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 *
 * This qualifier is used to define a Startup Health Check procedure mapped to the Startup Kubernetes probes.
 *
 * @author Martin Stefanko
 * @since 3.1
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, PARAMETER, FIELD})
public @interface Startup {

    /**
     * Support inline instantiation of the {@link Startup} qualifier.
     *
     * @author Martin Stefanko
     * @since 3.1
     */
    public static final class Literal extends AnnotationLiteral<Startup> implements Startup {
        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;
    }
}
