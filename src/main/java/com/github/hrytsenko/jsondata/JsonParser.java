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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_LONG_FOR_INTS;

/**
 * <p>This class provides utilities to serialize and deserialize JSON entities.
 */
@UtilityClass
public class JsonParser {

    private static final Provider provider = JacksonProvider.create();

    public static Map<String, ?> stringToMap(String json) {
        return JsonExceptions.wrap(
                () -> provider.readObject(json),
                exception -> new JsonParserException("Deserialization failed", exception));
    }

    public static List<Map<String, ?>> stringToList(String json) {
        return JsonExceptions.wrap(
                () -> provider.readObjects(json),
                exception -> new JsonParserException("Deserialization failed", exception));
    }

    public static <R extends JsonEntity<R>> R stringToEntity(String json, Supplier<R> supplier) {
        return mapToEntity(stringToMap(json), supplier);
    }

    public static <R extends JsonEntity<R>> List<R> stringToEntities(String json, Supplier<R> supplier) {
        return listToEntities(stringToList(json), supplier);
    }

    public static <R extends JsonEntity<R>> R mapToEntity(Map<String, ?> json, Supplier<R> supplier) {
        return supplier.get().fromMap(json);
    }

    public static <R extends JsonEntity<R>> List<R> listToEntities(List<Map<String, ?>> json, Supplier<R> supplier) {
        return json.stream()
                .map(entity -> Objects.nonNull(entity) ? mapToEntity(entity, supplier) : null)
                .collect(Collectors.toList());
    }

    public static <E> Map<String, E> entityToMap(JsonEntity<?> entity) {
        return entity.asMap();
    }

    public static List<Map<String, ?>> entitiesToList(List<? extends JsonEntity<?>> entities) {
        return entities.stream()
                .map(entity -> Objects.nonNull(entity) ? entityToMap(entity) : null)
                .collect(Collectors.toList());
    }

    public static String mapToString(Map<String, ?> json) {
        return JsonExceptions.wrap(
                () -> provider.writeObject(json),
                exception -> new JsonParserException("Serialization failed", exception));
    }

    public static String listToString(List<Map<String, ?>> json) {
        return JsonExceptions.wrap(
                () -> provider.writeObjects(json),
                exception -> new JsonParserException("Serialization failed", exception));
    }

    public static String entityToString(JsonEntity<?> entity) {
        return mapToString(entityToMap(entity));
    }

    public static String entitiesToString(List<? extends JsonEntity<?>> entities) {
        return listToString(entitiesToList(entities));
    }

    public static <R extends JsonEntity<R>> Function<Map<String, ?>, R> fromMapTo(Supplier<R> supplier) {
        return object -> supplier.get().fromMap(object);
    }

    public static <R extends JsonEntity<R>> Function<JsonEntity<?>, R> fromEntityTo(Supplier<R> supplier) {
        return entity -> entity.as(supplier);
    }

    interface Provider {

        Map<String, ?> readObject(String json);

        List<Map<String, ?>> readObjects(String json);

        String writeObject(Map<String, ?> json);

        String writeObjects(List<Map<String, ?>> json);

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    static class JacksonProvider implements Provider {

        public static JacksonProvider create() {
            return new JacksonProvider(
                    new ObjectMapper()
                            .configure(USE_LONG_FOR_INTS, true)
                            .configure(ALLOW_SINGLE_QUOTES, true)
            );
        }

        ObjectMapper objectMapper;

        @Override
        public Map<String, ?> readObject(String json) {
            return read(json);
        }

        @Override
        public List<Map<String, ?>> readObjects(String json) {
            return read(json);
        }

        @SneakyThrows
        private <R> R read(String json) {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        }

        @Override
        public String writeObject(Map<String, ?> json) {
            return write(json);
        }

        @Override
        public String writeObjects(List<Map<String, ?>> json) {
            return write(json);
        }

        @SneakyThrows
        private String write(Object object) {
            return objectMapper.writeValueAsString(object);
        }

    }

}
