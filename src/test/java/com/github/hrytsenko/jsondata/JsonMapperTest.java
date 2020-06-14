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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class JsonMapperTest {

    @ParameterizedTest
    @MethodSource("mapEntity_data")
    void mapEntity(String sourceSchema, String sourceJson, String expectedJson) {
        JsonMapper<JsonBean> sourceMapper = JsonMapper.create(sourceSchema, JsonBean::create);
        JsonBean sourceEntity = JsonParser.stringToEntity(sourceJson, JsonBean::create);

        JsonBean actualEntity = sourceMapper.map(sourceEntity);

        JsonBean expectedEntity = JsonParser.stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    private static Stream<Arguments> mapEntity_data() {
        return Stream.of(
                Arguments.of(
                        "[{'operation':'shift','spec':{'foo':'bar'}},{'operation':'default','spec':{}}]",
                        "{}",
                        "{}"
                ),
                Arguments.of(
                        "[{'operation':'shift','spec':{'foo':'bar'}}]",
                        "{'foo':'FOO'}",
                        "{'bar':'FOO'}"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("mapEntities_data")
    void mapEntities(String sourceSchema, String sourceJson, String expectedJson) {
        JsonMapper<JsonBean> sourceMapper = JsonMapper.create(sourceSchema, JsonBean::create);
        List<JsonBean> sourceEntity = JsonParser.stringToEntities(sourceJson, JsonBean::create);

        JsonBean actualEntity = sourceMapper.map(sourceEntity);

        JsonBean expectedEntity = JsonParser.stringToEntity(expectedJson, JsonBean::create);
        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    private static Stream<Arguments> mapEntities_data() {
        return Stream.of(
                Arguments.of(
                        "[{'operation':'shift','spec':{'*':{'foo':'bar[]'}}},{'operation':'default','spec':{}}]",
                        "[]",
                        "{}"
                ),
                Arguments.of(
                        "[{'operation':'shift','spec':{'*':{'foo':'bar[]'}}}]",
                        "[{'foo':'FOO'}]",
                        "{'bar':['FOO']}"
                )
        );
    }

}
