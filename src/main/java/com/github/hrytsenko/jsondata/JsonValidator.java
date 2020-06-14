/*
 * Copyright (C) 2020 Anton Hrytsenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hrytsenko.jsondata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import javax.json.JsonReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JsonValidator {

    Provider provider;

    public static JsonValidator create(String schema) {
        return new JsonValidator(JustifyProvider.create(schema));
    }

    public void validate(JsonEntity<?> entity) {
        provider.validate(JsonParser.entityToMap(entity));
    }

    public void validate(List<? extends JsonEntity<?>> entities) {
        provider.validate(JsonParser.entitiesToList(entities));
    }

    interface Provider {

        void validate(Map<String, ?> json);

        void validate(List<Map<String, ?>> json);

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    static class JustifyProvider implements Provider {

        private static final JsonValidationService VALIDATOR = JsonValidationService.newInstance();

        JsonSchema schema;

        public static JustifyProvider create(String schema) {
            return new JustifyProvider(VALIDATOR.readSchema(new StringReader(schema)));
        }

        @Override
        public void validate(Map<String, ?> json) {
            validate(JsonParser.mapToString(json));
        }

        @Override
        public void validate(List<Map<String, ?>> json) {
            validate(JsonParser.listToString(json));
        }

        @SneakyThrows
        private void validate(String json) {
            try (JsonReader reader = VALIDATOR.createReader(new StringReader(json), schema, ProblemHandler.throwing())) {
                reader.read();
            }
        }

    }

}
