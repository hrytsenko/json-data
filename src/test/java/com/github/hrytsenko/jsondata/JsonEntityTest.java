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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class JsonEntityTest {

    static class TestEntity extends JsonEntity<TestEntity> {
    }

    @Test
    void asMap() {
        TestEntity sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);

        Map<String, Object> actualMap = sourceEntity.asMap();

        Map<String, ?> expectedMap = JsonParser.stringToMap("{'foo':'FOO'}");
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @Test
    void asEntity() {
        TestEntity sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);

        JsonBean actualEntity = sourceEntity.as(JsonBean::create);

        JsonBean expectedEntity = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    void asString() {
        TestEntity sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);

        String actualJson = sourceEntity.toString();

        String expectedJson = "{\"foo\":\"FOO\"}";
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @ParameterizedTest
    @MethodSource("equals_testData")
    void equals(Object sourceLeft, Object sourceRight, boolean expectedResult) {
        boolean actualResult = Objects.equals(sourceLeft, sourceRight);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    private static Stream<Arguments> equals_testData() {
        return Stream.of(
                Arguments.of(
                        JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new),
                        JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new),
                        true
                ),
                Arguments.of(
                        JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new),
                        JsonParser.stringToEntity("{'foo':'BAR'}", TestEntity::new),
                        false
                ),
                Arguments.of(
                        JsonParser.stringToEntity("{}", TestEntity::new),
                        Map.of(),
                        false
                ),
                Arguments.of(
                        JsonParser.stringToEntity("{}", TestEntity::new),
                        null,
                        false
                )
        );
    }

    @Test
    void factory_create() {
        JsonEntity.Factory sourceFactory = new JsonEntity.Factory(TestEntity.class);

        TestEntity actualEntity = (TestEntity) sourceFactory.create();

        TestEntity expectedEntity = JsonParser.stringToEntity("{}", TestEntity::new);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    void factory_createFromMap() {
        JsonEntity.Factory sourceFactory = new JsonEntity.Factory(TestEntity.class);

        TestEntity actualEntity = (TestEntity) sourceFactory.createFromMap(Map.of("foo", "FOO"));

        TestEntity expectedEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    static class EntityWithInvalidConstructor extends JsonEntity<EntityWithInvalidConstructor> {
        EntityWithInvalidConstructor(String any) {
        }
    }

    @Test
    void factory_create_invalidConstructor() {
        Assertions.assertThrows(
                NoSuchMethodException.class,
                () -> new JsonEntity.Factory(EntityWithInvalidConstructor.class));
    }

    static class EntityWithFaultyConstructor extends JsonEntity<EntityWithFaultyConstructor> {
        EntityWithFaultyConstructor() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void factory_create_faultyConstructor() {
        JsonEntity.Factory sourceFactory = new JsonEntity.Factory(EntityWithFaultyConstructor.class);

        Assertions.assertThrows(
                InvocationTargetException.class,
                sourceFactory::create);
    }

}
