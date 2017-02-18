/*
 * Copyright 2015-2017 the original author or authors.
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
package org.lastaflute.di.helper.message;

/**
 * @author modified by jflute (originated in Struts)
 */
public interface MessageResourceBundle {

    /**
     * @param key The key of the message to find. (NullAllowed: if null, returns null)
     * @return The found message from the bundle. (NullAllowed: when e.g. not found)
     */
    String get(String key);

    MessageResourceBundle getParent();

    void setParent(MessageResourceBundle parent);
}
