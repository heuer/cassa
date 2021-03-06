/*
 * Copyright 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.semagia.cassa.jaxrs.providers.GraphMismatchExceptionMapper;
import com.semagia.cassa.jaxrs.providers.GraphNotExistsExceptionMapper;
import com.semagia.cassa.jaxrs.providers.IOExceptionMapper;
import com.semagia.cassa.jaxrs.providers.IllegalArgumentExceptionMapper;
import com.semagia.cassa.jaxrs.providers.IllegalStateExceptionMapper;
import com.semagia.cassa.jaxrs.providers.NullPointerExceptionMapper;
import com.semagia.cassa.jaxrs.providers.ParseExceptionMapper;
import com.semagia.cassa.jaxrs.providers.QueryExceptionMapper;
import com.semagia.cassa.jaxrs.providers.StoreExceptionMapper;
import com.semagia.cassa.jaxrs.providers.UnsupportedMediaTypeExceptionMapper;
import com.semagia.cassa.jaxrs.providers.UnsupportedOperationExceptionMapper;

/**
 * The Cassa application.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class CassaApplication extends Application {

    /* (non-Javadoc)
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(GraphsResource.class);
        classes.add(LocalGraphResource.class);
        return classes;
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.core.Application#getSingletons()
     */
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        singletons.add(new GraphMismatchExceptionMapper());
        singletons.add(new GraphNotExistsExceptionMapper());
        singletons.add(new IllegalArgumentExceptionMapper());
        singletons.add(new IllegalStateExceptionMapper());
        singletons.add(new IOExceptionMapper());
        singletons.add(new NullPointerExceptionMapper());
        singletons.add(new ParseExceptionMapper());
        singletons.add(new QueryExceptionMapper());
        singletons.add(new StoreExceptionMapper());
        singletons.add(new UnsupportedMediaTypeExceptionMapper());
        singletons.add(new UnsupportedOperationExceptionMapper());
        return singletons;
    }

}
