/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.common;

/**
 * Common constant values useful for client/server implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IConstants {

    /**
     * Atom 1.0 namespace.
     */
    public final static String NS_ATOM = "http://www.w3.org/2005/Atom";

    /**
     * SDShare namespace.
     */
    public final static String NS_SDSHARE = "http://www.sdshare.org/2012/core/";

    /**
     * Element name which contains the resource identifier in a SDShare Atom feed.
     */
    public final static String SD_RESOURCE = "resource";

    /**
     * Element name which contains the graph identifier in a SDShare Atom feed.
     */
    public final static String SD_GRAPH = "graph";

    /**
     * Attribute value for the link type "collectionfeed"
     */
    public static final String REL_COLLECTION_FEED = NS_SDSHARE + "collectionfeed";

    /**
     * Attribute value for the link type "fragmentsfeed"
     */
    public static final String REL_FRAGMENTS_FEED = NS_SDSHARE + "fragmentsfeed";

    /**
     * Attribute value for the link type "snapshotsfeed"
     */
    public static final String REL_SNAPSHOTS_FEED = NS_SDSHARE + "snapshotsfeed";

    /**
     * Attribute value for the link type "snapshot"
     */
    public static final String REL_SNAPSHOT = NS_SDSHARE + "snapshot";

    /**
     * Attribute value for a snapshot chunk (link type "snapshot-component")
     */
    public static final String REL_SNAPSHOT_CHUNK = NS_SDSHARE + "snapshot-component";

    /**
     * Attribute value for the link type "fragment"
     */
    public static final String REL_FRAGMENT = NS_SDSHARE + "fragment";

}
