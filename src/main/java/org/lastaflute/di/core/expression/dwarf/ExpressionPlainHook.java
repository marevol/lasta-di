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
package org.lastaflute.di.core.expression.dwarf;

import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.util.LdiResourceUtil;

/**
 * @author jflute
 */
public interface ExpressionPlainHook {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    String DQ = "\"";
    String SQ = "'";
    String EXISTS_BEGIN = LdiResourceUtil.class.getName() + ".exists('";
    String EXISTS_END = "')";
    String TYPE_BEGIN = "@"; // compatible with OGNL e.g. @org.dbflute.Entity@class, and for minor domain
    String TYPE_END = "@"; // me too
    String TYPE_END_CLASS = TYPE_END + "class"; // me too
    String METHOD_MARK = "()";

    // e.g. provider.config().getJdbcUrl()
    String PROVIDER_CONFIG = "provider.config()";
    String PROVIDER_GET = PROVIDER_CONFIG + ".get";

    // e.g. provider.config().getOrDefault("jdbc.connection.pooling.min.size", null)
    String ORDEFAULT_METHOD_NAME = "getOrDefault";
    String ORDEFAULT_PROVIDER_GET = PROVIDER_CONFIG + "." + ORDEFAULT_METHOD_NAME;
    String ORDEFAULT_BEGIN = ORDEFAULT_METHOD_NAME + "(\"";
    String ORDEFAULT_END = "\", null)"; // unsupported if valid default value and no space e.g. ",null" for now

    Object NULL_RETURN = new Object();

    Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType);

    static Object resolveHookedReturn(Object hooked) {
        return NULL_RETURN.equals(hooked) ? null : hooked;
    }
}
