package br.org.scadabr.rt.dataSource;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.org.scadabr.api.constants.ServerStateCode;

import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;

public class ServerStateChecker implements org.quartz.SchedulerListener, Job {

	private static ServerStateCode state = ServerStateCode.RUNNING;

	public static final long CHECK_PERIOD = 5000;
	private static final long TOLERANCE = (long) (CHECK_PERIOD * 0.05);
	private static long lastFiredTime = 0;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		lastFiredTime = arg0.getFireTime().getTime();
	}

	@Override
	public void schedulerError(String arg0, SchedulerException arg1) {
		ServerStateChecker.setState(ServerStateCode.FAILED);
		arg1.printStackTrace();
	}

	@Override
	public void jobScheduled(Trigger arg0) {
	}

	@Override
	public void jobUnscheduled(String arg0, String arg1) {

	}

	@Override
	public void jobsPaused(String arg0, String arg1) {

	}

	@Override
	public void jobsResumed(String arg0, String arg1) {

	}

	@Override
	public void schedulerShutdown() {
	}

	@Override
	public void triggerFinalized(Trigger arg0) {

	}

	@Override
	public void triggersPaused(String arg0, String arg1) {

	}

	@Override
	public void triggersResumed(String arg0, String arg1) {

	}

	public synchronized static void setState(ServerStateCode state) {
		ServerStateChecker.state = state;
	}

	private static boolean isDatabaseRunning() {
		boolean result = true;
		try {
			List<User> users = new UserDao().getUsers();
			if (users.size() < 1)
				result = false;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	private static boolean isSchedulerRunning() {
		// long now = System.currentTimeMillis();
		// boolean result = true;
		// try {
		// Scheduler sched = Common.ctx.getScheduler();
		//
		// if (sched.isShutdown())
		// result = false;
		// if (!sched.isStarted())
		// result = false;
		// if (sched.isInStandbyMode())
		// result = false;
		//
		// if (lastFiredTime != 0) {
		// if ((now - lastFiredTime) > CHECK_PERIOD + TOLERANCE) {
		// result = false;
		// }
		// }
		//
		// } catch (Exception e) {
		// result = false;
		// }
		//
		// return result;
		return true;
		// REVER - REM
	}

	public static ServerStateCode getState() {
		if (!isSchedulerRunning())
			return ServerStateCode.FAILED;
		if (!isDatabaseRunning())
			return ServerStateCode.FAILED;

		return state;
	}

}
