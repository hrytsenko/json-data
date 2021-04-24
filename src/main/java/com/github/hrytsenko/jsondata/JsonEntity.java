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
 * <p>JSON entities must utilize a default constructor and avoid any explicit constructors.
 * This enables integration with third-party libraries that may need to instantiate JSON entities.
 * JSON entities must use static factory methods instead of constructors for custom initialization behavior:
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
 * As well, JSON entities does not perform any copying for input or output objects.
 *
 * @param <T> the type of the JSON entity
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

    protected JsonEntity() {
        fromMap(new LinkedHashMap<>());
    }

    T fromMap(Map<String, ?> json) {
        context = JsonPath.using(CONFIGURATION)
                .parse(Objects.requireNonNull(json));
        return self();
    }

    protected boolean contains(String path) {
        return Objects.nonNull(getObject(path));
    }

    protected String getString(String path) {
        return getObject(path);
    }

    protected T putString(String path, String value) {
        return putObject(path, value);
    }

    protected Long getNumber(String path) {
        Number number = getObject(path);
        if (Objects.isNull(number)) {
            return null;
        }
        return number.longValue();
    }

    protected T putNumber(String path, Long value) {
        return putObject(path, value);
    }

    protected Boolean getBoolean(String path) {
        return getObject(path);
    }

    protected T putBoolean(String path, Boolean value) {
        return putObject(path, value);
    }

    protected <E> Map<String, E> getMap(String path) {
        return getObject(path);
    }

    protected T putMap(String path, Map<String, ?> value) {
        return putObject(path, value);
    }

    protected T mergeMap(Map<String, ?> value) {
        Map<String, Object> json = asMap();
        json.putAll(value);
        return fromMap(json);
    }

    protected <E> List<E> getList(String path) {
        return getObject(path);
    }

    protected T putList(String path, List<?> value) {
        return putObject(path, value);
    }

    protected <R extends JsonEntity<R>> R getEntity(String path, Supplier<R> supplier) {
        Map<String, ?> object = getMap(path);
        if (Objects.isNull(object)) {
            return null;
        }
        return supplier.get().fromMap(object);
    }

    protected T putEntity(String path, JsonEntity<?> entity) {
        Map<String, ?> object = entity.asMap();
        return putObject(path, object);
    }

    protected T mergeEntity(JsonEntity<?> entity) {
        return mergeMap(entity.asMap());
    }

    protected <E extends JsonEntity<E>> List<E> getEntities(String path, Supplier<E> supplier) {
        List<Map<String, ?>> objects = getList(path);
        if (Objects.isNull(objects)) {
            return null;
        }
        return objects.stream()
                .map(json -> supplier.get().fromMap(json))
                .collect(Collectors.toList());
    }

    protected T putEntities(String path, List<? extends JsonEntity<?>> entities) {
        List<Map<String, ?>> objects = entities.stream()
                .map(JsonEntity::asMap)
                .collect(Collectors.toList());
        return putObject(path, objects);
    }

    protected <R> R getObject(String path) {
        try {
            return context.read(path);
        } catch (PathNotFoundException exception) {
            return null;
        }
    }

    protected T putObject(String path, Object value) {
        int keyPosition = path.lastIndexOf('.');
        String parent = keyPosition != -1 ? path.substring(0, keyPosition) : "$";
        if (!Objects.equals("$", parent) && !contains(parent)) {
            putMap(parent, new LinkedHashMap<>());
        }
        context.put(parent, path.substring(keyPosition + 1), value);
        return self();
    }

    protected T remove(String path) {
        context.delete(path);
        return self();
    }

    public String asString() {
        return context.jsonString();
    }

    public <E> Map<String, E> asMap() {
        return context.json();
    }

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

    public static class Factory {

        private final Constructor<?> constructor;

        @SneakyThrows
        public Factory(Class<?> entityClass) {
            constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
        }

        @SneakyThrows
        public JsonEntity<?> create() {
            return (JsonEntity<?>) constructor.newInstance();
        }

        public JsonEntity<?> createFromMap(Map<String, ?> json) {
            return create().fromMap(json);
        }

    }

}
