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

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_LONG_FOR_INTS;

public abstract class JsonEntity<T extends JsonEntity<T>> {

    public static final JacksonJsonProvider PROVIDER =
            new JacksonJsonProvider(
                    new ObjectMapper()
                            .configure(USE_LONG_FOR_INTS, true)
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

    protected Long getNumber(String path) {
        return getObject(path);
    }

    protected Boolean getBoolean(String path) {
        return getObject(path);
    }

    protected <E> Map<String, E> getMap(String path) {
        return getObject(path);
    }

    protected <E> List<E> getList(String path) {
        return getObject(path);
    }

    protected <R extends JsonEntity<R>> R getEntity(String path, Supplier<R> supplier) {
        Map<String, ?> object = getMap(path);
        if (Objects.isNull(object)) {
            return null;
        }
        return supplier.get().fromMap(object);
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

    protected <R> R getObject(String path) {
        try {
            return context.read(path);
        } catch (PathNotFoundException exception) {
            return null;
        }
    }

    protected T putString(String path, String value) {
        return putObject(path, value);
    }

    protected T putNumber(String path, Long value) {
        return putObject(path, value);
    }

    protected T putBoolean(String path, Boolean value) {
        return putObject(path, value);
    }

    protected T putMap(String path, Map<String, ?> value) {
        return putObject(path, value);
    }

    protected T putList(String path, List<?> value) {
        return putObject(path, value);
    }

    protected T putEntity(String path, JsonEntity<?> entity) {
        Map<String, ?> object = entity.asMap();
        return putObject(path, object);
    }

    protected T putEntities(String path, List<? extends JsonEntity<?>> entities) {
        List<Map<String, ?>> objects = entities.stream()
                .map(JsonEntity::asMap)
                .collect(Collectors.toList());
        return putObject(path, objects);
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

    protected T mergeMap(Map<String, ?> value) {
        Map<String, Object> json = asMap();
        json.putAll(value);
        return fromMap(json);
    }

    protected T mergeEntity(JsonEntity<?> entity) {
        return mergeMap(entity.asMap());
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

        private Constructor<?> constructor;

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
