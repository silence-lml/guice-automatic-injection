/*******************************************************************************
 * Copyright 2010, Daniel Manzke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 * 
 ******************************************************************************/
package de.devsurf.injection.guice.scanner.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.inject.Qualifier;

import com.google.inject.Module;

import de.devsurf.injection.guice.scanner.GuiceAnnotationListener;

/**
 * Annotate a Module with the GuiceModule-Annotation and it will be installed
 * automatically.
 * 
 * @author Daniel Manzke
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface GuiceModule {
    public class GuiceModuleListener extends GuiceAnnotationListener {
	@Override
	public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	    if (annotations.containsKey(GuiceModule.class.getName())) {
		try {
		    synchronized (_binder) {
			_binder.install((Module) annotatedClass.newInstance());
		    }
		} catch (InstantiationException e) {
		    e.printStackTrace();
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}