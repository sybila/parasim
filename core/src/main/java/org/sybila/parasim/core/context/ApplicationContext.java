/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core.context;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.annotations.ApplicationScope;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ApplicationContext extends AbstractContext {

   
    public ApplicationContext(InstanceStorage instanceStorage) {
        super(instanceStorage);
    }

    public ApplicationContext() {
        super();
    }
    
    public Class<? extends Annotation> getScope() {
        return ApplicationScope.class;
    }
}
