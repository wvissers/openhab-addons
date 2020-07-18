/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.alarmclock.internal.system;

import java.time.Instant;
import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Object to encapsulate a compact local time hours/minutes/dayofweek.
 *
 * @author Wim Vissers.
 *
 */
@NonNullByDefault
public class CompactTime {

    private final int hour;
    private final int minute;
    private final @Nullable DayOfWeek dayOfWeek;

    /**
     * Create a local CompactTime from the system.
     */
    public CompactTime() {
        ZonedDateTime now = Instant.now().atZone(SystemHelper.getTimeZone().toZoneId());
        dayOfWeek = DayOfWeek.valueOf(now.getDayOfWeek().name());
        hour = now.getHour();
        minute = now.getMinute();
    }

    /**
     * Create a local CompactTime with only hours and minutes, but no DayOfWeek.
     *
     * @param hour
     * @param minute
     */
    public CompactTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.dayOfWeek = null;
    }

    /**
     * Return true if the time (hour:minute) of this instance is within the timeframe previous and current. The reason
     * is to make sure a switching event is not missed, even when for whatever reason to time between ticks exceeds the
     * minute.
     *
     * @param previous the previous time. Must be non null.
     * @param current the current time. Must be non null.
     * @return
     */
    public boolean isSwitchTime(CompactTime previous, CompactTime current) {
        return getCanonicalTime() > previous.getCanonicalTime() && getCanonicalTime() <= current.getCanonicalTime();
    }

    /**
     * Return true if this time is less than the given other time.
     *
     * @param other the other time. Must be non null.
     * @return
     */
    public boolean isLessThan(CompactTime other) {
        return getCanonicalTime() < other.getCanonicalTime();
    }

    /**
     * Return true if this time is less than or equal to the given other time.
     *
     * @param other the other time. Must be non null.
     * @return
     */
    public boolean isLessThanOrEqual(CompactTime other) {
        return getCanonicalTime() <= other.getCanonicalTime();
    }

    /**
     * Convert the time to a single integer value for easy handling.
     *
     * @return hour * 100 + minute.
     */
    private int getCanonicalTime() {
        return hour * 100 + minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public @Nullable DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

}
