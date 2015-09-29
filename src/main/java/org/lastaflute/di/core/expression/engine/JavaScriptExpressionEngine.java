/*
 * Copyright 2014-2015 the original author or authors.
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
package org.lastaflute.di.core.expression.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ExpressionClassCreateFailureException;
import org.lastaflute.di.core.expression.dwarf.ExpressionCastResolver;
import org.lastaflute.di.core.expression.dwarf.ExpressionCastResolver.CastResolved;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author jflute
 */
public class JavaScriptExpressionEngine implements ExpressionEngine {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String SQ = "'";
    protected static final String DQ = "\"";

    // thread-safe without e.g. put, register
    protected static final ScriptEngineManager defaultManager = new ScriptEngineManager();

    // of course thread-safe
    protected static final ExpressionCastResolver castResolver = new ExpressionCastResolver();

    // ===================================================================================
    //                                                                    Parse Expression
    //                                                                    ================
    @Override
    public Object parseExpression(String source) {
        return source.trim();
    }

    // ===================================================================================
    //                                                                            Evaluate
    //                                                                            ========
    @Override
    public Object evaluate(Object exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        return viaVariableResolvedEvaluate((String) exp, contextMap, container, resultType);
    }

    protected Object viaVariableResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
        String filteredExp = exp;
        for (Entry<String, ? extends Object> entry : contextMap.entrySet()) { // e.g. #SMART => 'cool'
            filteredExp = LdiStringUtil.replace(filteredExp, "#" + entry.getKey(), SQ + entry.getValue() + SQ);
        }
        return viaCastResolvedEvaluate(filteredExp, contextMap, container, resultType);
    }

    protected Object viaCastResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
        final CastResolved resolved = castResolver.resolveCast(exp, resultType);
        final String realExp;
        final Class<?> realType;
        if (resolved != null) {
            realExp = resolved.getFilteredExp();
            realType = resolved.getResolvedType();
        } else {
            realExp = exp.trim();
            realType = resultType;
        }
        return viaFirstNameResolvedEvaluate(realExp, contextMap, container, realType);
    }

    protected Object viaFirstNameResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
        final String filteredExp;
        String firstName = null;
        Object firstComponent = null;
        if (!exp.startsWith(DQ) && !exp.startsWith("[") && !exp.startsWith("{") && exp.contains(".")) {
            final String componentName = exp.substring(0, exp.indexOf("."));
            final LaContainer namedContainer = container.getRoot().findChild(componentName); // in all container
            if (namedContainer != null) { // first element is named container
                final String rear = exp.substring(exp.indexOf(".") + ".".length());
                if (rear.contains(".")) { // has more chain
                    final String nextName = rear.substring(0, rear.indexOf("."));
                    if (namedContainer.hasComponentDef(nextName)) { // in named container
                        filteredExp = rear;
                        firstName = nextName;
                        firstComponent = namedContainer.getComponent(nextName);
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                } else {
                    if (namedContainer.hasComponentDef(rear)) { // in named container
                        return namedContainer.getComponent(rear); // resolved without evaluation
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                }
            } else { // first element may be component
                filteredExp = exp;
                if (container.hasComponentDef(componentName)) { // in current container only
                    firstName = componentName;
                    firstComponent = container.getComponent(componentName);
                }
            }
        } else {
            filteredExp = exp;
        }
        final Object evaluated = actuallyEvaluate(filteredExp, contextMap, container, firstName, firstComponent);
        final Object filtered = filterEvaluated(filteredExp, contextMap, container, evaluated, resultType);
        // needs deep thinking time for e.g. primitive, Object.class
        //checkResultTypeMatched(filtered, contextMap, container, resultType);
        return filtered;
    }

    // ===================================================================================
    //                                                                   Actually Evaluate
    //                                                                   =================
    protected Object actuallyEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container, String firstName,
            Object firstComponent) {
        final ScriptEngine engine = prepareScriptEngineManager().getEngineByName("javascript");
        if (firstName != null) {
            engine.put(firstName, firstComponent);
        }
        try {
            return engine.eval(exp);
        } catch (ScriptException | RuntimeException e) {
            throwJavaScriptExpressionException(exp, contextMap, container, e);
            return null; // unreachable
        }
    }

    protected ScriptEngineManager prepareScriptEngineManager() {
        return defaultManager; // as default
    }

    protected void throwJavaScriptExpressionException(Object exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Exception e) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to evaluate the JavaScript expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Context Map");
        br.addElement(contextMap);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                           Filtering
    //                                                                           =========
    protected Object filterEvaluated(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Object evaluated,
            Class<?> resultType) {
        if (evaluated instanceof String) {
            // e.g. jp. cannot create the instance with this error,
            // ReferenceError: "jp" is not defined in <eval> at line number 1
            // (com. and org. can do it)
            // so you can create by quoted string expression: "new jp.dbflute.SeaLogic()"
            final String str = ((String) evaluated).trim();
            final String prefix = "new ";
            final String suffix = "()";
            if (str.startsWith(prefix) && str.endsWith(suffix)) {
                final String className = str.substring(prefix.length(), str.length() - suffix.length());
                try {
                    return LdiClassUtil.newInstance(className);
                } catch (RuntimeException e) {
                    throwExpressionClassCreateFailureException(exp, contextMap, container, className, e);
                }
            }
        }
        if (evaluated instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) evaluated;
            return handleMap(exp, contextMap, container, map, resultType);
        }
        return evaluated;
    }

    protected void throwExpressionClassCreateFailureException(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            String className, RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to create the class in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Context Map");
        br.addElement(contextMap);
        br.addItem("Class Name");
        br.addElement(className);
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }

    protected Object handleMap(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Map<String, Object> map,
            Class<?> resultType) {
        final List<Object> challengeList = challengeList(map);
        if (challengeList != null) { // e.g. [1,2] or ...
            return castResolver.convertListTo(exp, contextMap, container, resultType, challengeList);
        } else {
            return map;
        }
    }

    protected List<Object> challengeList(Map<String, Object> map) {
        int index = 0;
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (LdiStringUtil.isNumber(key) && Integer.parseInt(key) == index) {
                ++index;
                continue;
            }
            return null;
        }
        return new ArrayList<Object>(map.values());
    }

    // ===================================================================================
    //                                                                       Static Method
    //                                                                       =============
    @Override
    public String resolveStaticMethodReference(Class<?> refType, String methodName) {
        return refType.getName() + "." + methodName; // e.g. org.lastaflute.di.util.LdiResourceUtil.exists
    }
}
