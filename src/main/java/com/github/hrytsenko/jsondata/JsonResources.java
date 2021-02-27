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

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@UtilityClass
public class JsonResources {

    @SneakyThrows
    public static String readResource(String name) {
        try (InputStream stream = getLoader().getResourceAsStream(resolveName(name))) {
            if (stream == null) {
                throw new IOException("Resource not found");
            }
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
    }

    private static String resolveName(String name) {
        if (name.startsWith("/")) {
            return name.substring(1);
        }
        return name;
    }

    private static ClassLoader getLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                .orElseGet(JsonResources.class::getClassLoader);
    }

}
