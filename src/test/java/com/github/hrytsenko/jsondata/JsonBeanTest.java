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

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.hrytsenko.jsondata.JsonParser.stringToEntities;
import static com.github.hrytsenko.jsondata.JsonParser.stringToEntity;

class JsonBeanTest {

    @Test
    void create() {
        JsonBean actualBean = JsonBean.create();

        JsonBean expectedBean = stringToEntity("{}", JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    @ParameterizedTest
    @MethodSource("contains_testData")
    void contains(String sourceJson, String sourcePath, boolean expectedResult) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        boolean actualResult = sourceBean.contains(sourcePath);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    private static Stream<Arguments> contains_testData() {
        return Stream.of(
                Arguments.of("{}", "foo", false),
                Arguments.of("{'foo':null}", "foo", false),
                Arguments.of("{'foo':'FOO'}", "foo", true)
        );
    }

    @ParameterizedTest
    @MethodSource("getValue_testData")
    void getValue(String sourceJson, Function<JsonBean, Object> targetFunction, Object expectedValue) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        Object actualValue = targetFunction.apply(sourceBean);

        Assertions.assertEquals(expectedValue, actualValue);
    }

    private static Stream<Arguments> getValue_testData() {
        return Stream.of(
                Arguments.of(
                        "{'foo':'FOO'}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getString("foo"),
                        "FOO"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getString("foo"),
                        null
                ),
                Arguments.of(
                        "{'foo':true}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getBoolean("foo"),
                        true
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getBoolean("foo"),
                        null
                ),
                Arguments.of(
                        "{'foo':1}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getNumber("foo"),
                        1L
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getNumber("foo"),
                        null
                ),
                Arguments.of(
                        "{'foo':{}}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getMap("foo"),
                        Collections.emptyMap()
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getMap("foo"),
                        null
                ),
                Arguments.of(
                        "{'foo':[]}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getList("foo"),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getList("foo"),
                        null
                ),
                Arguments.of(
                        "{'foo':{'bar':'BAR'}}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getEntity("foo", JsonBean::create),
                        stringToEntity("{'bar':'BAR'}", JsonBean::create)
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getEntity("foo", JsonBean::create),
                        null
                ),
                Arguments.of(
                        "{'foo':[{'bar':'BAR'}]}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getEntities("foo", JsonBean::create),
                        stringToEntities("[{'bar':'BAR'}]", JsonBean::create)
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, Object>) bean ->
                            bean.getEntities("foo", JsonBean::create),
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("putValue_testData")
    void putValue(String sourceJson, Function<JsonBean, JsonBean> targetFunction, String expectedJson) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualBean = targetFunction.apply(sourceBean);

        JsonBean expectedBean = stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    private static Stream<Arguments> putValue_testData() {
        return Stream.of(
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putString("foo", "FOO"),
                        "{'foo':'FOO'}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putString("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putNumber("foo", 1L),
                        "{'foo':1}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putNumber("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putBoolean("foo", true),
                        "{'foo':true}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putBoolean("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putMap("foo", Collections.emptyMap()),
                        "{'foo':{}}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putMap("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putList("foo", Collections.emptyList()),
                        "{'foo':[]}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putList("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putEntity("foo", JsonBean.create()),
                        "{'foo':{}}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putEntity("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putEntities("foo", Collections.singletonList(JsonBean.create())),
                        "{'foo':[{}]}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.putEntities("foo", null),
                        "{'foo':null}"
                ),
                Arguments.of(
                        "{}",
                        (Function<JsonBean, JsonBean>) bean ->
                            bean.mergeEntity(stringToEntity("{'foo':'FOO'}", JsonBean::create)),
                        "{'foo':'FOO'}"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getObject_testData")
    void getObject(String sourceJson, String sourcePath, Object expectedObject) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        Object actualObject = sourceBean.getObject(sourcePath);

        Assertions.assertEquals(expectedObject, actualObject);
    }

    private static Stream<Arguments> getObject_testData() {
        return Stream.of(
                Arguments.of("{}", "foo", null),
                Arguments.of("{'foo':'FOO'}", "foo", "FOO"),
                Arguments.of("{'foo':{'bar':'BAR'}}", "foo.bar", "BAR")
        );
    }

    @ParameterizedTest
    @MethodSource("putObject_testData")
    void putObject(String sourceJson, String sourcePath, Object sourceObject, String expectedJson) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualBean = sourceBean.putObject(sourcePath, sourceObject);

        JsonBean expectedBean = stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    private static Stream<Arguments> putObject_testData() {
        return Stream.of(
                Arguments.of("{}", "foo", null, "{'foo':null}"),
                Arguments.of("{}", "foo", "FOO", "{'foo':'FOO'}"),
                Arguments.of("{'foo':'FOO'}", "foo", "BAR", "{'foo':'BAR'}"),
                Arguments.of("{}", "foo.bar", "FOO", "{'foo':{'bar':'FOO'}}"),
                Arguments.of("{'foo':{'bar':'FOO'}}", "foo.bar", "BAR", "{'foo':{'bar':'BAR'}}")
        );
    }

    @ParameterizedTest
    @MethodSource("remove_testData")
    void remove(String sourceJson, String sourcePath, String expectedJson) {
        JsonBean sourceBean = stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualBean = sourceBean.remove(sourcePath);

        JsonBean expectedBean = stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedBean, actualBean);
    }

    private static Stream<Arguments> remove_testData() {
        return Stream.of(
                Arguments.of("{}", "foo", "{}"),
                Arguments.of("{'foo':'FOO'}", "foo", "{}"),
                Arguments.of("{'foo':{'bar':'FOO'}}", "foo.bar", "{'foo':{}}")
        );
    }

}
