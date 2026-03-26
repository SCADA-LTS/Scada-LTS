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
package com.serotonin.mango.rt.dataSource.meta;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.script.ScriptException;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.IntValuePair;
import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.cache.DataSourcePointsCache;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.utils.ScriptContextUtils;

import static org.scada_lts.web.beans.validation.script.ScriptValidatorUtils.validateScript;

/**
 * @author Matthew Lohbihler
 */
public class ScriptExecutor {

	private final DataPointService dataPointService = new DataPointService();
	private static final String SCRIPT_PREFIX = "function __scriptExecutor__() {";
	private static final String SCRIPT_SUFFIX = "\r\n}\r\n__scriptExecutor__();";
	private static String SCRIPT_FUNCTION_PATH;
	private static String FUNCTIONS;
	private static final Log LOG = LogFactory.getLog(ScriptExecutor.class);

	public static void setScriptFunctionPath(String path) {
		SCRIPT_FUNCTION_PATH = path;
	}

	public Map<String, IDataPoint> convertContext(List<IntValuePair> context) throws Exception {
		return convertContext(context, null, null);
	}

	@Deprecated(since = "2.8.1")
	public Map<String, IDataPoint> convertContext2(List<IntValuePair> context, DataPointRT dataPoint, MetaDataSourceRT metaDataSource) throws Exception {
		RuntimeManager rtm = Common.ctx.getRuntimeManager();

		Map<String, IDataPoint> converted = new HashMap<>();
		List<DataPointStateException> exceptions = new ArrayList<>();
		for (IntValuePair contextEntry : context) {
			if(dataPoint == null || dataPoint.getId() == Common.NEW_ID || dataPoint.getId() != contextEntry.getKey()) {
				DataPointRT point = rtm.getDataPoint(contextEntry.getKey());
				if (point == null) {
					LOG.error("Error DataPointRT null "
							+ new Exception("key:" + contextEntry.getKey()
							+ " value:" + contextEntry.getValue()) + " from:" + LoggingUtils.dataPointInfo(dataPoint));
					DataPointStateException dataPointStateException = createPointUnavailableException(contextEntry);
					if(dataPoint != null && metaDataSource != null) {
						metaDataSource.raiseContextError(System.currentTimeMillis(), dataPoint, dataPointStateException.getLocalizableMessage());
					}
					exceptions.add(dataPointStateException);
				} else if (point.isUnreliable()) {
					LOG.warn("Error DataPointRT is unavailable "
							+ new Exception("key:" + contextEntry.getKey()
							+ " value:" + contextEntry.getValue()) + " - " + LoggingUtils.dataPointInfo(point));
					DataPointStateException dataPointStateException = createPointUnavailableException(contextEntry, point);
					if(dataPoint != null && metaDataSource != null) {
						metaDataSource.raiseContextError(System.currentTimeMillis(), dataPoint, dataPointStateException.getLocalizableMessage());
					}
					exceptions.add(dataPointStateException);
				} else {
					converted.put(contextEntry.getValue(), point);
				}
			}
		}
		if(!exceptions.isEmpty()) {
			StringBuilder messages = new StringBuilder();
			for(DataPointStateException exception: exceptions) {
				LocalizableMessage localizableMessage = exception.getLocalizableMessage();
				String message = localizableMessage.getLocalizedMessage(Common.getBundle());
				messages.append(message).append(" ; ");
			}
			throw new Exception(messages.toString());
		}
		if(dataPoint != null && metaDataSource != null)
			metaDataSource.returnToNormalContext(System.currentTimeMillis(), dataPoint);

		return converted;
	}

