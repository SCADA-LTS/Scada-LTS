package br.org.scadabr.rt.scripting;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataSource.meta.AlphanumericPointWrapper;
import com.serotonin.mango.rt.dataSource.meta.BinaryPointWrapper;
import com.serotonin.mango.rt.dataSource.meta.DataPointStateException;
import com.serotonin.mango.rt.dataSource.meta.MultistatePointWrapper;
import com.serotonin.mango.rt.dataSource.meta.NumericPointWrapper;
import com.serotonin.mango.rt.dataSource.meta.ScriptExecutor;
import com.serotonin.mango.rt.dataSource.meta.WrapperContext;
import com.serotonin.mango.vo.User;

public class ContextualizedScriptRT extends ScriptRT {
	private static final String SCRIPT_PREFIX = "function __scriptExecutor__() {";
	private static final String SCRIPT_SUFFIX = "\r\n}\r\n__scriptExecutor__();";
	private Log LOG = LogFactory.getLog(ContextualizedScriptRT.class);

	private static String SCRIPT_FUNCTION_PATH;
	private static String FUNCTIONS;

	public static void setScriptFunctionPath(String path) {
		SCRIPT_FUNCTION_PATH = path;
	}

	public ContextualizedScriptRT(ContextualizedScriptVO vo) {
		super(vo);
	}

	@Override
	public void execute() throws ScriptException {
		// ScriptEngineManager manager;
		Context cx = Context.enter();
		//cx.setLanguageVersion(Context.VERSION_DEFAULT);
		cx.setOptimizationLevel(Common.getEnvironmentProfile().getInt("js.optimizationlevel", 0));

		/*
		 * try { manager = new ScriptEngineManager(); } catch (Exception e) {
		 * throw new ScriptException(e); }
		 */

		try {
			Scriptable scope = cx.initStandardObjects();
			// ScriptEngine engine = manager.getEngineByName("js");
			// engine.getContext().setErrorWriter(new PrintWriter(System.err));
			// engine.getContext().setWriter(new PrintWriter(System.out));

			// Create the wrapper object context.
			WrapperContext wrapperContext = new WrapperContext(System.currentTimeMillis());

			// Add constants to the context.
			scope.put("SECOND", scope, Common.TimePeriods.SECONDS);
			scope.put("MINUTE", scope, Common.TimePeriods.MINUTES);
			scope.put("HOUR", scope, Common.TimePeriods.HOURS);
			scope.put("DAY", scope, Common.TimePeriods.DAYS);
			scope.put("WEEK", scope, Common.TimePeriods.WEEKS);
			scope.put("MONTH", scope, Common.TimePeriods.MONTHS);
			scope.put("YEAR", scope, Common.TimePeriods.YEARS);
			scope.put("CONTEXT", scope, wrapperContext);
			Map<String, IDataPoint> context = null;

			try {
				context = new ScriptExecutor().convertContext(((ContextualizedScriptVO) vo).getPointsOnContext());
			} catch (DataPointStateException e1) {
				LOG.error("Data Point State Exception" + e1.getMessage());
				if (vo != null) {
					throw new ScriptException("xid:"+vo.getXid() +" script:"+vo.getScript()+" error:" + e1.getMessage());
				} else {
					throw new ScriptException("vo: null,"+e1.getMessage());
				}
			}

			// Put the context variables into the engine with engine scope.
			for (String varName : context.keySet()) {
				IDataPoint point = context.get(varName);
				int dt = point.getDataTypeId();

				PointValueTime currentValue = point.getPointValue();
				LOG.debug("Var: " + varName + ", value: "
						+ (currentValue == null ? "null" : currentValue.toString()));

				if (dt == DataTypes.BINARY)
					scope.put(varName, scope, new BinaryPointWrapper(point, wrapperContext));
				else if (dt == DataTypes.MULTISTATE)
					scope.put(varName, scope, new MultistatePointWrapper(point, wrapperContext));
				else if (dt == DataTypes.NUMERIC)
					scope.put(varName, scope, new NumericPointWrapper(point, wrapperContext));
				else if (dt == DataTypes.ALPHANUMERIC)
					scope.put(varName, scope, new AlphanumericPointWrapper(point, wrapperContext));
				else
					throw new ShouldNeverHappenException("Unknown data type id: " + point.getDataTypeId());
			}

			List<IntValuePair> objectsContext = ((ContextualizedScriptVO) vo).getObjectsOnContext();

			User user = new UserDao().getUser(vo.getUserId());
			for (IntValuePair object : objectsContext) {
				ScriptContextObject o = ScriptContextObject.Type.valueOf(object.getKey()).createScriptContextObject();
				o.setUser(user);
				scope.put(object.getValue(), scope, o);
			}

			// Create the script.
			String script = SCRIPT_PREFIX + getScript() + SCRIPT_SUFFIX;

			// Execute.
			Object result = null;
			try {	
				result = cx.evaluateString(scope, script, "<cmd>", 1, null);
			} catch (Exception e) {
				LOG.error("Error executing script " + e.getMessage());
				StringWriter strWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(strWriter);
				e.printStackTrace(printWriter);
				throw new ScriptException( strWriter.toString());
			}
		} finally {
			Context.exit();
		}

	}
}
