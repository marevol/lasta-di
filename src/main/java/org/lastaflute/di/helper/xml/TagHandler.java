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
package org.lastaflute.di.helper.xml;

import java.io.Serializable;

import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TagHandler implements Serializable {

    static final long serialVersionUID = 1L;

    public void start(TagHandlerContext context, Attributes attributes) {
    }

    public void appendBody(TagHandlerContext context, String body) {
    }

    public void end(TagHandlerContext context, String body) {
    }
}
