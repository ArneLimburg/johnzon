/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.johnzon.jsonb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbVisibility;

import org.junit.Test;

public class DefaultPropertyVisibilityStrategyTest {
    @Test // note it is not a valid case since our default impl is internal but it guarantees we dont wrongly impl equals
    public void subclassing() throws Exception {
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            final TheClass theClass = jsonb.fromJson("{\"foo\":true}", TheClass.class);
            assertTrue(theClass.isFoo());
        }
    }

    @Test
    public void annotated() throws Exception {
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            final VisibleCauseAnnotated theClass = jsonb.fromJson("{\"foo\":true}", VisibleCauseAnnotated.class);
            assertTrue(theClass.isFoo());
        }
    }

    @Test
    public void hiddenGetter() throws Exception {
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            assertFalse(jsonb.fromJson("{\"foo\":true}", HideAllModel.class).isFoo());
            assertFalse(jsonb.fromJson("{\"foo\":true}", HideAllDefaultModel.class).isFoo());
        }
    }

    public static class HideAll extends DefaultPropertyVisibilityStrategy {
        @Override
        public boolean isVisible(final Field field) {
            return false;
        }

        @Override
        public boolean isVisible(final Method method) {
            return false;
        }
    }

    public static class MyVisibility extends DefaultPropertyVisibilityStrategy {
        @Override
        public boolean isVisible(final Field field) {
            return true;
        }

        @Override
        public boolean isVisible(final Method method) {
            return true;
        }
    }

    @JsonbVisibility(MyVisibility.class)
    public static final class TheClass {
        @JsonbProperty
        private boolean foo;

        public boolean isFoo() {
            return foo;
        }
    }

    public static final class VisibleCauseAnnotated {
        @JsonbProperty
        private boolean foo;

        public boolean isFoo() {
            return foo;
        }
    }

    @JsonbVisibility(HideAll.class)
    public static final class HideAllModel {
        protected boolean foo;

        @JsonbTransient
        public boolean isFoo() {
            return foo;
        }
    }

    public static final class HideAllDefaultModel {
        protected boolean foo;

        @JsonbTransient
        public boolean isFoo() {
            return foo;
        }
    }
}
