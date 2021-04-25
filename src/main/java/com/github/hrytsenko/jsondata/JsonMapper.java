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

/**
 * <p>Class {@link JsonMapper} transforms JSON entities via a Jolt schema.
 * <pre>
 * JsonMapper&lt;Output&gt; mapper = JsonMapper.create(JsonResources.readResource("/schema.json"), Output::new);
 * Output output = mapper.map(input);
 * </pre>
 *
 * <p><b>Instances of this class are immutable and thread-safe.</b>
 * Reuse instances of this class where possible.
 *
 * @param <R> the type of the output JSON entity.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JsonMapper<R extends JsonEntity<R>> {

    Provider provider;
    Supplier<R> supplier;

    /**
     * Creates a JSON mapper for a given Jolt schema.
     *
     * @param schema   the Jolt schema for the transformation.
     *                 Use {@link JsonResources} to read the Jolt schema.
     * @param supplier the supplier for output JSON entities.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return a new instance of a JSON mapper.
     * @throws JsonMapperException if the Jolt schema is malformed.
     */
    public static <R extends JsonEntity<R>> JsonMapper<R> create(String schema, Supplier<R> supplier) {
        return JsonExceptions.wrap(
                () -> new JsonMapper<>(JoltProvider.create(schema), supplier),
                exception -> new JsonMapperException("Configuration failed", exception));
    }

    /**
     * Transforms a JSON entity.
     *
     * @param entity the input Json entity.
     * @return the output JSON entity.
     * @throws JsonMapperException if the transformation is failed.
     */
    public R map(JsonEntity<?> entity) {
        return JsonExceptions.wrap(
                () -> JsonParser.mapToEntity(provider.mapObject(JsonParser.entityToMap(entity)), supplier),
                exception -> new JsonMapperException("Transformation failed", exception));
    }

    /**
     * Transforms a list of JSON entities.
     *
     * @param entities the input list of JSON entities.
     * @return the output JSON entity.
     * @throws JsonMapperException if the transformation is failed.
     */
    public R map(List<? extends JsonEntity<?>> entities) {
        return JsonExceptions.wrap(
                () -> JsonParser.mapToEntity(provider.mapObjects(JsonParser.entitiesToList(entities)), supplier),
                exception -> new JsonMapperException("Transformation failed", exception));
    }

    interface Provider {

        Map<String, ?> mapObject(Map<String, ?> json);

        Map<String, ?> mapObjects(List<Map<String, ?>> json);

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    static class JoltProvider implements Provider {

        Chainr schema;

        public static JoltProvider create(String schema) {
            return new JoltProvider(Chainr.fromSpec(JsonParser.stringToList(schema)));
        }

        @Override
        public Map<String, ?> mapObject(Map<String, ?> json) {
            return transform(json);
        }

        @Override
        public Map<String, ?> mapObjects(List<Map<String, ?>> json) {
            return transform(json);
        }

        private Map<String, ?> transform(Object json) {
            @SuppressWarnings("unchecked")
            Map<String, ?> output = (Map<String, ?>) schema.transform(json);
            Objects.requireNonNull(output, "Output is undefined (use default output)");
            return output;
        }

    }

}
