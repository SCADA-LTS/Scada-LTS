/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.event.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.SetPointWorkItem;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

public class SetPointHandlerRT extends EventHandlerRT implements SetPointSource {
	private static final Log LOG = LogFactory.getLog(SetPointHandlerRT.class);

	public SetPointHandlerRT(EventHandlerVO vo) {
		this.vo = vo;
	}

	@Override
	public void eventRaised(EventInstance evt) {
		if (vo.getActiveAction() == EventHandlerVO.SET_ACTION_NONE)
			return;

		// Validate that the target point is available.
		DataPointRT targetPoint = Common.ctx.getRuntimeManager().getDataPoint(
				vo.getTargetPointId());
		if (targetPoint == null) {
			raiseFailureEvent(new LocalizableMessage(
					"event.setPoint.targetPointMissing"), evt.getEventType());
			return;
		}

		if (!targetPoint.getPointLocator().isSettable()) {
			raiseFailureEvent(new LocalizableMessage(
					"event.setPoint.targetNotSettable"), evt.getEventType());
			return;
		}

		int targetDataType = targetPoint.getVO().getPointLocator()
				.getDataTypeId();

		MangoValue value;
		if (vo.getActiveAction() == EventHandlerVO.SET_ACTION_POINT_VALUE) {
			// Get the source data point.
			DataPointRT sourcePoint = Common.ctx.getRuntimeManager()
					.getDataPoint(vo.getActivePointId());
			if (sourcePoint == null) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.activePointMissing"),
						evt.getEventType());
				return;
			}

			PointValueTime valueTime = sourcePoint.getPointValue();
			if (valueTime == null) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.activePointValue"), evt.getEventType());
				return;
			}

			if (DataTypes.getDataType(valueTime.getValue()) != targetDataType) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.activePointDataType"),
						evt.getEventType());
				return;
			}

			value = valueTime.getValue();
		} else if (vo.getActiveAction() == EventHandlerVO.SET_ACTION_STATIC_VALUE) {
			value = MangoValue.stringToValue(vo.getActiveValueToSet(),
					targetDataType);
		} else
			throw new ShouldNeverHappenException("Unknown active action: "
					+ vo.getActiveAction());

		// Queue a work item to perform the set point.
		Common.ctx.getBackgroundProcessing().addWorkItem(
				new SetPointWorkItem(vo.getTargetPointId(), new PointValueTime(
						value, evt.getActiveTimestamp()), this));
	}

	@Override
	public void eventInactive(EventInstance evt) {
		if (vo.getInactiveAction() == EventHandlerVO.SET_ACTION_NONE)
			return;

		// Validate that the target point is available.
		DataPointRT targetPoint = Common.ctx.getRuntimeManager().getDataPoint(
				vo.getTargetPointId());
		if (targetPoint == null) {
			raiseFailureEvent(new LocalizableMessage(
					"event.setPoint.targetPointMissing"), evt.getEventType());
			return;
		}

		if (!targetPoint.getPointLocator().isSettable()) {
			raiseFailureEvent(new LocalizableMessage(
					"event.setPoint.targetNotSettable"), evt.getEventType());
			return;
		}

		int targetDataType = targetPoint.getVO().getPointLocator()
				.getDataTypeId();

		MangoValue value;
		if (vo.getInactiveAction() == EventHandlerVO.SET_ACTION_POINT_VALUE) {
			// Get the source data point.
			DataPointRT sourcePoint = Common.ctx.getRuntimeManager()
					.getDataPoint(vo.getInactivePointId());
			if (sourcePoint == null) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.inactivePointMissing"),
						evt.getEventType());
				return;
			}

			PointValueTime valueTime = sourcePoint.getPointValue();
			if (valueTime == null) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.inactivePointValue"),
						evt.getEventType());
				return;
			}

			if (DataTypes.getDataType(valueTime.getValue()) != targetDataType) {
				raiseFailureEvent(new LocalizableMessage(
						"event.setPoint.inactivePointDataType"),
						evt.getEventType());
				return;
			}

			value = valueTime.getValue();
		} else if (vo.getInactiveAction() == EventHandlerVO.SET_ACTION_STATIC_VALUE)
			value = MangoValue.stringToValue(vo.getInactiveValueToSet(),
					targetDataType);
		else
			throw new ShouldNeverHappenException("Unknown active action: "
					+ vo.getInactiveAction());

		Common.ctx.getBackgroundProcessing().addWorkItem(
				new SetPointWorkItem(vo.getTargetPointId(), new PointValueTime(
						value, evt.getRtnTimestamp()), this));
	}

	private void raiseFailureEvent(LocalizableMessage message, EventType et) {
		if (et != null && et.isSystemMessage()) {
			if (((SystemEventType) et).getSystemEventTypeId() == SystemEventType.TYPE_SET_POINT_HANDLER_FAILURE) {
				// The set point attempt failed for an event that is a set point
				// handler failure in the first place.
				// Do not propagate the event, but rather just write a log
				// message.
				LOG.warn("A set point event due to a set point handler failure itself failed. The failure event "
						+ "has been discarded: "
						+ message.getLocalizedMessage(Common.getBundle()));
				return;
			}
		}

		SystemEventType eventType = new SystemEventType(
				SystemEventType.TYPE_SET_POINT_HANDLER_FAILURE, vo.getId());
		if (StringUtils.isEmpty(vo.getAlias()))
			message = new LocalizableMessage("event.setPointFailed", message);
		else
			message = new LocalizableMessage("event.setPointFailed.alias",
					vo.getAlias(), message);
		SystemEventType.raiseEvent(eventType, System.currentTimeMillis(),
				false, message);
	}

	public void raiseRecursionFailureEvent() {
		raiseFailureEvent(new LocalizableMessage(
				"event.setPoint.recursionFailure"), null);
	}

	//
	// SetPointSource implementation
	//
	public int getSetPointSourceId() {
		return vo.getId();
	}

	public int getSetPointSourceType() {
		return SetPointSource.Types.EVENT_HANDLER;
	}

	@Override
	public void pointSetComplete() {
		// TODO Auto-generated method stub

	}
}
