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
package org.openhab.binding.alarmclock.internal.handlers;

import static org.openhab.binding.alarmclock.internal.AlarmClockBindingConstants.*;
import static org.openhab.binding.alarmclock.internal.system.Constants.*;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.alarmclock.internal.system.ClockManager;
import org.openhab.binding.alarmclock.internal.system.SunriseSunset;
import org.openhab.binding.alarmclock.internal.system.SystemHelper;
import org.openhab.binding.alarmclock.internal.system.SystemSunClock;
import org.openhab.binding.alarmclock.internal.system.ClockManager.Event;

/**
 * This clock calculates the sunrise and sunset times based on the
 * location and current date and timezone.
 *
 * @author wim
 *
 */
public abstract class AbstractSunClockHandler extends AbstractClockHandler {

    // Channel UIDs
    private final ChannelUID channelSunrise;
    private final ChannelUID channelSunset;
    private final ChannelUID channelValid;
    private boolean valid;

    public AbstractSunClockHandler(Thing thing) {
        super(thing);
        channelSunrise = new ChannelUID(thing.getUID(), CHANNEL_SUNRISE);
        channelSunset = new ChannelUID(thing.getUID(), CHANNEL_SUNSET);
        channelValid = new ChannelUID(thing.getUID(), CHANNEL_VALID);
        valid = true;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (super.handleBaseCommand(channelUID, command)) {
            return;
        } else if (command instanceof RefreshType && channelUID.getId().equals(CHANNEL_VALID)) {
            updateState(channelUID, isValid() ? OnOffType.ON : OnOffType.OFF);
        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }
    }

    /**
     * Set the valid state on or off.
     *
     * @param valid the new state.
     */
    protected void setValid(boolean valid) {
        this.valid = valid;
        updateState(channelValid, isValid() ? OnOffType.ON : OnOffType.OFF);
        updateProperties();
    }

    /**
     * Determine if the settings are valid. By default, this method returns true. Override in subclasses to test the
     * settings. For a sunrise clock e.g. settings may be invalid when the sunrise time is before the on time.
     *
     * @return true when valid.
     */
    @Override
    protected boolean isValid() {
        return valid;
    }

    /**
     * This method is called whenever an update is needed in settings related to sunrise/sunset times.
     *
     * @param sunriseSunset the Sunrise/sunset clock instance.
     */
    protected void updateTimeTriggers(SunriseSunset sunriseSunset) {
    }

    /**
     * Initialize the time triggers by registering the basic event handlers with the clock manager.
     */
    @Override
    protected void initEventHandlers() {
        ClockManager clockManager = ClockManager.getInstance();
        clockManager.on(Event.SIX_HOUR_TICK, (previous, current) -> {
            SystemSunClock sunClock = SystemSunClock.getInstance();
            sunClock.reCalculate();
            updateTimeTriggers(sunClock.getSunriseSunset());
        }, this);
        super.initEventHandlers();
    }

    /**
     * Update values and properties.
     */
    @SuppressWarnings("null")
    @Override
    protected void updateProperties() {
        super.updateProperties();
        Thing thing = getThing();
        thing.setProperty(PROPERTY_VALID, "" + isValid());
        PointType newLoc = SystemHelper.getLocation();
        String currentLoc = thing.getProperties().get(PROPERTY_LOCATION);
        if (currentLoc == null || !currentLoc.equals(newLoc.toString())) {
            thing.setProperty(PROPERTY_LOCATION, newLoc.toString());
            SystemSunClock.getInstance().reCalculate();
            refreshState();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        // Make sure location is reininitialized
        getThing().setProperty(PROPERTY_LOCATION, "");
    }

    /**
     * Refresh the state of channels that may have changed by (re-)initialization.
     */
    @Override
    protected void refreshState() {
        super.refreshState();
        updateState(channelOnTime, SystemHelper.formatTime(onHour, onMinute));
        updateState(channelOffTime, SystemHelper.formatTime(offHour, offMinute));
        updateState(channelSunrise, SystemSunClock.getInstance().getSunrise());
        updateState(channelSunset, SystemSunClock.getInstance().getSunset());
        updateState(channelValid, isValid() ? OnOffType.ON : OnOffType.OFF);
    }

}
