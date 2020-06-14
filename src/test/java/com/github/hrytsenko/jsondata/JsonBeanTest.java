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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class JsonBeanTest {

    @Test
    void create() {
        JsonBean actualBean = JsonBean.create();

        JsonBean expectedBean = JsonParser.stringToEntity("{}", JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    @ParameterizedTest
    @MethodSource("contains_data")
    void contains(String sourceJson, String sourcePath, boolean expectedResult) {
        JsonBean sourceBean = JsonParser.stringToEntity(sourceJson, JsonBean::create);

        boolean actualResult = sourceBean.contains(sourcePath);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    private static Stream<Arguments> contains_data() {
        return Stream.of(
                Arguments.of("{}", "foo", false),
                Arguments.of("{'foo':null}", "foo", false),
                Arguments.of("{'foo':'FOO'}", "foo", true)
        );
    }

    @ParameterizedTest
    @MethodSource("put_data")
    void put(String sourceJson, String sourcePath, String sourceObject, String expectedJson) {
        JsonBean sourceBean = JsonParser.stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualBean = sourceBean.putObject(sourcePath, sourceObject);

        JsonBean expectedBean = JsonParser.stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    private static Stream<Arguments> put_data() {
        return Stream.of(
                Arguments.of("{}", "foo", "FOO", "{'foo':'FOO'}"),
                Arguments.of("{'foo':'FOO'}", "foo", "BAR", "{'foo':'BAR'}"),
                Arguments.of("{}", "foo.bar", "FOO", "{'foo':{'bar':'FOO'}}"),
                Arguments.of("{'foo':{'bar':'FOO'}}", "foo.bar", "BAR", "{'foo':{'bar':'BAR'}}")
        );
    }

    @ParameterizedTest
    @MethodSource("remove_data")
    void remove(String sourceJson, String sourcePath, String expectedJson) {
        JsonBean sourceBean = JsonParser.stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualBean = sourceBean.remove(sourcePath);

        JsonBean expectedBean = JsonParser.stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    private static Stream<Arguments> remove_data() {
        return Stream.of(
                Arguments.of("{}", "foo", "{}"),
                Arguments.of("{'foo':'FOO'}", "foo", "{}"),
                Arguments.of("{'foo':{'bar':'FOO'}}", "foo.bar", "{'foo':{}}")
        );
    }

    @Test
    void handleString() {
        String sourceValue = "FOO";

        JsonBean actualBean = JsonBean.create().putString("foo", sourceValue);
        String actualValue = actualBean.getString("foo");

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleString_absent() {
        JsonBean sourceBean = JsonBean.create();

        String actualValue = sourceBean.getString("foo");

        Assertions.assertNull(actualValue);
    }

    @Test
    void handleNumber() {
        long sourceValue = 1L;

        JsonBean actualBean = JsonBean.create().putNumber("foo", sourceValue);
        Long actualValue = actualBean.getNumber("foo");

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleNumber_absent() {
        JsonBean sourceBean = JsonBean.create();

        Long actualValue = sourceBean.getNumber("foo");

        Assertions.assertNull(actualValue);
    }

    @Test
    void handleBoolean() {
        boolean sourceValue = true;

        JsonBean actualBean = JsonBean.create().putBoolean("foo", sourceValue);
        Boolean actualValue = actualBean.getBoolean("foo");

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleBoolean_absent() {
        JsonBean sourceBean = JsonBean.create();

        Boolean actualValue = sourceBean.getBoolean("foo");

        Assertions.assertNull(actualValue);
    }

    @Test
    void handleMap() {
        Map<String, ?> sourceValue = Map.of("bar", "BAR");

        JsonBean actualBean = JsonBean.create().putMap("foo", sourceValue);
        Map<String, Object> actualValue = actualBean.getMap("foo");

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleMap_absent() {
        JsonBean sourceBean = JsonBean.create();

        Map<String, ?> actualValue = sourceBean.getMap("foo");

        Assertions.assertNull(actualValue);
    }

    @Test
    void handleList() {
        List<String> sourceValue = List.of("FOO");

        JsonBean actualBean = JsonBean.create().putList("foo", sourceValue);
        List<Object> actualValue = actualBean.getList("foo");

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleList_absent() {
        JsonBean sourceBean = JsonBean.create();

        List<Object> actualValue = sourceBean.getList("absent");

        Assertions.assertNull(actualValue);
    }

    @Test
    void handleEntity() {
        JsonBean sourceValue = JsonBean.create()
                .putString("bar", "BAR");

        JsonBean actualBean = JsonBean.create().putEntity("foo", sourceValue);
        JsonBean actualValue = actualBean.getEntity("foo", JsonBean::create);

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void handleEntities() {
        List<JsonBean> sourceValue = JsonParser.stringToEntities("[{'bar':'BAR'}]", JsonBean::create);

        JsonBean actualBean = JsonBean.create().putEntities("foo", sourceValue);
        List<JsonBean> actualValue = actualBean.getEntities("foo", JsonBean::create);

        Assertions.assertEquals(sourceValue, actualValue);
    }

    @Test
    void mergeEntities() {
        JsonBean actualBean = JsonBean.create()
                .mergeEntity(JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create))
                .mergeEntity(JsonParser.stringToEntity("{'bar':'BAR'}", JsonBean::create));

        JsonBean expectedBean = JsonParser.stringToEntity("{'foo':'FOO','bar':'BAR'}", JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    @Test
    void factory() {
        JsonEntity.Factory sourceFactory = new JsonEntity.Factory(JsonBean.class);

        JsonBean actualEntity = (JsonBean) sourceFactory.create();

        JsonBean expectedEntity = JsonParser.stringToEntity("{}", JsonBean::create);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

}
