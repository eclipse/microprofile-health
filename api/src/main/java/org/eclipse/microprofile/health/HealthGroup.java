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

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 *
 * This qualifier is used to define a custom Health Check procedure by applying a group name to it
 *
 * @author Antoine Sabot-Durand
 * @since 2.2
 */
@Target({TYPE, METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
@Qualifier
@Repeatable(HealthGroups.class)
public @interface HealthGroup {

    /**
     * @return name of the custom group
     */
    String value();

    /**
     * Support inline instantiation of the {@link HealthGroup} qualifier.
     *
     * @author Antoine Sabot-Durand
     * @since 2.2
     */
    public static final class Literal extends AnnotationLiteral<HealthGroup> implements HealthGroup {

        private static final long serialVersionUID = 1L;

        private final String value;

        public static Literal of(String value) {
            return new Literal(value);
        }

        private Literal(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}

