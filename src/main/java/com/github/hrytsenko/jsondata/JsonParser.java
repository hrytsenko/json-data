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
 * <p>Class {@link JsonParser} converts JSON entities from and to plain data types.
 *
 * <p><b>All {@link JsonParser} methods are thread-safe.</b>
 * {@link JsonParser} methods do not perform any copying for input or output objects.
 */
@UtilityClass
public class JsonParser {

    private static final Provider provider = JacksonProvider.create();

    /**
     * Converts a JSON string to a plain map.
     *
     * @param json the input JSON string.
     * @return the output plain map.
     * @throws JsonParserException if the conversion is failed.
     */
    public static Map<String, ?> stringToMap(String json) {
        return JsonExceptions.wrap(
                () -> provider.readObject(json),
                exception -> new JsonParserException("Deserialization failed", exception));
    }

    /**
     * Converts a JSON string to a plain list.
     *
     * @param json the input JSON string.
     * @return the output plain list.
     * @throws JsonParserException if the conversion is failed.
     */
    public static List<Map<String, ?>> stringToList(String json) {
        return JsonExceptions.wrap(
                () -> provider.readObjects(json),
                exception -> new JsonParserException("Deserialization failed", exception));
    }

    /**
     * Converts a JSON string to a JSON entity.
     *
     * @param json     the input JSON string.
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the output JSON entity.
     * @throws JsonParserException if the conversion is failed.
     */
    public static <R extends JsonEntity<R>> R stringToEntity(String json, Supplier<R> supplier) {
        return mapToEntity(stringToMap(json), supplier);
    }

    /**
     * Converts a JSON string to a list of JSON entities.
     *
     * @param json     the input JSON string.
     * @param supplier the supplier for output JSON entities.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the output list of JSON entities.
     * @throws JsonParserException if the conversion is failed.
     */
    public static <R extends JsonEntity<R>> List<R> stringToEntities(String json, Supplier<R> supplier) {
        return listToEntities(stringToList(json), supplier);
    }

    /**
     * Converts a plain map to a JSON entity.
     *
     * @param json     the input plain map.
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the output JSON entity.
     * @throws JsonParserException if the conversion is failed.
     */
    public static <R extends JsonEntity<R>> R mapToEntity(Map<String, ?> json, Supplier<R> supplier) {
        return supplier.get().fromMap(json);
    }

    /**
     * Converts a plain list to a list of JSON entities.
     *
     * @param json     the input plain list.
     * @param supplier the supplier for output JSON entities.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the output list of JSON entities.
     * @throws JsonParserException if the conversion is failed.
     */
    public static <R extends JsonEntity<R>> List<R> listToEntities(List<Map<String, ?>> json, Supplier<R> supplier) {
        return json.stream()
                .map(entity -> Objects.nonNull(entity) ? mapToEntity(entity, supplier) : null)
                .collect(Collectors.toList());
    }

    /**
     * Converts a JSON entity to a plain map.
     *
     * @param entity the input JSON entity.
     * @return the output plain map.
     */
    public static <E> Map<String, E> entityToMap(JsonEntity<?> entity) {
        return entity.asMap();
    }

    /**
     * Converts a list of JSON entities to a plain list.
     *
     * @param entities the input list of JSON entities.
     * @return the output plain list.
     */
    public static List<Map<String, ?>> entitiesToList(List<? extends JsonEntity<?>> entities) {
        return entities.stream()
                .map(entity -> Objects.nonNull(entity) ? entityToMap(entity) : null)
                .collect(Collectors.toList());
    }

    /**
     * Converts a plain map to a JSON string.
     *
     * @param json the input plain map.
     * @return the output JSON string.
     * @throws JsonParserException if the conversion is failed.
     */
    public static String mapToString(Map<String, ?> json) {
        return JsonExceptions.wrap(
                () -> provider.writeObject(json),
                exception -> new JsonParserException("Serialization failed", exception));
    }

    /**
     * Converts a plain list to a JSON string.
     *
     * @param json the input plain list.
     * @return the output JSON string.
     * @throws JsonParserException if the conversion is failed.
     */
    public static String listToString(List<Map<String, ?>> json) {
        return JsonExceptions.wrap(
                () -> provider.writeObjects(json),
                exception -> new JsonParserException("Serialization failed", exception));
    }

    /**
     * Converts a JSON entity to a JSON string.
     *
     * @param entity the input JSON entity.
     * @return the output JSON string.
     * @throws JsonParserException if the conversion is failed.
     */
    public static String entityToString(JsonEntity<?> entity) {
        return mapToString(entityToMap(entity));
    }

    /**
     * Converts a list of JSON entities to a JSON string.
     *
     * @param entities the input list of JSON entities.
     * @return the output JSON string.
     * @throws JsonParserException if the conversion is failed.
     */
    public static String entitiesToString(List<? extends JsonEntity<?>> entities) {
        return listToString(entitiesToList(entities));
    }

    /**
     * Creates a helper function that converts a plain map to a JSON entity.
     *
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the helper function.
     */
    public static <R extends JsonEntity<R>> Function<Map<String, ?>, R> fromMapTo(Supplier<R> supplier) {
        return object -> supplier.get().fromMap(object);
    }

    /**
     * Creates a helper function that converts a JSON entity to another JSON entity.
     *
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the helper function.
     */
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
            return objectMapper.readValue(json, new TypeReference<R>() {
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
