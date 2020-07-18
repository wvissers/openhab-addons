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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The Constants class contains constants used throughout the binding.
 *
 * @author Wim Vissers - Initial contribution
 */
@NonNullByDefault
public interface Constants {

    // Property names
    public static final String PROPERTY_LOCALE = "locale";
    public static final String PROPERTY_TIMEZONE = "timezone";
    public static final String PROPERTY_LOCATION = "location";
    public static final String PROPERTY_VALID = "valid";

    // ClockManager settings
    public static final int TIME_RESOLUTION_SECONDS = 30; // Should be < 60 seconds.

}
