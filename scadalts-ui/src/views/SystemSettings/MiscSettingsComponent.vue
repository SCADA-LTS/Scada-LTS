<template>
  <div class="col-md-6">
    <div class="row align-items-center">
      <h2 class="col-xs-11">
        {{ $t("systemsettings.misc.title")
        }}<span v-if="isMiscSettingsEdited">*</span>
      </h2>
      <div class="col-xs-1">
        <btn size="lg" type="link" @click="purgeData()" id="btn-purge-data">
          <i class="glyphicon glyphicon-trash"></i>
        </btn>
        <tooltip
          :text="$t('systemsettings.tooltip.purgedata')"
          target="#btn-purge-data"
        />
      </div>
    </div>
    <div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.misc.uiPerformance") }}</p>
        </div>
        <div class="col-xs-6">
          <select
            class="form-control"
            v-model="miscSettings.uiPerformance"
            @input="watchDataChange()"
          >
            <option v-bind:value="2000">
              {{ $t("systemsettings.misc.performance.high") }}
            </option>
            <option v-bind:value="5000">
              {{ $t("systemsettings.misc.performance.medium") }}
            </option>
            <option v-bind:value="10000">
              {{ $t("systemsettings.misc.performance.low") }}
            </option>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.misc.purge.events") }}</p>
        </div>
        <div class="col-xs-3">
          <input
            class="form-control"
            type="number"
            v-model="miscSettings.eventPurgePeriods"
            @change="watchDataChange()"
          />
        </div>
        <div class="col-xs-3">
          <select
            class="form-control"
            v-model="miscSettings.eventPurgePeriodType"
            @input="watchDataChange()"
          >
            <option v-bind:value="4">{{ $t("timeperiod.days") }}</option>
            <option v-bind:value="5">{{ $t("timeperiod.weeks") }}</option>
            <option v-bind:value="6">{{ $t("timeperiod.months") }}</option>
            <option v-bind:value="7">{{ $t("timeperiod.years") }}</option>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.misc.purge.reports") }}</p>
        </div>
        <div class="col-xs-3">
          <input
            class="form-control"
            type="number"
            v-model="miscSettings.reportPurgePeriods"
            @change="watchDataChange()"
          />
        </div>
        <div class="col-xs-3">
          <select
            class="form-control"
            v-model="miscSettings.reportPurgePeriodType"
            @input="watchDataChange()"
          >
            <option v-bind:value="4">{{ $t("timeperiod.days") }}</option>
            <option v-bind:value="5">{{ $t("timeperiod.weeks") }}</option>
            <option v-bind:value="6">{{ $t("timeperiod.months") }}</option>
            <option v-bind:value="7">{{ $t("timeperiod.years") }}</option>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.misc.purge.future") }}</p>
        </div>
        <div class="col-xs-3">
          <input
            class="form-control"
            type="number"
            v-model="miscSettings.futureDateLimitPeriods"
            @change="watchDataChange()"
          />
        </div>
        <div class="col-xs-3">
          <select
            class="form-control"
            v-model="miscSettings.futureDateLimitPeriodType"
            @input="watchDataChange()"
          >
            <option v-bind:value="2">{{ $t("timeperiod.minutes") }}</option>
            <option v-bind:value="3">{{ $t("timeperiod.hours") }}</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { object } from "@amcharts/amcharts4/core";
import i18n from "../../i18n";

export default {
  name: "MiscSettingsComponent",

  data() {
    return {
      miscSettings: undefined,
      miscSettingsStore: undefined,
      isMiscSettingsEdited: false,
    };
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      this.miscSettings = await this.$store.dispatch("getMiscSettings");
      this.miscSettingsStore = this.copyDataFromStore();
    },

    saveData() {
      console.log("Saved!");
      this.$store.commit("setMiscSettings", this.miscSettings);
      this.miscSettingsStore = this.copyDataFromStore();
      this.$store
        .dispatch("saveMiscSettings")
        .then((resp) => {
          if (resp) {
            this.restoreData();
            this.$notify({
              placement: "top-right",
              type: "success",
              content: i18n.t("systemsettings.notification.save.misc"),
            });
          }
        })
        .catch(() => {
          this.$notify({
            placement: "top-right",
            type: "danger",
            content: i18n.t("systemsettings.notification.fail"),
          });
        });
      // this.$store.dispatch("saveAuditEventTypes", this.auditEventTypes);
    },

    restoreData() {
      this.fetchData();
      this.isMiscSettingsEdited = false;
    },

    copyDataFromStore() {
      return JSON.parse(
        JSON.stringify(this.$store.state.systemSettings.miscSettings)
      );
    },

    async watchDataChange() {
      this.isMiscSettingsEdited = await this.isDataChanged();
      this.emitData(this.isMiscSettingsEdited);
    },

    async isDataChanged() {
      return !(await this.$store.dispatch("configurationEqual", {
        object1: this.miscSettings,
        object2: this.miscSettingsStore,
      }));
    },

    emitData(changed) {
      this.$emit("changed", {
        component: "miscSettingsComponent",
        title: "systemsettings.misc.title",
        changed: changed,
        data: this.sumarizeDataChanges(),
      });
    },

    sumarizeDataChanges() {
      let data = [];
      for (let key in this.miscSettings) {
        if (this.miscSettings[key] !== this.miscSettingsStore[key]) {
          data.push({
            label: `systemsettings.misc.${key}`,
            originalData: this.convertTimePeriod(this.miscSettingsStore[key], key),
            changedData: this.convertTimePeriod(this.miscSettings[key], key),
          });
        }
      }
      return data;
    },

    purgeData() {
      this.$confirm({
        title: i18n.t("systemsettings.alert.purgedata.title"),
        content: i18n.t("systemsettings.alert.purgedata"),
      }).then(() => {
        this.$store.dispatch("purgeData").then((resp) => {
          if (resp === true) {
            this.$notify({
              placement: "top-right",
              type: "success",
              content: i18n.t("systemsettings.notification.purgedata"),
            });
          }
        });
      });
    },

    convertTimePeriod: function (value, key) {
      if (key.includes("Type")) {
        switch (Number(value)) {
          case 2:
            return i18n.t("timeperiod.minutes");
            break;
          case 3:
            return i18n.t("timeperiod.hours");
            break;
          case 4:
            return i18n.t("timeperiod.days");
            break;
          case 5:
            return i18n.t("timeperiod.weeks");
            break;
          case 6:
            return i18n.t("timeperiod.months");
            break;
          case 7:
            return i18n.t("timeperiod.years");
            break;
        }
      }
      return value;
    },
  },
};
</script>
<style>
</style>