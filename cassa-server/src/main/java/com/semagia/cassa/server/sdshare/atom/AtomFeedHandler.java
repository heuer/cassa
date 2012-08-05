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
package com.semagia.cassa.server.sdshare.atom;

import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.helpers.AttributesImpl;

import com.semagia.cassa.common.DateTimeUtils;
import com.semagia.cassa.common.IConstants;
import com.semagia.cassa.server.sdshare.IOutputAwareFeedHandler;

/**
 * {@link IOutputAwareFeedHandler} implementation that writes Atom.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class AtomFeedHandler implements IOutputAwareFeedHandler {

    private static final String _PREFIX_SDSHARE = "sd";
    private XMLWriter _writer;
    private final AttributesImpl _attrs;

    public AtomFeedHandler() {
        _attrs = new AttributesImpl();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IOutputAwareFeedHandler#init(java.io.OutputStream)
     */
    @Override
    public void init(final OutputStream out) throws IOException {
        _writer = new XMLWriter(out);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#startFeed(java.lang.String, java.lang.String, long)
     */
    @Override
    public void startFeed(final String id, final String title, final long updated) throws IOException {
        _writer.startDocument();
        _attrs.clear();
        _attrs.addAttribute("", "xmlns", "", "CDATA", IConstants.NS_ATOM);
        _attrs.addAttribute("", "xmlns:" + _PREFIX_SDSHARE, "", "CDATA", IConstants.NS_SDSHARE);
        _writer.startElement("feed", _attrs);
        _writeCommons(id, title, updated);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#endFeed()
     */
    @Override
    public void endFeed() throws IOException {
        _writer.endElement("feed");
        _writer.endDocument();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#startEntry(java.lang.String, java.lang.String, long)
     */
    @Override
    public void startEntry(final String id, final String title, final long updated) throws IOException {
        _writer.startElement("entry");
        _writeCommons(id, title, updated);
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.feed.IFeedHandler#endEntry()
     */
    @Override
    public void endEntry() throws IOException {
        _writer.endElement("entry");
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#author(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void author(final String name, final String email, final String iri) throws IOException {
        _writer.startElement("author");
        _writer.dataElement("name", name);
        if (email != null) {
            _writer.dataElement("email", email);
        }
        if (iri != null) {
            _writer.dataElement("uri", iri);
        }
        _writer.endElement("author");
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#generator(java.lang.String, java.lang.String)
     */
    @Override
    public void generator(final String name, final String iri) throws IOException {
        _attrs.clear();
        if (iri != null) {
            _attrs.addAttribute("", "uri", null, "CDATA", iri);
        }
        _writer.dataElement("generator", _attrs, name);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#link(java.lang.String, java.lang.String)
     */
    @Override
    public void link(final String href, final String relation) throws IOException {
        link(href, relation, null);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#link(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void link(final String href, final String relation, final String mediaType) throws IOException {
        _attrs.clear();
        if (relation != null) {
            _attrs.addAttribute("", "rel", "", "CDATA", relation);
        }
        if (mediaType != null) {
            _attrs.addAttribute("", "type", "", "CDATA", mediaType);
        }
        _attrs.addAttribute("", "href", "", "CDATA", href);
        _writer.emptyElement("link", _attrs);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#summary(java.lang.String)
     */
    @Override
    public void summary(final String summary) throws IOException {
        _writer.dataElement("summary", summary);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#graph(java.lang.String)
     */
    @Override
    public void graph(final String iri) throws IOException {
        _writer.dataElement(_PREFIX_SDSHARE + ":graph", iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.sdshare.IFeedHandler#resource(java.lang.String, java.lang.String)
     */
    @Override
    public void resource(final String iri, final String role) throws IOException {
        _attrs.clear();
        if (role != null) {
            _attrs.addAttribute("", "role", "", "CDATA", role);
        }
        _writer.dataElement(_PREFIX_SDSHARE + ":resource", _attrs, iri);
    }

    private void _writeCommons(final String id, final String title, final long updated) throws IOException {
        _writer.dataElement("id", id);
        _writer.dataElement("title", title);
        _writer.dataElement("updated", DateTimeUtils.toISO8601Date(updated));
    }
}
