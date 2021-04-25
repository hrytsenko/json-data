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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_LONG_FOR_INTS;

/**
 * <p>Class {@link JsonEntity} is a base class for all JSON entities.
 * <pre>
 * class Entity extends JsonEntity&lt;Entity&gt; {
 *   public String getName() {
 *     return getString("entity.name");
 *   }
 * }
 * </pre>
 *
 * <p>JSON entities use only a default constructor and avoid any explicit constructors.
 * This enables integration with third-party libraries that may need to instantiate JSON entities.
 * JSON entities may use static factory methods instead of constructors for custom initialization behavior:
 * <pre>
 * class Entity extends JsonEntity&lt;Entity&gt; {
 *   public static create(String name) {
 *     return new Entity()
 *       .putString("entity.name", name);
 *   }
 * }
 * </pre>
 *
 * <p><b>JSON entities are mutable and not thread-safe.</b>
 * JSON entities does not perform any copying for input or output objects.
 *
 * @param <T> the type of the JSON entity.
 * @implNote JSON entities always represent integer values as {@link Long}.
 */
public abstract class JsonEntity<T extends JsonEntity<T>> {

    private static final JacksonJsonProvider PROVIDER =
            new JacksonJsonProvider(
                    new ObjectMapper()
                            .configure(USE_LONG_FOR_INTS, true)
                            .configure(ALLOW_SINGLE_QUOTES, true)
            );

    private static final Configuration CONFIGURATION =
            Configuration.defaultConfiguration()
                    .jsonProvider(PROVIDER)
                    .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    private DocumentContext context;

    /**
     * Creates a JSON entity.
     */
    protected JsonEntity() {
        fromMap(new LinkedHashMap<>());
    }

    T fromMap(Map<String, ?> json) {
        context = JsonPath.using(CONFIGURATION)
                .parse(Objects.requireNonNull(json));
        return self();
    }

    /**
     * Checks that a given path contains a non-null value.
     *
     * @param path the JSON path to read.
     * @return {@code true} if the given path contains a non-null value, otherwise {@code false}.
     */
    protected boolean contains(String path) {
        return Objects.nonNull(getObject(path));
    }

    /**
     * Reads a string value from a given path.
     *
     * @param path the JSON path to read.
     * @return the string value (or {@code null}).
     */
    protected String getString(String path) {
        return getObject(path);
    }

    /**
     * Writes a string value to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the string value to write.
     * @return the current JSON entity.
     */
    protected T putString(String path, String value) {
        return putObject(path, value);
    }

    /**
     * Reads a numerical value from a given path.
     *
     * @param path the JSON path to read.
     * @return the numerical value (or {@code null}).
     */
    protected Long getNumber(String path) {
        Number number = getObject(path);
        if (Objects.isNull(number)) {
            return null;
        }
        return number.longValue();
    }

    /**
     * Writes a numerical value to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the numerical value to write.
     * @return the current JSON entity.
     */
    protected T putNumber(String path, Long value) {
        return putObject(path, value);
    }

    /**
     * Reads a logical value from a given path.
     *
     * @param path the JSON path to read.
     * @return the logical value (or {@code null}).
     */
    protected Boolean getBoolean(String path) {
        return getObject(path);
    }

    /**
     * Writes a logical value to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the logical value to write.
     * @return the current JSON entity.
     */
    protected T putBoolean(String path, Boolean value) {
        return putObject(path, value);
    }

    /**
     * Reads a plain map from a given path.
     *
     * @param path the JSON path to read.
     * @param <E>  the type of map values.
     * @return the plain map (or {@code null}).
     */
    protected <E> Map<String, E> getMap(String path) {
        return getObject(path);
    }

    /**
     * Writes a plain map to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the plain map to write.
     * @return the current JSON entity.
     */
    protected T putMap(String path, Map<String, ?> value) {
        return putObject(path, value);
    }

    /**
     * Writes a plain map to a root.
     *
     * @param value the plain map to write.
     * @return the current JSON entity.
     */
    protected T mergeMap(Map<String, ?> value) {
        Map<String, Object> json = asMap();
        json.putAll(value);
        return fromMap(json);
    }

    /**
     * Reads a plain list from a given path.
     *
     * @param path the JSON path to read.
     * @param <E>  the type of list elements.
     * @return the plain list (or {@code null}).
     */
    protected <E> List<E> getList(String path) {
        return getObject(path);
    }

    /**
     * Writes a plain list to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the plain list to write.
     * @return the current JSON entity.
     */
    protected T putList(String path, List<?> value) {
        return putObject(path, value);
    }

    /**
     * Reads a JSON entity from a given path.
     *
     * @param path     the JSON path to read.
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the JSON entity (or {@code null}).
     */
    protected <R extends JsonEntity<R>> R getEntity(String path, Supplier<R> supplier) {
        Map<String, ?> object = getMap(path);
        if (Objects.isNull(object)) {
            return null;
        }
        return supplier.get().fromMap(object);
    }

