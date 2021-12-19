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
package com.serotonin.mango.web.dwr;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class MailingListsDwr extends BaseDwr {
	private final Log log = LogFactory.getLog(MailingListsDwr.class);
	private static final String DEFAULT_CRON = "1 */15 * * * ?";

	public DwrResponseI18n init() {
		Permissions.ensureAdmin();
		DwrResponseI18n response = new DwrResponseI18n();
		response.addData("lists", new MailingListDao().getMailingLists());
		response.addData("users", new UserDao().getUsers());
		return response;
	}

	public MailingList getMailingList(int id) {
		if (id == Common.NEW_ID) {
			MailingList ml = new MailingList();
			ml.setId(Common.NEW_ID);
			ml.setXid(new MailingListDao().generateUniqueXid());
			ml.setEntries(new LinkedList<EmailRecipient>());
			return ml;
		}
		return new MailingListDao().getMailingList(id);
	}

	public DwrResponseI18n saveMailingList(int id, String xid, String name,
			List<RecipientListEntryBean> entryBeans, List<Integer> inactiveIntervals,
            boolean dailyLimitSentEmails, String cronPattern, boolean collectInactiveEmails,
			int collectInactiveEmailsNumber) {
		Permissions.ensureAdmin();
		DwrResponseI18n response = new DwrResponseI18n();
		MailingListDao mailingListDao = new MailingListDao();

		// Validate the given information. If there is a problem, return an
		// appropriate error message.
		MailingList ml = createMailingList(id, xid, name, entryBeans);

		if(!collectInactiveEmails && (cronPattern == null || cronPattern.trim().isEmpty())) {
			cronPattern = DEFAULT_CRON;
		}

		ml.getInactiveIntervals().addAll(inactiveIntervals);
		ml.setDailyLimitSentEmailsNumber(collectInactiveEmailsNumber);
		ml.setDailyLimitSentEmails(dailyLimitSentEmails);
		ml.setCronPattern(cronPattern);
		ml.setCollectInactiveEmails(collectInactiveEmails);

		if (StringUtils.isEmpty(xid))
			response.addContextualMessage("xid", "validate.required");
		else if (!mailingListDao.isXidUnique(xid, id))
			response.addContextualMessage("xid", "validate.xidUsed");

		ml.validate(response);

		if (!response.getHasMessages()) {
			// Save the mailing list
			mailingListDao.saveMailingList(ml);
			response.addData("mlId", ml.getId());
		}

		return response;
	}

	public void deleteMailingList(int mlId) {
		Permissions.ensureAdmin();
		new MailingListDao().deleteMailingList(mlId);
	}

	public DwrResponseI18n sendTestEmail(int id, String name,
			List<RecipientListEntryBean> entryBeans) {
		DwrResponseI18n response = new DwrResponseI18n();

		MailingList ml = createMailingList(id, null, name, entryBeans);
		new MailingListDao().populateEntrySubclasses(ml.getEntries());

		Set<String> addresses = new HashSet<String>();
		ml.appendAddresses(addresses, null);
		String[] toAddrs = addresses.toArray(new String[0]);

		try {
			ResourceBundle bundle = Common.getBundle();
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("message",
					new LocalizableMessage("ftl.userTestEmail", ml.getName()));
			MangoEmailContent cnt = new MangoEmailContent("ftl.testEmail",
					model, bundle,
					I18NUtils.getMessage(bundle, "ftl.testEmail"), Common.UTF8);
			EmailWorkItem.queueEmail(toAddrs, cnt);
		} catch (Exception e) {
			response.addGenericMessage("mailingLists.testerror", e.getMessage());
			log.warn("", e);
		}

		return response;
	}

	//
	// /
	// / Private helper methods
	// /
	//
	private MailingList createMailingList(int id, String xid, String name,
			List<RecipientListEntryBean> entryBeans) {
		// Convert the incoming information into more useful types.
		MailingList ml = new MailingList();
		ml.setId(id);
		ml.setXid(xid);
		ml.setName(name);

		List<EmailRecipient> entries = new ArrayList<EmailRecipient>(
				entryBeans.size());
		for (RecipientListEntryBean bean : entryBeans) {
			entries.add(bean.createEmailRecipient());
		}

		ml.setEntries(entries);

		return ml;
	}
}
