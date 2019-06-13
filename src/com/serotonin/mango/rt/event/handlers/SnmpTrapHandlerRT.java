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

/**
 * @author Matthew Lohbihler
 */
public class SnmpTrapHandlerRT /* extends EventHandlerRT */{
    // TODO

    // CommunityTarget target = new CommunityTarget();
    // target.setCommunity(SnmpUtils.createOctetString("public"));
    //    
    // Address address = new UdpAddress(InetAddress.getByName("192.168.0.2"), 20162);
    // target.setAddress(address);
    //    
    // Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
    //    
    // PDUv1 pdu = new PDUv1();
    // pdu.setEnterprise(new OID(".1.3.6.1.4.1.850.1.12345.54321"));
    // pdu.setGenericTrap(PDUv1.COLDSTART);
    // pdu.add(new VariableBinding(new OID(".0") , new OctetString("my message")));
    //    
    // snmp.trap(pdu, target);

    // private static final Log LOG = LogFactory.getLog(EmailHandlerRT.class);
    //    
    // /**
    // * The name of the job in the scheduler if this escalates.
    // */
    // private String jobName;
    //    
    // private Set<String> activeRecipients;
    //    
    // /**
    // * The list of all of the recipients - active and escalation - for sending upon inactive if configured to do so.
    // */
    // private Set<String> inactiveRecipients;
    //
    // public EmailHandlerRT(EventHandlerVO vo) {
    // this.vo = vo;
    // }
    //    
    // public Set<String> getActiveRecipients() {
    // return activeRecipients;
    // }
    //
    // @Override
    // public void eventRaised(EventInstance evt) {
    // // Get the email addresses to send to
    // activeRecipients = new MailingListDao().getRecipientAddresses(vo.getActiveRecipients(),
    // new DateTime(evt.getActiveTimestamp()));
    //        
    // // Send an email to the active recipients.
    // sendActiveEmail(evt, activeRecipients);
    //        
    // // If an inactive notification is to be sent, save the active recipients.
    // if (vo.isSendInactive())
    // inactiveRecipients = activeRecipients;
    //        
    // // If an escalation is to be sent, set up timeout to trigger it.
    // if (vo.isSendEscalation()) {
    // long delayMS = Common.getMillis(vo.getEscalationDelayType(), vo.getEscalationDelay());
    // jobName = "EmailHandlerRT"+ Integer.toString(evt.getId());
    // Common.scheduleTimeout(jobName, this, evt.getActiveTimestamp() + delayMS, evt);
    // }
    // }
    //    
    // //
    // /// TimeoutClient
    // //
    // synchronized public void scheduleTimeout(Object model, long fireTime) {
    // EventInstance evt = (EventInstance)model;
    //        
    // // Get the email addresses to send to
    // Set<String> addresses = new MailingListDao().getRecipientAddresses(vo.getEscalationRecipients(),
    // new DateTime(fireTime));
    //        
    // // Send the escalation.
    // sendEmail("escalation.ftl", evt, "ftl.subject.escalation", addresses);
    //        
    // // If an inactive notification is to be sent, save the active recipients.
    // if (vo.isSendInactive())
    // inactiveRecipients.addAll(addresses);
    // }
    //
    // @Override
    // synchronized public void eventInactive(EventInstance evt) {
    // // Cancel the escalation job in case it's there
    // if (vo.isSendEscalation())
    // Common.unscheduleJob(jobName);
    //        
    // if (inactiveRecipients != null && inactiveRecipients.size() > 0)
    // // Send an email to the inactive recipients.
    // sendEmail("inactive.ftl", evt, "ftl.subject.inactive", inactiveRecipients);
    // }
    //    
    // public static void sendActiveEmail(EventInstance evt, Set<String> addresses) {
    // sendEmail("active.ftl", evt, "ftl.subject.active", addresses);
    // }
    //    
    // private static void sendEmail(String file, EventInstance evt, String subjectKey, Set<String> addresses) {
    // if (evt.getEventType().isSystemMessage()) {
    // if (((SystemEventType)evt.getEventType()).getSystemEventTypeId() ==
    // SystemEventType.TYPE_EMAIL_SEND_FAILURE) {
    // // Don't send email notifications about email send failures.
    // LOG.info("Not sending email for event raised due to email failure");
    // return;
    // }
    // }
    //        
    // ResourceBundle bundle = Common.getBundle();
    //        
    // String subject = I18NUtils.getMessage(bundle, subjectKey);
    // if (evt.getId() != Common.NEW_ID)
    // subject += new LocalizableMessage("ftl.subject.eventId", evt.getId()).getLocalizedMessage(bundle);
    //        
    // try {
    // Template plain = Common.ctx.getFreemarkerConfig().getTemplate("event/text/"+ file);
    // Template html = Common.ctx.getFreemarkerConfig().getTemplate("event/"+ file);
    // String[] toAddrs = addresses.toArray(new String[0]);
    // UsedImagesDirective inlineImages = new UsedImagesDirective();
    //            
    // // Send the email.
    // Map<String, Object> model = new HashMap<String, Object>();
    // model.put("evt", evt);
    // model.put("fmt", new MessageFormatDirective(bundle));
    // model.put("img", inlineImages);
    // EmailContent content = new EmailContent(plain, html, model, Common.UTF8);
    //            
    // for (String s : inlineImages.getImageList())
    // content.addInline(new EmailInline.FileInline(s, Common.ctx.getServletContext().getRealPath(s)));
    //            
    // EmailWorkItem.queueEmail(toAddrs, subject, content);
    // }
    // catch (Exception e) {
    // LOG.error("", e);
    // }
    // }

