<template>
  <div class="container-fluid">
    <div class="col-md-9 mainbar">
      <h1 class="col-xs-12">{{ $t("systemsettings.title") }}</h1>
      <div v-if="auditEventTypes" class="col-md-6">
        <div class="row align-items-center">
          <h2 class="col-xs-11">{{ $t("systemsettings.audit.title") }}</h2>
          <div class="col-xs-1" v-if="activeTimeoutAudit">
            <btn size="lg" type="link">
              <i class="glyphicon glyphicon-floppy-disk saving"></i>
            </btn>
          </div>
        </div>

        <div>
          <div
            class="row"
            v-for="event in auditEventTypes"
            v-bind:key="event.i1"
          >
            <div class="col-xs-6">
              <p>{{ $t(event.translation) }}</p>
            </div>
            <div class="col-xs-5">
              <select
                class="form-control"
                v-model="event.i2"
                @input="saveAuditEventAlarms()"
              >
                <option v-bind:value="0">{{ $t("alarmlevels.none") }}</option>
                <option v-bind:value="1">
                  {{ $t("alarmlevels.information") }}
                </option>
                <option v-bind:value="2">{{ $t("alarmlevels.urgent") }}</option>
                <option v-bind:value="3">
                  {{ $t("alarmlevels.critical") }}
                </option>
                <option v-bind:value="4">
                  {{ $t("alarmlevels.lifesafety") }}
                </option>
              </select>
            </div>
            <div class="col-xs-1">
              <img
                v-if="event.i2 === 1"
                src="images/flag_blue.png"
                title="Information"
                alt="Information"
              />
              <img
                v-if="event.i2 === 2"
                src="images/flag_yellow.png"
                title="Urgent"
                alt="Urgent"
              />
              <img
                v-if="event.i2 === 3"
                src="images/flag_orange.png"
                title="Critical"
                alt="Critical"
              />
              <img
                v-if="event.i2 === 4"
                src="images/flag_red.png"
                title="Life Safety"
                alt="Life Safety"
              />
            </div>
          </div>
        </div>
      </div>
      <div v-if="systemEventTypes" class="col-md-6">
        <div class="row align-items-center">
          <h2 class="col-xs-11">{{ $t("systemsettings.event.title") }}</h2>
          <div class="col-xs-1" v-if="activeTimeoutEvent">
            <btn size="lg" type="link">
              <i class="glyphicon glyphicon-floppy-disk saving"></i>
            </btn>
          </div>
        </div>
        <div
          class="row"
          v-for="event in systemEventTypes"
          v-bind:key="event.i1"
        >
          <div class="col-xs-6">
            <p>{{ $t(event.translation) }}</p>
          </div>
          <div class="col-xs-5">
            <select
              class="form-control"
              v-model="event.i2"
              @input="saveSystemEventAlarms()"
            >
              <option v-bind:value="0">{{ $t("alarmlevels.none") }}</option>
              <option v-bind:value="1">
                {{ $t("alarmlevels.information") }}
              </option>
              <option v-bind:value="2">{{ $t("alarmlevels.urgent") }}</option>
              <option v-bind:value="3">{{ $t("alarmlevels.critical") }}</option>
              <option v-bind:value="4">
                {{ $t("alarmlevels.lifesafety") }}
              </option>
            </select>
          </div>
          <div class="col-xs-1">
            <img
              v-if="event.i2 === 1"
              src="images/flag_blue.png"
              title="Information"
              alt="Information"
            />
            <img
              v-if="event.i2 === 2"
              src="images/flag_yellow.png"
              title="Urgent"
              alt="Urgent"
            />
            <img
              v-if="event.i2 === 3"
              src="images/flag_orange.png"
              title="Critical"
              alt="Critical"
            />
            <img
              v-if="event.i2 === 4"
              src="images/flag_red.png"
              title="Life Safety"
              alt="Life Safety"
            />
          </div>
        </div>
      </div>
      <div v-if="emailSettings" class="col-md-6">
        <div class="row align-items-center">
          <h2 class="col-xs-11">{{ $t("systemsettings.email.title") }}</h2>
          <div class="col-xs-1">
            <btn size="lg" type="link" @click="saveEmailSettings()">
              <i class="glyphicon glyphicon-floppy-disk"></i>
            </btn>
          </div>
        </div>

        <div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.email.smtp.host") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.host"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.email.smtp.port") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="number"
                v-model="emailSettings.port"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.email.address") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.from"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.email.from") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.name"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.email.contenttype") }}</p>
            </div>
            <div class="col-xs-6">
              <select class="form-control" v-model="emailSettings.contentType">
                <option v-bind:value="0">
                  {{ $t("systemsettings.email.contenttype.htmltext") }}
                </option>
                <option v-bind:value="1">
                  {{ $t("systemsettings.email.contenttype.html") }}
                </option>
                <option v-bind:value="2">
                  {{ $t("systemsettings.email.contenttype.text") }}
                </option>
              </select>
            </div>
          </div>
          <div class="row no-gutters">
            <div class="col-xs-12">
              <btn-group class="col-xs-12">
                <btn
                  input-type="radio"
                  :input-value="true"
                  v-model="emailSettings.auth"
                  class="col-xs-6"
                  >{{ $t("systemsettings.email.auth.enable") }}</btn
                >
                <btn
                  input-type="radio"
                  :input-value="false"
                  v-model="emailSettings.auth"
                  class="col-xs-6"
                  >{{ $t("systemsettings.email.auth.disable") }}</btn
                >
              </btn-group>
            </div>
          </div>
          <div v-if="emailSettings.auth">
            <div class="row">
              <div class="col-xs-6">
                <p>{{ $t("systemsettings.email.auth.username") }}</p>
              </div>
              <div class="col-xs-6">
                <input
                  class="form-control"
                  type="text"
                  v-model="emailSettings.username"
                />
              </div>
            </div>
            <div class="row">
              <div class="col-xs-6">
                <p>{{ $t("systemsettings.email.auth.password") }}</p>
              </div>
              <div class="col-xs-6">
                <div class="col-xs-2">
                  <btn @click="togglePassword('emailPasswordInput')">
                    <i class="glyphicon glyphicon-eye-open"></i>
                  </btn>
                </div>
                <div class="col-xs-10">
                  <input
                    id="emailPasswordInput"
                    class="form-control"
                    type="password"
                    v-model="emailSettings.password"
                  />
                </div>
              </div>
            </div>
          </div>
          <div class="row no-gutters">
            <div class="col-xs-12">
              <btn-group class="col-xs-12">
                <btn
                  input-type="radio"
                  :input-value="true"
                  v-model="emailSettings.tls"
                  class="col-xs-6"
                  >{{ $t("systemsettings.email.tls.enable") }}</btn
                >
                <btn
                  input-type="radio"
                  :input-value="false"
                  v-model="emailSettings.tls"
                  class="col-xs-6"
                  >{{ $t("systemsettings.email.tls.disable") }}</btn
                >
              </btn-group>
            </div>
          </div>
        </div>
      </div>
      <div v-if="httpSettings" class="col-md-6">
        <div class="row align-items-center">
          <h2 class="col-xs-11">{{ $t("systemsettings.http.title") }}</h2>
          <div class="col-xs-1">
            <btn size="lg" type="link" @click="saveHttpSettings()">
              <i class="glyphicon glyphicon-floppy-disk"></i>
            </btn>
          </div>
        </div>
        <div>
          <div class="row no-gutters">
            <div class="col-xs-12 no-gutters">
              <btn-group class="col-xs-12 no-gutters">
                <btn
                  input-type="radio"
                  :input-value="true"
                  class="col-xs-6"
                  v-model="httpSettings.useProxy"
                  >{{ $t("systemsettings.http.proxy.enable") }}</btn
                >
                <btn
                  input-type="radio"
                  :input-value="false"
                  class="col-xs-6"
                  v-model="httpSettings.useProxy"
                  >{{ $t("systemsettings.http.proxy.disable") }}</btn
                >
              </btn-group>
            </div>
          </div>
        </div>
        <div v-if="httpSettings.useProxy">
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.http.proxy.host") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="httpSettings.host"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.http.proxy.port") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="number"
                v-model="httpSettings.port"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.http.proxy.username") }}</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="httpSettings.username"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.http.proxy.password") }}</p>
            </div>
            <div class="col-xs-6">
              <div class="col-xs-2">
                <btn @click="togglePassword('httpPasswordInput')">
                  <i class="glyphicon glyphicon-eye-open"></i>
                </btn>
              </div>
              <div class="col-xs-10">
                <input
                  id="httpPasswordInput"
                  class="form-control"
                  type="password"
                  v-model="httpSettings.password"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="miscSettings" class="col-md-6">
        <div class="row align-items-center">
          <h2 class="col-xs-10">{{ $t("systemsettings.misc.title") }}</h2>
          <div class="col-xs-1">
            <btn size="lg" type="link" @click="purgeData()">
              <i class="glyphicon glyphicon-trash"></i>
            </btn>
          </div>
          <div class="col-xs-1" v-if="activeTimeoutMisc">
            <btn size="lg" type="link">
              <i class="glyphicon glyphicon-floppy-disk saving"></i>
            </btn>
          </div>
        </div>
        <div>
          <div class="row">
            <div class="col-xs-6">
              <p>{{ $t("systemsettings.misc.performance") }}</p>
            </div>
            <div class="col-xs-6">
              <select
                class="form-control"
                v-model="miscSettings.uiPerformance"
                @input="saveMiscSettings()"
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
                @change="saveMiscSettings()"
              />
            </div>
            <div class="col-xs-3">
              <select
                class="form-control"
                v-model="miscSettings.eventPurgePeriodType"
                @input="saveMiscSettings()"
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
                @change="saveMiscSettings()"
              />
            </div>
            <div class="col-xs-3">
              <select
                class="form-control"
                v-model="miscSettings.reportPurgePeriodType"
                @input="saveMiscSettings()"
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
                @change="saveMiscSettings()"
              />
            </div>
            <div class="col-xs-3">
              <select
                class="form-control"
                v-model="miscSettings.futureDateLimitPeriodType"
                @input="saveMiscSettings()"
              >
                <option v-bind:value="2">{{ $t("timeperiod.minutes") }}</option>
                <option v-bind:value="3">{{ $t("timeperiod.hours") }}</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-3 sidebar">
      <div v-if="systemInfoSettings" class="col-xs-12">
        <div class="row">
          <h2 class="col-xs-12">
            <IsAlive
              class="col-xs-12"
              :plabel="$t('systemsettings.info.title')"
              :ptime-refresh="4000"
              :ptime-warning="9000"
              :ptime-error="11000"
            ></IsAlive>
          </h2>
        </div>
        <div class="row" v-if="systemRunningTime">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.info.systemtime") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ systemRunningTime }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.info.milestone") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ $store.getters.appMilestone }} build
              {{ $store.getters.appBuild }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.info.branch") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ $store.getters.appBranch }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.info.tag") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ $store.getters.appTag }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.info.uiversion") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ $store.getters.appVersion }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>{{ $t("systemsettings.info.instance") }}</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="systemInfoSettings.instanceDescription"
              @change="saveSystemInfoSettings()"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>{{ $t("systemsettings.info.language") }}</p>
          </div>
          <div class="col-xs-6">
            <select
              class="form-control"
              v-model="systemInfoSettings.language"
              @input="saveSystemInfoSettings()"
            >
              <option value="de">Deutsch</option>
              <option value="en">English</option>
              <!-- <option value="ru">Руссиан</option>
            <option value="fi">Suomi</option>
            <option value="pl">Polski</option> -->
            </select>
          </div>
        </div>
      </div>
      <div v-if="databaseType" class="col-xs-12">
        <div class="row">
          <h2 class="col-xs-10">{{ $t("systemsettings.database.title") }}</h2>
          <btn
            class="col-xs-2"
            size="lg"
            type="link"
            @click="getDatabaseSize()"
          >
            <i class="glyphicon glyphicon-refresh"></i>
          </btn>
        </div>
        <div class="row">
          <div class="col-xs-4">
            <p>{{ $t("systemsettings.database") }}</p>
          </div>
          <div class="col-xs-8">
            <btn-group class="col-xs-12" justified>
              <btn
                input-type="radio"
                input-value="mysql"
                class="col-xs-6"
                v-model="databaseType"
                @click="saveDatabase('mysql')"
                >{{ $t("systemsettings.database.mysql") }}</btn
              >
              <btn
                disabled
                input-type="radio"
                input-value="derby"
                class="col-xs-6"
                v-model="databaseType"
                @click="saveDatabase('derby')"
                >{{ $t("systemsettings.database.derby") }}</btn
              >
            </btn-group>
          </div>
        </div>
        <div class="row" v-if="schemaVersion">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.schemaVersion") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ schemaVersion }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.size") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ databaseInfo.databaseSize }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.file") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ databaseInfo.filedataCount }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.total") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ databaseInfo.filedataSize }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.histroy") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ databaseInfo.historyCount }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-8">
            <p>{{ $t("systemsettings.database.event") }}</p>
          </div>
          <div class="col-xs-4">
            <p>
              {{ databaseInfo.eventCount }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-12">
            <p>{{ $t("systemsettings.database.top") }}</p>
          </div>
          <div
            class="col-xs-12"
            v-for="point in databaseInfo.topPoints"
            v-bind:key="point.pointId"
          >
            <p>
              {{ point.pointName }} (Point ID: {{ point.pointId }} - count:
              {{ point.count }})
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import store from "../store";
import i18n from "../i18n";
import IsAlive from "../components/graphical_views/IsAlive";

export default {
  el: "#systemsettings",
  name: "systemsettings",
  components: {
    IsAlive,
  },
  data() {
    return {
      activeTimeoutAudit: undefined,
      activeTimeoutEvent: undefined,
      activeTimeoutMisc: undefined,
      TIMEOUT_DELAY_SHORT: 1500,
      systemRunningTime: undefined,
    };
  },
  mounted() {
    store.dispatch("getAllSettings");
    this.initDatabaseSection();
    this.loadClock();
  },
  methods: {
    initDatabaseSection() {
      store.dispatch("getDatabaseSize");
      store.dispatch("getSchemaVersion");
    },
    purgeData() {
      this.$confirm({
        title: i18n.t("systemsettings.alert.purgedata.title"),
        content: i18n.t("systemsettings.alert.purgedata"),
      }).then(() => {
        store.dispatch("purgeData").then((resp) => {
          if (resp === true) {
            this.getDatabaseSize();
            this.generateNotification(
              "success",
              i18n.t("systemsettings.notification.purgedata")
            );
          }
        });
      });
    },
    saveSystemInfoSettings() {
      this.$store.dispatch("saveSystemInfoSettings").then((resp) => {
        if (resp) {
          i18n.locale = this.systemInfoSettings.language;
          this.generateNotification(
            "success",
            i18n.t("systemsettings.notification.save.systeminfo")
          );
        } else {
          this.generateNotification(
            "danger",
            i18n.t("systemsettings.notification.fail")
          );
        }
      });
    },
    saveEmailSettings() {
      this.$store.dispatch("saveEmailSettings").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            i18n.t("systemsettings.notification.save.email")
          );
        } else {
          this.generateNotification(
            "danger",
            i18n.t("systemsettings.notification.fail")
          );
        }
      });
    },
    saveHttpSettings() {
      this.$store.dispatch("saveHttpSettings").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            i18n.t("systemsettings.notification.save.http")
          );
        } else {
          this.generateNotification(
            "danger",
            i18n.t("systemsettings.notification.fail")
          );
        }
      });
    },
    saveMiscSettings() {
      if (this.activeTimeoutMisc != undefined) {
        clearTimeout(this.activeTimeoutMisc);
      }
      this.activeTimeoutMisc = setTimeout(() => {
        this.$store.dispatch("saveMiscSettings").then((resp) => {
          if (resp) {
            this.generateNotification(
              "success",
              i18n.t("systemsettings.notification.save.misc")
            );
          } else {
            this.generateNotification(
              "danger",
              i18n.t("systemsettings.notification.fail")
            );
          }
        });
        this.activeTimeoutMisc = null;
      }, this.TIMEOUT_DELAY_SHORT);
    },
    saveSystemEventAlarms() {
      if (this.activeTimeoutEvent != undefined) {
        clearTimeout(this.activeTimeoutEvent);
      }
      this.activeTimeoutEvent = setTimeout(() => {
        this.$store.dispatch("saveSystemEventTypes").then((resp) => {
          if (resp) {
            this.generateNotification(
              "success",
              i18n.t("systemsettings.notification.save.systemevent")
            );
          } else {
            this.generateNotification(
              "danger",
              i18n.t("systemsettings.notification.fail")
            );
          }
        });
        this.activeTimeoutEvent = null;
      }, this.TIMEOUT_DELAY_SHORT);
    },
    saveAuditEventAlarms() {
      if (this.activeTimeoutAudit != undefined) {
        clearTimeout(this.activeTimeoutAudit);
      }
      this.activeTimeoutAudit = setTimeout(() => {
        this.$store.dispatch("saveAuditEventTypes").then((resp) => {
          if (resp) {
            this.generateNotification(
              "success",
              i18n.t("systemsettings.notification.save.auditevent")
            );
          } else {
            this.generateNotification(
              "danger",
              i18n.t("systemsettings.notification.fail")
            );
          }
        });
        this.activeTimeoutAudit = null;
      }, this.TIMEOUT_DELAY_SHORT);
    },
    saveDatabase(databaseType) {
      this.$store.dispatch("saveDatabaseType", databaseType).then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            i18n.t("systemsettings.notification.save.database")
          );
        } else {
          this.generateNotification(
            "danger",
            i18n.t("systemsettings.notification.fail")
          );
        }
      });
    },
    generateNotification(type, content) {
      this.$notify({
        placement: "bottom-right",
        type,
        content,
      });
    },
    togglePassword(elementId) {
      let x = document.getElementById(elementId);
      if (x.type === "password") {
        x.type = "text";
      } else {
        x.type = "password";
      }
    },
    async loadClock() {
      console.debug("Loading clock");
      let result = await store.dispatch("getSystemStartupTime");
      console.debug("Startging clock");
      if (result) {
        this.runClock();
      }
    },
    runClock() {
      setInterval(() => {
        let now = new Date().getTime();
        console.log(now);
        console.log(this.systemStartupTime);
        console.log(this.systemStartupTime.getTime());
        let distance = now - this.systemStartupTime.getTime();
        console.log(distance);
        let days = Math.floor(distance / (1000 * 60 * 60 * 24));
        let hours = Math.floor(
          (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        );
        let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        let seconds = Math.floor((distance % (1000 * 60)) / 1000);
        this.systemRunningTime = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      }, 1000);
    },
  },
  computed: {
    databaseType() {
      return this.$store.state.systemSettings.databaseType;
    },
    databaseInfo() {
      return this.$store.state.systemSettings.databaseInfo;
    },
    systemInfoSettings() {
      return this.$store.state.systemSettings.systemInfoSettings;
    },
    emailSettings() {
      return this.$store.state.systemSettings.emailSettings;
    },
    httpSettings() {
      return this.$store.state.systemSettings.httpSettings;
    },
    miscSettings() {
      return this.$store.state.systemSettings.miscSettings;
    },
    auditEventTypes() {
      return this.$store.state.systemSettings.auditEventTypes;
    },
    systemEventTypes() {
      return this.$store.state.systemSettings.systemEventTypes;
      //      3) Add notifications when something went wrong
    },
    systemStartupTime() {
      return this.$store.state.systemSettings.systemStartupTime;
    },
    schemaVersion() {
      return this.$store.state.systemSettings.schemaVersion;
    },
  },
};
</script>
<style scoped>
.saving {
  animation: fadeinfadeout 2s;
  animation-iteration-count: infinite;
}
.sidebar {
  background-color: #23232e;
  color: white;
  height: 98vh;
  overflow-y: auto;
}
.mainbar {
  height: 98vh;
  overflow-y: auto;
}
@keyframes fadeinfadeout {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}
</style>