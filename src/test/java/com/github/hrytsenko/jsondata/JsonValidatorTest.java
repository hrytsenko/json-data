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
import org.leadpony.justify.api.JsonValidatingException;

import java.util.List;

class JsonValidatorTest {

    @Test
    void validateEntity() {
        String sourceSchema = "{\"properties\":{\"foo\":{\"enum\":[\"FOO\"]}},\"required\":[\"foo\"]}";
        JsonBean sourceEntity = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);

        JsonValidator.create(sourceSchema).validate(sourceEntity);
    }

    @Test
    void validateEntity_failure() {
        String sourceSchema = "{\"properties\":{\"foo\":{\"enum\":[\"FOO\"]}},\"required\":[\"foo\"]}";
        JsonBean sourceEntity = JsonParser.stringToEntity("{'foo':'BAR'}", JsonBean::create);

        Assertions.assertThrows(JsonValidatingException.class,
                () -> JsonValidator.create(sourceSchema).validate(sourceEntity));
    }

    @Test
    void validateEntities() {
        String sourceSchema = "{\"type\":\"array\",\"items\":{\"properties\":{\"foo\":{\"enum\":[\"FOO\"]}},\"required\":[\"foo\"]}}";
        List<JsonBean> sourceEntity = JsonParser.stringToEntities("[{'foo':'FOO'}]", JsonBean::create);

        JsonValidator.create(sourceSchema).validate(sourceEntity);
    }

}
