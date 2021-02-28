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

import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * <p>This class provides utilities to wrap exceptions for {@link JsonParser}, {@link JsonMapper} and {@link JsonValidator}.
 */
@UtilityClass
class JsonExceptions {

    static void wrap(Runnable runnable, Function<Exception, ? extends RuntimeException> wrapper) {
        try {
            runnable.run();
        } catch (Exception exception) {
            throw wrapper.apply(exception);
        }
    }

    static <R> R wrap(Callable<R> callable, Function<Exception, ? extends RuntimeException> wrapper) {
        try {
            return callable.call();
        } catch (Exception exception) {
            throw wrapper.apply(exception);
        }
    }

}
