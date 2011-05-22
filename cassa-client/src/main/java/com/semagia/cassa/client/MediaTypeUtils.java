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
package com.semagia.cassa.client;

import java.net.URI;

import com.semagia.cassa.common.MediaType;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class MediaTypeUtils {

    public static MediaType guessMediaType(final URI uri) {
        final String uri_ = uri.toString();
        final int dotIdx = uri_.lastIndexOf('.');
        final String ext = dotIdx > -1 ? uri_.substring(dotIdx+1).toLowerCase() : null;
        if (ext != null) {
            if (ext.equals("xtm")) {
                return MediaType.XTM;
            }
            else if (ext.equals("rdf")) {
                return MediaType.RDF_XML;
            }
        }
        return null;
    }
}
