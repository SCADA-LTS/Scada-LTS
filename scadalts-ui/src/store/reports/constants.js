import i18n from '@/i18n';

export const ALARM_OPTIONS = [
    { label: i18n.t("reports.events.none"), value: 1 },
    { label: i18n.t("reports.events.alarms"), value: 2 },
    { label: i18n.t("reports.events.all"), value: 3 },
];

export const DATE_RANGE_TYPE_OPTIONS = [
    { label: i18n.t("reports.specificDates"), value:2 },
    { label: i18n.t("reports.relative"), value:1 }
];