    /**
     * Writes a JSON entity to a given path.
     *
     * @param path   the JSON path to write.
     * @param entity the JSON entity to write.
     * @return the current JSON entity.
     */
    protected T putEntity(String path, JsonEntity<?> entity) {
        Map<String, ?> object = entity.asMap();
        return putObject(path, object);
    }

    /**
     * Writes a JSON entity to a root.
     *
     * @param entity the JSON entity to write.
     * @return the current JSON entity.
     */
    protected T mergeEntity(JsonEntity<?> entity) {
        return mergeMap(entity.asMap());
    }

    /**
     * Reads a list of JSON entities from a given path.
     *
     * @param path     the JSON path to read.
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the list of JSON entities (or {@code null}).
     */
    protected <R extends JsonEntity<R>> List<R> getEntities(String path, Supplier<R> supplier) {
        List<Map<String, ?>> objects = getList(path);
        if (Objects.isNull(objects)) {
            return null;
        }
        return objects.stream()
                .map(json -> supplier.get().fromMap(json))
                .collect(Collectors.toList());
    }

    /**
     * Writes a list of JSON entities to a given path.
     *
     * @param path     the JSON path to write.
     * @param entities the list of JSON entities to write.
     * @return the current JSON entity.
     */
    protected T putEntities(String path, List<? extends JsonEntity<?>> entities) {
        List<Map<String, ?>> objects = entities.stream()
                .map(JsonEntity::asMap)
                .collect(Collectors.toList());
        return putObject(path, objects);
    }

    /**
     * Reads an arbitrary object from a given path.
     *
     * @param path the JSON path to read.
     * @param <R>  the type of the output object.
     * @return the arbitrary object (or {@code null}).
     */
    protected <R> R getObject(String path) {
        try {
            return context.read(path);
        } catch (PathNotFoundException exception) {
            return null;
        }
    }

    /**
     * Writes the arbitrary object to a given path.
     *
     * @param path  the JSON path to write.
     * @param value the arbitrary object to write.
     * @return the current JSON entity.
     */
    protected T putObject(String path, Object value) {
        int keyPosition = path.lastIndexOf('.');
        String parent = keyPosition != -1 ? path.substring(0, keyPosition) : "$";
        if (!Objects.equals("$", parent) && !contains(parent)) {
            putMap(parent, new LinkedHashMap<>());
        }
        context.put(parent, path.substring(keyPosition + 1), value);
        return self();
    }

    /**
     * Removes an arbitrary object from a given path.
     *
     * @param path the JSON path to remove.
     * @return the current JSON entity.
     */
    protected T remove(String path) {
        context.delete(path);
        return self();
    }

    /**
     * Represents a JSON entity as a JSON string.
     *
     * @return the output JSON string.
     */
    public String asString() {
        return context.jsonString();
    }

    /**
     * Represents a JSON entity as a plain map.
     *
     * @return the output plain map.
     * @implNote The output plain map is a view of the current JSON entity.
     */
    public <E> Map<String, E> asMap() {
        return context.json();
    }

    /**
     * Represents a JSON entity as another JSON entity.
     *
     * @param supplier the supplier for the output JSON entity.
     *                 Use a default constructor to create the supplier.
     * @param <R>      the type of the output JSON entity.
     * @return the output JSON entity.
     * @implNote The output JSON entity is a view of the current JSON entity.
     */
    public <R extends JsonEntity<R>> R as(Supplier<R> supplier) {
        return supplier.get().fromMap(asMap());
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int hashCode() {
        return context.json().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JsonEntity<?>)) {
            return false;
        }
        JsonEntity<?> other = (JsonEntity<?>) obj;
        return Objects.equals(this.context.json(), other.context.json());
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    /**
     * Class {@link Factory} creates JSON entities via a default constructor.
     *
     * <p><b>Instances of this class are immutable and thread-safe.</b>
     * Reuse instances of this class where possible.
     */
    public static class Factory {

        private final Constructor<?> constructor;

        /**
         * Creates a factory for a given JSON entity.
         *
         * @param entityClass the class of the target JSON entity.
         */
        @SneakyThrows
        public Factory(Class<?> entityClass) {
            constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
        }

        /**
         * Creates a JSON entity.
         *
         * @return the created JSON entity.
         */
        @SneakyThrows
        public JsonEntity<?> create() {
            return (JsonEntity<?>) constructor.newInstance();
        }

        /**
         * Creates a JSON entity from a plain map.
         *
         * @param json the input plain map.
         * @return the created JSON entity.
         */
        public JsonEntity<?> createFromMap(Map<String, ?> json) {
            return create().fromMap(json);
        }

    }

}
