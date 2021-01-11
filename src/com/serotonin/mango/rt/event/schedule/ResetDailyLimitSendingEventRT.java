package com.serotonin.mango.rt.event.schedule;

import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.MailingListService;

import java.text.ParseException;

public class ResetDailyLimitSendingEventRT implements ModelTimeoutClient<Boolean> {


    private final Log log = LogFactory.getLog(ResetDailyLimitSendingEventRT.class);

    private TimerTask task;
    private final int mailingListId;
    private final RuntimeManager runtimeManager;
    private final MailingListService mailingListService;

    public ResetDailyLimitSendingEventRT(MailingList mailingList, RuntimeManager runtimeManager,
                                         MailingListService mailingListService) {
        this.mailingListId = mailingList.getId();
        this.runtimeManager = runtimeManager;
        this.mailingListService = mailingListService;
    }

    public void initialize() {
        try {
            CronTimerTrigger activeTrigger = new CronTimerTrigger("0 0 0 * * ?");
            task = new ModelTimeoutTask<>(activeTrigger, this, true);
        } catch (ParseException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void terminate() {
        if (task != null)
            task.cancel();
    }

    @Override
    public void scheduleTimeout(Boolean model, long fireTime) {
        log.info("run: " + ResetDailyLimitSendingEventRT.class.getName());
        runtimeManager.stopSendEmailSms(mailingListId);
        MailingList mailingList1 = mailingListService.getMailingList(mailingListId);
        runtimeManager.startSendEmailSms(mailingList1);
        log.info("executed: " + ResetDailyLimitSendingEventRT.class.getName());
    }
}
