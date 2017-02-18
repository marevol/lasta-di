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
package org.lastaflute.di.core.creator;

import org.lastaflute.di.core.customizer.ComponentCustomizer;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.naming.NamingConvention;

/**
 * @author modified by jflute (originated in Seasar)
 * @author vestige
 *  
 */
public class ServiceCreator extends ComponentCreatorImpl {

    /**
     * @param namingConvention
     */
    public ServiceCreator(NamingConvention namingConvention) {
        super(namingConvention);
        setNameSuffix(namingConvention.getServiceSuffix());
        setInstanceDef(InstanceDefFactory.PROTOTYPE);
        setEnableInterface(true);
        setEnableAbstract(true);
    }

    /**
     * @return 
     */
    public ComponentCustomizer getServiceCustomizer() {
        return getCustomizer();
    }

    /**
     * @param customizer 
     */
    public void setServiceCustomizer(ComponentCustomizer customizer) {
        setCustomizer(customizer);
    }
}