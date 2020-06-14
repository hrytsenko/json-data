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

import com.bazaarvoice.jolt.Chainr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JsonMapper<R extends JsonEntity<R>> {

    Provider provider;
    Supplier<R> supplier;

    public static <R extends JsonEntity<R>> JsonMapper<R> create(String schema, Supplier<R> supplier) {
        return new JsonMapper<>(JoltProvider.create(schema), supplier);
    }

    public R map(JsonEntity<?> entity) {
        return JsonParser.mapToEntity(provider.map(JsonParser.entityToMap(entity)), supplier);
    }

    public R map(List<? extends JsonEntity<?>> entities) {
        return JsonParser.mapToEntity(provider.map(JsonParser.entitiesToList(entities)), supplier);
    }

    interface Provider {

        Map<String, ?> map(Map<String, ?> json);

        Map<String, ?> map(List<Map<String, ?>> json);

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    static class JoltProvider implements Provider {

        Chainr schema;

        public static JoltProvider create(String schema) {
            return new JoltProvider(Chainr.fromSpec(JsonParser.stringToList(schema)));
        }

        @Override
        public Map<String, ?> map(Map<String, ?> json) {
            return map(schema, json);
        }

        @Override
        public Map<String, ?> map(List<Map<String, ?>> json) {
            return map(schema, json);
        }

        private Map<String, ?> map(Chainr schema, Object json) {
            Objects.requireNonNull(json, "Input is undefined");
            @SuppressWarnings("unchecked")
            Map<String, ?> output = (Map<String, ?>) schema.transform(json);
            Objects.requireNonNull(output, "Output is undefined (use default output)");
            return output;
        }

    }

}