	public Map<String, IDataPoint> convertContext(List<IntValuePair> context, DataPointRT parentPoint, MetaDataSourceRT parentSource) throws Exception {
		RuntimeManager rtm = Common.ctx.getRuntimeManager();
		ResourceBundle resourceBundle = Common.getBundle();
		Map<String, IDataPoint> converted = new HashMap<>();
		List<DataPointStateException> exceptions = new ArrayList<>();
		for (IntValuePair contextEntry : context) {
			if(parentPoint == null || parentPoint.getId() == Common.NEW_ID || parentPoint.getId() != contextEntry.getKey()) {
				DataPointRT contextPoint = rtm.getDataPoint(contextEntry.getKey());

				DataPointVO contextPointVO;

				if(contextPoint == null) {
					contextPointVO = DataSourcePointsCache.getInstance().getDataPoint(contextEntry.getKey());
					if(contextPointVO == null) {
						contextPointVO = dataPointService.getDataPoint(contextEntry.getKey());
					}
 				} else {
					contextPointVO = contextPoint.getVO();
				}

				LocalizableMessage pointMissingMessage = createPointMissingMessage(contextEntry);
				LocalizableMessage pointDisabledMessage = createPointDisabledMessage(contextEntry);
				LocalizableMessage pointUnavailableMessage = createUnavailablePointMessage(contextEntry);

				List<DataPointStateException> iterationExceptions = new ArrayList<>();

				RaiseEventExecutor raiseEventExecutor = RaiseEventExecutor.newExecutor(parentPoint, parentSource, contextEntry, contextPoint, contextPointVO, resourceBundle);

				raiseEventExecutor.execute(pointMissingMessage,
						(t,p) -> m -> parentSource.raiseContextErrorPointMissing(t, p, m),
						ScriptExecutor::isMissingPoint,
						(t,p) -> m -> parentSource.returnToNormalContextPointMissing(t, p, m)
				).map(iterationExceptions::add);

				raiseEventExecutor.execute(pointDisabledMessage,
						(t,p) -> m -> parentSource.raiseContextErrorPointDisabled(t, p, m),
						ScriptExecutor::isDisabledPoint,
						(t,p) -> m -> parentSource.returnToNormalContextPointDisabled(t, p, m)
				).map(iterationExceptions::add);

				raiseEventExecutor.execute(pointUnavailableMessage,
						(t,p) -> m -> parentSource.raiseContextErrorPointUnavailable(t, p, m),
						(p, v) -> isUnreliablePoint(p),
						(t,p) -> m -> parentSource.returnToNormalContextPointUnavailable(t, p, m)
				).map(iterationExceptions::add);

				if (iterationExceptions.isEmpty()) {
					converted.put(contextEntry.getValue(), contextPoint);
				}

				exceptions.addAll(iterationExceptions);
			}
		}

		StringBuilder messages = new StringBuilder();
		if(!exceptions.isEmpty()) {
			for(DataPointStateException exception: exceptions) {
				messages.append(" ")
						.append(exception.getLocalizedMessage())
						.append(" ; ");
			}
			throw new Exception(messages.toString());
		}

		return converted;
	}

	private static boolean isUnreliablePoint(DataPointRT point) {
		return point != null && point.isUnreliable();
	}

	private static boolean isDisabledPoint(DataPointRT point, DataPointVO dataPointVO) {
		return point == null && dataPointVO != null;
	}

	private static boolean isMissingPoint(DataPointRT point, DataPointVO dataPointVO) {
		return point == null && dataPointVO == null;
	}

