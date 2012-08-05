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
package com.semagia.cassa.server.store.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * Dummy implementation of {@link IStore}. This store is read only.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class DummyReadOnlyStore extends AbstractReadOnlyStore {
    
    public static List<MediaType> GRAPH_INFO_2_MEDIATYPES = Arrays.asList(MediaType.TURTLE, MediaType.RDF_XML);
    public static URI GRAPH_INFO_1_URI = URI.create("http://www.example.org/graph-A");
    public static URI GRAPH_INFO_2_URI = URI.create("http://www.example.org/graph-B");
    public static IGraphInfo GRAPH_INFO_1 = new DefaultGraphInfo(GRAPH_INFO_1_URI, MediaType.RDF_XML, new Date().getTime());
    public static IGraphInfo GRAPH_INFO_2 = new DefaultGraphInfo(GRAPH_INFO_2_URI,  GRAPH_INFO_2_MEDIATYPES, GRAPH_INFO_1.getLastModification() + 20);
    public static List<IGraphInfo> GRAPH_INFOS = Arrays.asList(GRAPH_INFO_1, GRAPH_INFO_2);

    @Override
    public long getLastModification() throws StoreException {
        return GRAPH_INFO_2.getLastModification();
    }

    @Override
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException {
        return GRAPH_INFOS;
    }

    @Override
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType)
            throws GraphNotExistsException, UnsupportedMediaTypeException,
            IOException, StoreException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IGraphInfo getGraphInfo(URI graphURI)
            throws GraphNotExistsException, StoreException {
        for (IGraphInfo info: GRAPH_INFOS) {
            if (info.getURI().equals(graphURI)) {
                return info;
            }
        }
        throw new GraphNotExistsException(graphURI);
    }

}
