# PLC Alarms Notification List

New component for preparing a Notification Configuration that allow Scada-LTS users to simply receive Alarm and Warning evnets directly to their mailboxes or phones.

## Usage

Select specific mailing list from the select box. When it is selected DataSources with Datapoints containing in their name pattern: ' AL ' or ' ST ' will be listed below. When opening the specific tree node all that point are beeing loaded from the server with thier configuration. When there is a EmailEventHandler with selected MailingList the icon related to specific datapoint will be filled with green color. Uncheck this option to enable notification for that datapoint via specified mailing list.

## Further developmnet

- Add configuration menu for default Event Detector and Event Handler.
- Add SMS notification feature. (Now it is disabled)