	public PointValueTime execute(String script,
			Map<String, IDataPoint> context, long runtime, int dataTypeId,
			long timestamp) throws ScriptException, ResultTypeException {

		validateScript(script);

		ensureFunctions();

		// Create the script engine.
		// ScriptEngineManager manager;
		/*
		 * try { manager = new ScriptEngineManager(); } catch (Exception e) {
		 * throw new ScriptException(e); }
		 */

		Integer jsOptimizationLevel = 0;
		try {
			jsOptimizationLevel = ScadaConfig.getInstance().getInt(ScadaConfig.OPTIMIZATION_LEVEL_JS,0);
		} catch (IOException e) {
			LOG.error(e);
		}
		Context cx = Context.enter();
		cx.setOptimizationLevel(jsOptimizationLevel);

		Scriptable scope = null;
		Object result = null;

		// Execute.
		try {
			scope = ScriptContextUtils.initStandardObjects(cx);

			// Create the wrapper object context.
			WrapperContext wrapperContext = new WrapperContext(runtime);

			// Add constants to the context.
			scope.put("SECOND", scope, Common.TimePeriods.SECONDS);
			scope.put("MINUTE", scope, Common.TimePeriods.MINUTES);
			scope.put("HOUR", scope, Common.TimePeriods.HOURS);
			scope.put("DAY", scope, Common.TimePeriods.DAYS);
			scope.put("WEEK", scope, Common.TimePeriods.WEEKS);
			scope.put("MONTH", scope, Common.TimePeriods.MONTHS);
			scope.put("YEAR", scope, Common.TimePeriods.YEARS);
			scope.put("CONTEXT", scope, wrapperContext);

			// Put the context variables into the engine with engine scope.
			for (String varName : context.keySet()) {
				IDataPoint point = context.get(varName);
				int dt = point.getDataTypeId();

				PointValueTime currentValue = point.getPointValue();
				LOG.debug("Var: "
						+ varName
						+ ", value: "
						+ (currentValue == null ? "null" : currentValue.toString()));


				if (dt == DataTypes.BINARY) {
					scope.put(varName, scope, new BinaryPointWrapper(point,
							wrapperContext));
				} else if (dt == DataTypes.MULTISTATE) {
					scope.put(varName, scope, new MultistatePointWrapper(point,
							wrapperContext));
				} else if (dt == DataTypes.NUMERIC) {
					scope.put(varName, scope, new NumericPointWrapper(point,
							wrapperContext));
				} else if (dt == DataTypes.ALPHANUMERIC) {
					scope.put(varName, scope, new AlphanumericPointWrapper(
							point, wrapperContext));
				} else
					throw new ShouldNeverHappenException(
							"Unknown data type id: " + point.getDataTypeId());
			}

			// Create the script.
			LOG.debug("Script: " + script);
			if (jsOptimizationLevel == -1) {
				// optimization level -1
				script = SCRIPT_PREFIX + script + SCRIPT_SUFFIX;
				try {

					Object o = cx.evaluateString(scope, script, "<cmd>", 1,
							null);
					if (o == null)
						result = null;
					else {
						if (!(o instanceof AbstractPointWrapper)) {
							result = o.toString();
						} else {
							result = o;
						}
					}

				} catch (Exception e) {
					LOG.warn("Error evaluating string (script): "
							+ e.getMessage());
					throw new ScriptException(e.getMessage());
				}

				Object ts = scope.get("TIMESTAMP", scope);
				if (ts != null) {
					if (ts instanceof Number)
						timestamp = ((Number) ts).longValue();
				}

				MangoValue value;
				String strResult = (String) result;

				try {

					if (result == null) {
						if (dataTypeId == DataTypes.BINARY)
							value = new BinaryValue(false);
						else if (dataTypeId == DataTypes.MULTISTATE)
							value = new MultistateValue(0);
						else if (dataTypeId == DataTypes.NUMERIC)
							value = new NumericValue(0);
						else if (dataTypeId == DataTypes.ALPHANUMERIC)
							value = new AlphanumericValue("");
						else
							value = null;
					} else if (result instanceof AbstractPointWrapper) {
						value = ((AbstractPointWrapper) result).getValueImpl();
						// See if the type matches.
					} else if (dataTypeId == DataTypes.BINARY) {
						Boolean b = Boolean.parseBoolean(strResult);
						value = new BinaryValue(b);
						LOG.debug("Result: " + value.getBooleanValue());
					} else if (dataTypeId == DataTypes.MULTISTATE) {
						Double d = Double.parseDouble(strResult);
						Integer i = (int) Math.round(d);
						value = new MultistateValue(((Number) i).intValue());
						LOG.debug("Result: " + value.getIntegerValue());
					} else if (dataTypeId == DataTypes.NUMERIC) {
						Double d = Double.parseDouble(strResult);
						value = new NumericValue(((Number) d).doubleValue());
						LOG.debug("Result: " + value.getDoubleValue());
					} else if (dataTypeId == DataTypes.ALPHANUMERIC) {
						value = new AlphanumericValue((String) strResult);
						LOG.debug("Result: " + value.getStringValue());
					} else
						// If not, ditch it.
						throw new ResultTypeException(new LocalizableMessage(
								"event.script.convertError", result,
								DataTypes.getDataTypeMessage(dataTypeId)));
				} catch (Exception ex) {
					if(ex instanceof ResultTypeException)
						throw ex;
					throw new ScriptException(ex);
				}
				return new PointValueTime(value, timestamp);
			} else {
				script = SCRIPT_PREFIX + script + SCRIPT_SUFFIX + FUNCTIONS;

				try {
					result = cx.evaluateString(scope, script, "<cmd>", 1, null);
				} catch (Exception e) {
					LOG.warn("Error evaluating string (script): "
							+ e.getMessage());
					throw new ScriptException(e.getMessage());
				}

				Object ts = scope.get("TIMESTAMP", scope);
				if (ts != null) {
					if (ts instanceof Number)
						timestamp = ((Number) ts).longValue();
				}

				MangoValue value;
				try {
					if (result == null) {
						if (dataTypeId == DataTypes.BINARY)
							value = new BinaryValue(false);
						else if (dataTypeId == DataTypes.MULTISTATE)
							value = new MultistateValue(0);
						else if (dataTypeId == DataTypes.NUMERIC)
							value = new NumericValue(0);
						else if (dataTypeId == DataTypes.ALPHANUMERIC)
							value = new AlphanumericValue("");
						else
							value = null;
					} else if (result instanceof AbstractPointWrapper) {
						value = ((AbstractPointWrapper) result).getValueImpl();
					} else if (dataTypeId == DataTypes.BINARY
							&& result instanceof Boolean) {
						value = new BinaryValue((Boolean) result);
						LOG.debug("Result: " + value.getBooleanValue());
					} else if (dataTypeId == DataTypes.MULTISTATE
							&& result instanceof Number) {
						value = new MultistateValue(((Number) result).intValue());
						LOG.debug("Result: " + value.getIntegerValue());
					} else if (dataTypeId == DataTypes.NUMERIC
							&& result instanceof Number) {
						value = new NumericValue(((Number) result).doubleValue());
						LOG.debug("Result: " + value.getDoubleValue());
					} else if (dataTypeId == DataTypes.ALPHANUMERIC
							&& result instanceof String) {
						value = new AlphanumericValue((String) result);
						LOG.debug("Result: " + value.getStringValue());
					} else
						throw new ResultTypeException(new LocalizableMessage(
								"event.script.convertError", result,
								DataTypes.getDataTypeMessage(dataTypeId)));
				} catch (Exception ex) {
					if(ex instanceof ResultTypeException)
						throw ex;
					throw new ScriptException(ex);
				}
				return new PointValueTime(value, timestamp);
			}
		} finally {
			Context.exit();
		}
	}

