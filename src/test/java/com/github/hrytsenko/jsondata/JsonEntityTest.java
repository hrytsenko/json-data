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

import java.util.Map;

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

    @Test
    void equals() {
        TestEntity thisEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);
        TestEntity otherEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);

        Assertions.assertEquals(thisEntity, thisEntity);
        Assertions.assertEquals(thisEntity, otherEntity);
    }

    @Test
    void notEquals() {
        TestEntity thisEntity = JsonParser.stringToEntity("{'foo':'FOO'}", TestEntity::new);
        TestEntity otherEntity = JsonParser.stringToEntity("{'foo':'BAR'}", TestEntity::new);

        Assertions.assertNotEquals(thisEntity, new Object());
        Assertions.assertNotEquals(thisEntity, otherEntity);
    }

    @Test
    void factory() {
        JsonEntity.Factory sourceFactory = new JsonEntity.Factory(TestEntity.class);

        TestEntity actualEntity = (TestEntity) sourceFactory.create();

        TestEntity expectedEntity = JsonParser.stringToEntity("{}", TestEntity::new);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

}
