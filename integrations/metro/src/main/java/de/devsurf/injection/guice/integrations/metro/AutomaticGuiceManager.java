/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devsurf.injection.guice.integrations.metro;

import com.google.inject.Injector;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.ResourceInjector;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;

public abstract class AutomaticGuiceManager<T> extends AbstractMultiInstanceResolver<T> {
	protected static Injector injector;
	protected ResourceInjector resourceInjector;
	protected WSWebServiceContext webServiceContext;

	public AutomaticGuiceManager(@NotNull final Class<T> clazz) throws IllegalAccessException,
			InstantiationException {
		super(clazz);
	}

	@Override
	public T resolve(@NotNull final Packet packet) {
		final T instance = injector.getInstance(this.clazz);
		resourceInjector.inject(this.webServiceContext, instance);
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
		resourceInjector = getResourceInjector(endpoint);
		webServiceContext = wsc;
		if (injector == null) {
			injector = getInjector();
		}
	}
	
	protected abstract Injector getInjector();
}
