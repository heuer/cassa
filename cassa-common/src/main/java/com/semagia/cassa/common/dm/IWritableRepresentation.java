/*
 * Copyright 2008 - 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.common.dm;

import java.io.IOException;
import java.io.OutputStream;

import com.semagia.cassa.common.MediaType;

/**
 * A representation which can be serialized.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IWritableRepresentation {

    /**
     * Writes this instance to the specified {@link OutputStream}.
     *
     * @param out The stream to write to.
     * @throws IOException In case of an error.
     */
    public void write(OutputStream out) throws IOException;

    /**
     * Returns the media type of this representation.
     *
     * @return The media type, never {@code null}.
     */
    public MediaType getMediaType();

    /**
     * Returns the encoding or {@code null} if the encoding is unknown.
     * 
     * @return The encoding (i.e ."UTF-8") or {@code null}.
     */
    public String getEncoding();

}
