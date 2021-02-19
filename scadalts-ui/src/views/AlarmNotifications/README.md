# PLC Alarms Notification List

New component for preparing a Notification Configuration that allow Scada-LTS users to simply receive Alarm and Warning evnets directly to their mailboxes or phones.

## Usage

### Preparation

- Create a mailing list and assign users to it.  
  _(if you want to receive notification about Events that was taking place druring inactive period select **Collect inactive msg** and set up **cron** pattern for example like this one "0 */1 * \* \* ?" that will try to send past emails every one minute)_
- Create **alarm** ( AL )_ or **warning** ( ST )_ **BINARY** datapoints that will handle information about specific event.  
  _\*(it could be any name of data point but must contain this uppercase expresion AL|ST with spaces around them. For example "Test AL Datapoint" or "Point-0 ST ". Underscores are not allowed - here are examples that are not valid: "Test_AL\_", "DP ST" or "Point-0_AL")_

### Definition

- Open SMS and Email notification page  
  _(open Alarms>Alarms Notifications or use this link: /ScadaBR/app.shtm#/alarm-notifications)_

- Select mailing list for which you want to configure a notification  
  _(User is able to configure two mailing list at once, or use the second one section to compare the configuration.)_
- Expand specific datapoint and click on the phone or mail icon to set up notification channel.  
  _(There is an option to select both of them or to select none of them. Green fill color of the icon means that it is active communication channel)_
- Click Save button if everyting is ready.

### Check

- In EventHandlers page check that valid Event Handlers has been created.

### Important notes

- It works only for BINARY data points. (Other types are not suppeorted)
- When event detector exisit the new one Event Handler tries to attach to it.
- Event detector templates works only when event detector for data ponit do not exists.
- Scada use Mail2SMS function. It does not send pure SMS but Mail formatted as SMS.  
  It requires addtional SMS Gateway Server that will handle and proceed text message.  
  Server addess can be changed using System Settings page.

## Known bugs

Sometimes when user tries to save SMS and Email channel to specific datapoint at once only one is saved correctly. Simillar situation happens when user tries to toggle between SMS/Email channel. So solve that problem we suggest to do that step by step or tires to refresh the page to see which changes was applied correctly.

## Troubleshooting

When you have noticed that Inactive Emails or SMSes was not sent make sure that mailing list is configured with selected **Collect inactive msg** and CRON pattern is valid.

If it's still not working make sure that event handlers generates alarm level higher than none.

In other cases open an issue on the Scada-LTS GitHub project page or inform the development team.
