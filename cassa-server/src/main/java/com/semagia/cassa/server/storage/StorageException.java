/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.server.storage;

import java.io.IOException;

/**
 * Exception thrown if an error happens within a {@link IStore} instance. 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@SuppressWarnings("serial")
public class StorageException extends IOException {

    /**
     * Constructs a <tt>StorageException</tt> with the specified detail message.
     *
     * @param msg The detail message.
     */
    public StorageException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <tt>StorageException</tt> with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public StorageException(Throwable cause) {
        super();
        initCause(cause);
    }

    /**
     * Constructs a <tt>StorageException</tt> with the specified message and 
     * cause.
     *
     * @param msg The detail message.
     * @param cause The cause of the exception.
     */
    public StorageException(String msg, Throwable cause) {
        this(msg);
        initCause(cause);
    }

}
