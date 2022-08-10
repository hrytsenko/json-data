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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>Class {@link JsonBean} is a generic JSON entity.
 *
 * <p>Use JSON beans as temporary entities with a limited scope and a short lifetime.
 * Otherwise, create a specific JSON entity.
 */
public final class JsonBean extends JsonEntity<JsonBean> {

    /**
     * Creates a JSON bean.
     *
     * @return the created JSON bean.
     * @implNote A synonym for a default constructor.
     */
    public static JsonBean create() {
        return new JsonBean();
    }

    @Override
    public boolean contains(String path) {
        return super.contains(path);
    }

    @Override
    public String getString(String path) {
        return super.getString(path);
    }

    @Override
    public String getStringOrDefault(String path, String defaultValue) {
        return super.getStringOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putString(String path, String defaultValue) {
        return super.putString(path, defaultValue);
    }

    @Override
    public Long getNumber(String path) {
        return super.getNumber(path);
    }

    @Override
    public Long getNumberOrDefault(String path, Long defaultValue) {
        return super.getNumberOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putNumber(String path, Long value) {
        return super.putNumber(path, value);
    }

    @Override
    public Boolean getBoolean(String path) {
        return super.getBoolean(path);
    }

    @Override
    public Boolean getBooleanOrDefault(String path, Boolean defaultValue) {
        return super.getBooleanOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putBoolean(String path, Boolean value) {
        return super.putBoolean(path, value);
    }

    @Override
    public <E> Map<String, E> getMap(String path) {
        return super.getMap(path);
    }

    @Override
    public <E> Map<String, E> getMapOrDefault(String path, Map<String, E> defaultValue) {
        return super.getMapOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putMap(String path, Map<String, ?> value) {
        return super.putMap(path, value);
    }

    @Override
    public JsonBean mergeMap(Map<String, ?> value) {
        return super.mergeMap(value);
    }

    @Override
    public <E> List<E> getList(String path) {
        return super.getList(path);
    }

    @Override
    public <E> List<E> getListOrDefault(String path, List<E> defaultValue) {
        return super.getListOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putList(String path, List<?> value) {
        return super.putList(path, value);
    }

    @Override
    public <R extends JsonEntity<R>> R getEntity(String path, Supplier<R> supplier) {
        return super.getEntity(path, supplier);
    }

    @Override
    public <R extends JsonEntity<R>> R getEntityOrDefault(String path, Supplier<R> supplier, R defaultValue) {
        return super.getEntityOrDefault(path, supplier, defaultValue);
    }

    @Override
    public JsonBean putEntity(String path, JsonEntity<?> entity) {
        return super.putEntity(path, entity);
    }

    @Override
    public JsonBean mergeEntity(JsonEntity<?> entity) {
        return super.mergeEntity(entity);
    }

    @Override
    public <R extends JsonEntity<R>> List<R> getEntities(String path, Supplier<R> supplier) {
        return super.getEntities(path, supplier);
    }

    @Override
    public <R extends JsonEntity<R>> List<R> getEntitiesOrDefault(String path, Supplier<R> supplier, List<R> defaultValue) {
        return super.getEntitiesOrDefault(path, supplier, defaultValue);
    }

    @Override
    public JsonBean putEntities(String path, List<? extends JsonEntity<?>> entities) {
        return super.putEntities(path, entities);
    }

    @Override
    public <R> R getObject(String path) {
        return super.getObject(path);
    }

    @Override
    public <R> R getObjectOrDefault(String path, R defaultValue) {
        return super.getObjectOrDefault(path, defaultValue);
    }

    @Override
    public JsonBean putObject(String path, Object value) {
        return super.putObject(path, value);
    }

    @Override
    public JsonBean remove(String path) {
        return super.remove(path);
    }

}
