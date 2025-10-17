export class NotificationAlertEntry {
    constructor(text, type = 'info', color = '', icon = null) {
        this.value = true;
        this.type = type;
        this.icon = icon;
        this.color = (type === 'info' && !color) ? 'x' : color;
        this.text = text;
    }
}

export default NotificationAlertEntry;