    // public SetPointHandlerRT(EventHandlerVO vo) {
    // this.vo = vo;
    // }
    //
    // @Override
    // public void eventRaised(EventInstance evt) {
    // // Validate that the target point is available.
    // DataPointRT targetPoint = Common.ctx.getRuntimeManager().getDataPoint(vo.getTargetPointId());
    // if (targetPoint == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.targetPointMissing"));
    // return;
    // }
    //        
    // if (!targetPoint.getPointLocator().isSettable()) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.targetNotSettable"));
    // return;
    // }
    //        
    // int targetDataType = targetPoint.getVO().getPointLocator().getDataTypeId();
    //        
    // Object value;
    // if (vo.getActiveAction() == EventHandlerVO.SET_ACTION_POINT_VALUE) {
    // // Get the source data point.
    // DataPointRT sourcePoint = Common.ctx.getRuntimeManager().getDataPoint(vo.getActivePointId());
    // if (sourcePoint == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.activePointMissing"));
    // return;
    // }
    //            
    // PointValueTime valueTime = sourcePoint.getPointValue();
    // if (valueTime == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.activePointValue"));
    // return;
    // }
    //            
    // if (DataTypes.getDataType(valueTime.getValue()) != targetDataType) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.activePointDataType"));
    // return;
    // }
    //            
    // value = valueTime.getValue();
    // }
    // else if (vo.getActiveAction() == EventHandlerVO.SET_ACTION_STATIC_VALUE) {
    // value = DataTypes.stringToValue(vo.getActiveValueToSet(), targetDataType);
    // }
    // else
    // return;
    //        
    // // Queue a work item to perform the set point.
    // Common.ctx.getBackgroundProcessing().addWorkItem(new SetPointWorkItem(
    // vo.getTargetPointId(), new PointValueTime(value, evt.getActiveTimestamp()), this));
    // }
    //    
    // @Override
    // public void eventInactive(EventInstance evt) {
    // // Validate that the target point is available.
    // DataPointRT targetPoint = Common.ctx.getRuntimeManager().getDataPoint(vo.getTargetPointId());
    // if (targetPoint == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.targetPointMissing"));
    // return;
    // }
    //        
    // if (!targetPoint.getPointLocator().isSettable()) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.targetNotSettable"));
    // return;
    // }
    //        
    // int targetDataType = targetPoint.getVO().getPointLocator().getDataTypeId();
    //        
    // Object value;
    // if (vo.getInactiveAction() == EventHandlerVO.SET_ACTION_POINT_VALUE) {
    // // Get the source data point.
    // DataPointRT sourcePoint = Common.ctx.getRuntimeManager().getDataPoint(vo.getInactivePointId());
    // if (targetPoint == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.inactivePointMissing"));
    // return;
    // }
    //            
    // PointValueTime valueTime = sourcePoint.getPointValue();
    // if (valueTime == null) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.inactivePointValue"));
    // return;
    // }
    //            
    // if (DataTypes.getDataType(valueTime.getValue()) != targetDataType) {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.inactivePointDataType"));
    // return;
    // }
    //            
    // value = valueTime.getValue();
    // }
    // else if (vo.getInactiveAction() == EventHandlerVO.SET_ACTION_STATIC_VALUE)
    // value = DataTypes.stringToValue(vo.getInactiveValueToSet(), targetDataType);
    // else
    // return;
    //        
    // Common.ctx.getBackgroundProcessing().addWorkItem(new SetPointWorkItem(
    // vo.getTargetPointId(), new PointValueTime(value, evt.getRtnTimestamp()), this));
    // }
    //    
    // private void raiseFailureEvent(LocalizableMessage message) {
    // SystemEventType eventType = new SystemEventType(SystemEventType.TYPE_SET_POINT_HANDLER_FAILURE, vo.getId());
    // if (StringUtils.isEmpty(vo.getAlias()))
    // message = new LocalizableMessage("event.setPointFailed", message);
    // else
    // message = new LocalizableMessage("event.setPointFailed.alias", vo.getAlias(), message);
    // SystemEventType.raiseEvent(eventType, System.currentTimeMillis(), false, message);
    // }
    //    
    // public void raiseRecursionFailureEvent() {
    // raiseFailureEvent(new LocalizableMessage("event.setPoint.recursionFailure"));
    // }
    //    
    //    
    // //
    // // SetPointSource implementation
    // //
    // public int getSetPointSourceId() {
    // return vo.getId();
    // }
    //
    // public int getSetPointSourceType() {
    // return SetPointSource.Types.EVENT_HANDLER;
    // }

}
