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
package com.semagia.cassa.common.dm;

import java.net.URI;

/**
 * Represents a resource.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IResource {

    /**
     * Role of a resource.
     * 
     * {@link #NONE} indicates that the resource IRI has no Topic Maps specific
     * identity. A Topic Maps client may interpret the IRI as subject identifier
     * then. Or use the <a href="http://www.w3.org/2001/tag/issues.html#httpRange-14">httpRange-14</a> 
     * solution to decide whether the IRI should be interpreted as subject 
     * identifier or subject locator.
     * 
     */
    public enum Role {
        NONE, SUBJECT_IDENTIFIER, SUBJECT_LOCATOR, ITEM_IDENTIFIER;

        /**
         * Returns the string value of the role.
         * 
         * If the role is {@link #NONE}, an empty string is returned.
         */
        @Override
        public String toString() {
            return this == NONE ? "" : super.toString().toLowerCase().replace('_','-');
        }
    }

    /**
     * Returns the URI of this resource.
     * 
     * @return The URI of the resource, never {@code null}.
     */
    public URI getURI();

    /**
     * Returns the resource's role.
     * 
     * @return The role of the resource.
     */
    public Role getRole();

}
