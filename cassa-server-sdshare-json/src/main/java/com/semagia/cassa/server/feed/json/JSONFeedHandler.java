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
package com.semagia.cassa.server.feed.json;

import java.io.IOException;
import java.io.OutputStream;

import com.semagia.cassa.common.DateTimeUtils;
import com.semagia.cassa.server.sdshare.IOutputAwareFeedHandler;

/**
 * {@link IOutputAwareFeedHandler} implementation that writes JSON.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class JSONFeedHandler implements IOutputAwareFeedHandler {

    private JSONWriter _writer;

    private boolean _inEntries;
    private boolean _inLinks;
    private boolean _inAuthors;
    private boolean _inSIDs;
    private boolean _wroteSIDs;
    private boolean _wroteFeedLinks;
    private boolean _wroteEntryLinks;
    private boolean _wroteFeedAuthors;
    private boolean _wroteEntryAuthors;

    public JSONFeedHandler() {
        // noop.
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IOutputAwareFeedHandler#init(java.io.OutputStream)
     */
    @Override
    public void init(OutputStream out) throws IOException {
        _writer = new JSONWriter(out, "utf-8");
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#startFeed(java.lang.String, java.lang.String, long)
     */
    @Override
    public void startFeed(final String id, final String title, final long updated) throws IOException {
        _writer.startDocument();
        _writer.startObject();
        _writeCommons(id, title, updated);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#endFeed()
     */
    @Override
    public void endFeed() throws IOException {
        _finishPendingArray();
        if (_inEntries) {
            _writer.endArray();
        }
        _writer.endObject(); // feed
        _writer.endDocument();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#startEntry(java.lang.String, java.lang.String, long)
     */
    @Override
    public void startEntry(String id, String title, long updated) throws IOException {
        if (!_inEntries) {
            _finishPendingArray();  // Finish feed authors/links if any
            _inEntries = true;
            _writer.key("entries");
            _writer.startArray();
        }
        _writer.startObject();
        _wroteEntryLinks = false;
        _wroteEntryAuthors = false;
        _writeCommons(id, title, updated);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#endEntry()
     */
    @Override
    public void endEntry() throws IOException {
        _finishPendingArray();
        _writer.endObject(); // entry
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IOutputAwareFeedHandler#author(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void author(String name, String email, String iri)
            throws IOException {
        if (!_inAuthors) {
            _finishPendingArray();
            if (_wroteEntryAuthors || (!_inEntries && _wroteFeedAuthors)) {
                throw new IllegalStateException("Internal error: 'authors' array was already written");
            }
            _inAuthors = true;
            _writer.key("authors");
            _writer.startArray();
        }
        _writer.startObject();
        _writer.keyValue("name", name);
        if (email != null) {
            _writer.keyValue("email", email);
        }
        if (iri != null) {
            _writer.keyValue("uri", iri);
        }
        _writer.endObject();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IOutputAwareFeedHandler#generator(java.lang.String, java.lang.String)
     */
    @Override
    public void generator(final String name, final String iri) throws IOException {
        _finishPendingArray();
        _writer.key("generator");
        _writer.startObject();
        _writer.keyValue("value", name);
        if (iri != null) {
            _writer.keyValue("uri", iri);
        }
        _writer.endObject();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#link(java.lang.String, java.lang.String)
     */
    @Override
    public void link(String href, String relation) throws IOException {
        link(href, relation, null);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#link(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void link(String href, String relation, String mediaType) throws IOException {
        if (!_inLinks) {
            _finishPendingArray();
            if (_wroteEntryLinks || (!_inEntries && _wroteFeedLinks)) {
                throw new IllegalStateException("Internal error: 'links' array was already written");
            }
            _inLinks = true;
            _writer.key("links");
            _writer.startArray();
        }
        _writer.startObject();
        _writer.keyValue("href", href);
        if (relation != null) {
            _writer.keyValue("rel", relation);
        }
        if (mediaType != null) {
            _writer.keyValue("type", mediaType);
        }
        _writer.endObject();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IOutputAwareFeedHandler#summary(java.lang.String)
     */
    @Override
    public void summary(String summary) throws IOException {
        _finishPendingArray();
        _writer.keyValue("summary", summary);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#resource(java.lang.String)
     */
    @Override
    public void resource(final String iri, final String role) throws IOException {
        if (!_inSIDs) {
            _finishPendingArray();
            if (_wroteSIDs) {
                throw new IllegalStateException("Internal error: 'resource' array was already written");
            }
            _inSIDs = true;
            _writer.key("[sd:resource]");
            _writer.startArray();
        }
        _writer.startObject();
        _writer.keyValue("iri", iri);
        if (role != null) {
            _writer.keyValue("role", role);
        }
        _writer.endObject();
    }

    private void _writeCommons(final String id, final String title, final long updated) throws IOException {
        _writer.keyValue("id", id);
        _writer.keyValue("title", title);
        _writer.keyValue("updated", DateTimeUtils.toISO8601Date(updated));
    }

    /** 
     * Finishes an author/links array if necessary.
     *
     * @throws IOException In case of an error.
     */
    private void _finishPendingArray() throws IOException {
        if (_inLinks) {
            _writer.endArray();
            if (_inEntries) {
                _wroteEntryLinks = true;
            }
            else {
                _wroteFeedLinks = true;
            }
            _inLinks = false;
        }
        if (_inAuthors) {
            _writer.endArray();
            if (_inEntries) {
                _wroteEntryAuthors = true;
            }
            else {
                _wroteFeedAuthors = true;
            }
            _inAuthors = false;
        }
        if (_inSIDs) {
            _writer.endArray();
            _wroteSIDs = true;
        }
    }

}
