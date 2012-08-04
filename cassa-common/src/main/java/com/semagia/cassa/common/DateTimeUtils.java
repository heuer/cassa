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
package com.semagia.cassa.common;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for date/time values.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class DateTimeUtils {

    // This code was taken and adapted from the Apache Abdera project
    // http://abdera.apache.org/ licensed under the Apache License Version 2.0

    /**
     * Pattern to match a date in the Atom / ISO 8601 format.
     */
    private static final Pattern _PATTERN = Pattern.compile("(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:([Tt])?(?:(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?(?:\\.(\\d{3}))?)?([Zz])?(?:([+-])(\\d{2}):(\\d{2}))?");

    private DateTimeUtils() {
        // noop.
    }

    /**
     * Coverts the specified <tt>time</tt> into a string which represents
     * the lexical form acc. to ISO 8601.
     *
     * @param time The time to convert.
     * @return A lexical representation of the time.
     */
    public static String toISO8601Date(final long time) {
        final StringBuffer sb = new StringBuffer(19);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        sb.append(cal.get(Calendar.YEAR));
        sb.append('-');
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        int date = cal.get(Calendar.DATE);
        if (date < 10) {
            sb.append('0');
        }
        sb.append(date);
        sb.append('T');
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        sb.append(':');
        int min = cal.get(Calendar.MINUTE);
        if (min < 10) {
            sb.append('0');
        }
        sb.append(min);
        sb.append(':');
        int sec = cal.get(Calendar.SECOND);
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        sb.append('.');
        int ms = cal.get(Calendar.MILLISECOND);
        if (ms < 100) {
            sb.append('0');
        }
        if (ms < 10) {
            sb.append('0');
        }
        sb.append(ms);
        sb.append('Z');
        return sb.toString();
    }

    /**
     * Returns the time in milliseconds of an ISO 8601 date representation.
     *
     * @param date The ISO 8601 encoded date/time to convert.
     * @return The specified date in milliseconds.
     */
    public static long fromISO8601Date(final String date) {
        final Matcher m = _PATTERN.matcher(date);
        if (!m.find() || m.group(4) == null) {
            throw new IllegalArgumentException("Illegal date format '" + date + "'");
        }
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int hoff = 0, moff = 0, doff = -1;
        if (m.group(10) != null) {
            doff = m.group(10).equals("-") ? 1 : -1;
            hoff = doff * (m.group(11) != null ? Integer.parseInt(m.group(11)) : 0);
            moff = doff * (m.group(12) != null ? Integer.parseInt(m.group(12)) : 0);
        }
        cal.set(Calendar.YEAR,        Integer.parseInt(m.group(1)));
        cal.set(Calendar.MONTH,       m.group(2) != null ? Integer.parseInt(m.group(2))-1 : 0);
        cal.set(Calendar.DATE,        m.group(3) != null ? Integer.parseInt(m.group(3)) : 1);
        cal.set(Calendar.HOUR_OF_DAY, m.group(5) != null ? Integer.parseInt(m.group(5)) + hoff: 0);
        cal.set(Calendar.MINUTE,      m.group(6) != null ? Integer.parseInt(m.group(6)) + moff: 0);
        cal.set(Calendar.SECOND,      m.group(7) != null ? Integer.parseInt(m.group(7)) : 0);
        cal.set(Calendar.MILLISECOND, m.group(8) != null ? Integer.parseInt(m.group(8)) : 0);
        return cal.getTimeInMillis();
    }

}
