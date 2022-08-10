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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonParserTest {

    @Test
    void stringToMap() {
        String sourceJson = "{'foo':'FOO'}";

        Map<String, ?> actualMap = JsonParser.stringToMap(sourceJson);

        Map<String, ?> expectedMap = Collections.singletonMap("foo", "FOO");
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @Test
    void stringToMap_deserializationFailed() {
        String sourceJson = "[]";

        Assertions.assertThrows(
                JsonParserException.class,
                () -> JsonParser.stringToMap(sourceJson));
    }

    @Test
    void stringToList() {
        String sourceJson = "[{'foo':'FOO'}]";

        List<Map<String, ?>> actualList = JsonParser.stringToList(sourceJson);

        List<Map<String, ?>> expectedList = Collections.singletonList(Collections.singletonMap("foo", "FOO"));
        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void stringToList_deserializationFailed() {
        String sourceJson = "{}";

        Assertions.assertThrows(
                JsonParserException.class,
                () -> JsonParser.stringToList(sourceJson));
    }

    @Test
    void stringToEntity() {
        String sourceJson = "{'foo':'FOO'}";

        JsonBean actualEntity = JsonParser.stringToEntity(sourceJson, JsonBean::create);

        JsonBean expectedEntity = JsonBean.create().fromMap(Collections.singletonMap("foo", "FOO"));
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    void stringToEntities() {
        String sourceJson = "[{'foo':'FOO'}]";

        List<JsonBean> actualEntities = JsonParser.stringToEntities(sourceJson, JsonBean::create);

        List<JsonBean> expectedEntities = Collections.singletonList(
                JsonBean.create().fromMap(Collections.singletonMap("foo", "FOO")));
        Assertions.assertEquals(expectedEntities, actualEntities);
    }

    @Test
    void mapToEntity() {
        Map<String, ?> sourceMap = JsonParser.stringToMap("{'foo':'FOO'}");

        JsonBean actualEntity = JsonParser.mapToEntity(sourceMap, JsonBean::create);

        JsonBean expectedEntity = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @ParameterizedTest
    @MethodSource("listToEntities_testData")
    void listToEntities(List<Map<String, ?>> sourceList, List<JsonBean> expectedEntities) {
        List<JsonBean> actualEntities = JsonParser.listToEntities(sourceList, JsonBean::create);

        Assertions.assertEquals(expectedEntities, actualEntities);
    }

    private static Stream<Arguments> listToEntities_testData() {
        return Stream.of(
                Arguments.of(
                        JsonParser.stringToList("[{'foo':'FOO'}]"),
                        JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create)
                ),
                Arguments.of(
                        JsonParser.stringToList("[null]"),
                        JsonParser.stringToEntities("[null]", JsonBean::create)
                )
        );
    }

    @Test
    void entityToMap() {
        JsonBean sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);

        Map<String, ?> actualMap = JsonParser.entityToMap(sourceEntity);

        Map<String, ?> expectedMap = Collections.singletonMap("foo", "FOO");
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @ParameterizedTest
    @MethodSource("entitiesToList_testData")
    void entitiesToList(List<JsonBean> sourceEntities, List<Map<String, ?>> expectedList) {
        List<Map<String, ?>> actualList = JsonParser.entitiesToList(sourceEntities);

        Assertions.assertEquals(expectedList, actualList);
    }

    private static Stream<Arguments> entitiesToList_testData() {
        return Stream.of(
                Arguments.of(
                        JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create),
                        JsonParser.stringToList("[{'foo':'FOO'}]")
                ),
                Arguments.of(
                        JsonParser.stringToEntities("[null]", JsonBean::create),
                        JsonParser.stringToList("[null]")
                )
        );
    }

    @Test
    void mapToString() {
        Map<String, ?> sourceMap = Collections.singletonMap("foo", "FOO");

        String actualJson = JsonParser.mapToString(sourceMap);

        String expectedJson = "{\"foo\":\"FOO\"}";
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    void mapToString_serializationFailed() {
        Map<String, ?> sourceMap = Collections.singletonMap("foo", new Object());

        Assertions.assertThrows(
                JsonParserException.class,
                () -> JsonParser.mapToString(sourceMap));
    }

    @Test
    void listToString() {
        List<Map<String, ?>> sourceList = Collections.singletonList(
                Collections.singletonMap("foo", "FOO"));

        String actualJson = JsonParser.listToString(sourceList);

        String expectedJson = "[{\"foo\":\"FOO\"}]";
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    void listToString_serializationFailed() {
        List<Map<String, ?>> sourceList = Collections.singletonList(
                Collections.singletonMap("foo", new Object()));

        Assertions.assertThrows(
                JsonParserException.class,
                () -> JsonParser.listToString(sourceList));
    }

    @Test
    void entityToString() {
        JsonBean sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);

        String actualJson = JsonParser.entityToString(sourceEntity);

        String expectedJson = "{\"foo\":\"FOO\"}";
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    void entitiesToString() {
        List<JsonBean> sourceEntities = JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create);

        String actualJson = JsonParser.entitiesToString(sourceEntities);

        String expectedJson = "[{\"foo\":\"FOO\"}]";
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    void mapToEntity_stream() {
        List<Map<String, ?>> sourceList = JsonParser.stringToList("[{'foo':'FOO'}]");

        List<JsonBean> actualEntities = sourceList.stream()
                .map(JsonParser.fromMapTo(JsonBean::create))
                .collect(Collectors.toList());

        List<JsonBean> expectedEntities = JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create);
        Assertions.assertEquals(expectedEntities, actualEntities);
    }

    @Test
    void entityToEntity_stream() {
        List<JsonBean> sourceList = JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create);

        List<JsonBean> actualEntities = sourceList.stream()
                .map(JsonParser.fromEntityTo(JsonBean::create))
                .collect(Collectors.toList());

        List<JsonBean> expectedEntities = JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create);
        Assertions.assertEquals(expectedEntities, actualEntities);
    }

}