	public static ScriptException prettyScriptMessage(ScriptException e) {
		while (e.getCause() instanceof ScriptException)
			e = (ScriptException) e.getCause();

		// Try to make the error message look a bit nicer.
		List<String> exclusions = new ArrayList<String>();
		exclusions.add("sun.org.mozilla.javascript.internal.EcmaError: ");
		exclusions
				.add("sun.org.mozilla.javascript.internal.EvaluatorException: ");
		String message = e.getMessage();
		for (String exclude : exclusions) {
			if (message.startsWith(exclude))
				message = message.substring(exclude.length());
		}
		return new ScriptException(message, e.getFileName(), e.getLineNumber(),
				e.getColumnNumber());
	}

	private static void ensureFunctions() {
		if (FUNCTIONS == null) {
			if (SCRIPT_FUNCTION_PATH == null)
				SCRIPT_FUNCTION_PATH = Common.ctx.getServletContext()
						.getRealPath("WEB-INF/scripts/scriptFunctions.js");
			StringWriter sw = new StringWriter();
			FileReader fr = null;
			try {
				fr = new FileReader(SCRIPT_FUNCTION_PATH);
				StreamUtils.transfer(fr, sw);
			} catch (Exception e) {
				throw new ShouldNeverHappenException(e);
			} finally {
				try {
					if (fr != null)
						fr.close();
				} catch (IOException e) {
					// no op
				}
			}
			FUNCTIONS = sw.toString();
		}
	}

	@Deprecated(since = "2.8.1")
	private static DataPointStateException createPointUnavailableException(IntValuePair contextEntry) {
		return createDataPointStateException(contextEntry, createPointProblemMessage(contextEntry, null), Common.getBundle());
	}

	@Deprecated(since = "2.8.1")
	private static DataPointStateException createPointUnavailableException(IntValuePair contextEntry, DataPointRT point) {
		return createDataPointStateException(contextEntry, createPointProblemMessage(contextEntry, point), Common.getBundle());
	}

	private static LocalizableMessage createUnavailablePointMessage(IntValuePair contextEntry) {
		return new LocalizableMessage("event.meta.pointUnavailable", LoggingUtils.varPointInfo(contextEntry));
	}

	private static LocalizableMessage createPointMissingMessage(IntValuePair contextEntry) {
		return new LocalizableMessage("event.meta.pointMissingX", LoggingUtils.varPointInfo(contextEntry));
	}

	private static LocalizableMessage createPointDisabledMessage(IntValuePair contextEntry) {
		return new LocalizableMessage("event.meta.pointDisabled", LoggingUtils.varPointInfo(contextEntry));
	}


	private static DataPointStateException createDataPointStateException(IntValuePair contextEntry, LocalizableMessage message, ResourceBundle resourceBundle) {
		return DataPointStateException.newInstance(contextEntry, message, null, resourceBundle);
	}

	private static LocalizableMessage createPointProblemMessage(IntValuePair contextEntry, DataPointRT dataPoint) {
		if(dataPoint == null || dataPoint.getVO() == null)
			return createPointDisabledMessage(contextEntry);
		return createUnavailablePointMessage(contextEntry);
	}
}
