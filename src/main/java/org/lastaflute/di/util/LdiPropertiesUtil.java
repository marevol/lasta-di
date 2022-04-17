/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.di.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiPropertiesUtil {

    protected LdiPropertiesUtil() {
    }

    public static void load(Properties props, InputStream ins) throws IORuntimeException {
        try {
            props.load(ins);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void load(Properties props, Reader reader) throws IORuntimeException {
        try {
            props.load(reader);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
