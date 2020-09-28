<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <h1 class="col-xs-12">{{ $t("systemsettings.title") }}</h1>
    </div>
    <div v-if="systemInfoSettings" class="container-md">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.info.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveSystemInfoSettings()">
            <i class="glyphicon glyphicon-floppy-disk"></i>
          </btn>
        </div>
      </div>

      <div class="col-md-6">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.info.instance") }}</p>
        </div>
        <div class="col-xs-6">
          <input
            class="form-control"
            type="text"
            v-model="systemInfoSettings.instanceDescription"
          />
        </div>
      </div>
      <div class="col-md-6">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.info.language") }}</p>
        </div>
        <div class="col-xs-6">
          <select class="form-control" v-model="systemInfoSettings.language">
            <option value="de">Deutsch</option>
            <option value="en">English</option>
            <option value="ru">Руссиан</option>
            <option value="fi">Suomi</option>
            <option value="pl">Polski</option>
          </select>
        </div>
      </div>
    </div>
    <div v-if="systemEventTypes" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.event.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveSystemEventAlarms()">
            <i class="glyphicon glyphicon-floppy-disk"></i>
          </btn>
        </div>
      </div>
      <div>
        <div
          class="row"
          v-for="event in systemEventTypes"
          v-bind:key="event.i1"
        >
          <div class="col-xs-6">
            <p>{{ $t(event.translation) }}</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="event.i2">
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
    </div>
    <div v-if="auditEventTypes" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.audit.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveAuditEventAlarms()">
            <i class="glyphicon glyphicon-floppy-disk"></i>
          </btn>
        </div>
      </div>

      <div>
        <div class="row" v-for="event in auditEventTypes" v-bind:key="event.i1">
          <div class="col-xs-6">
            <p>{{ $t(event.translation) }}</p>
          </div>
          <div class="col-xs-5">
            <select class="form-control" v-model="event.i2">
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
    </div>
    <div v-if="httpSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.http.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveHttpSettings()">
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
            <input
              class="form-control"
              type="text"
              v-model="httpSettings.password"
            />
          </div>
        </div>
      </div>
    </div>
    <div v-if="databaseType" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.database.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="getDatabaseSize()">
            <i class="glyphicon glyphicon-refresh"></i>
          </btn>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database") }}</p>
        </div>
        <div class="col-xs-6">
          <btn-group class="col-xs-12 no-gutters">
            <btn
              input-type="radio"
              input-value="mysql"
              class="col-xs-6"
              v-model="databaseType"
              @click="saveDatabase('mysql')"
              >{{ $t("systemsettings.database.mysql") }}</btn
            >
            <btn
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
      <div class="col-xs-12" v-if="databaseInfo">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database.size") }}</p>
        </div>
        <div class="col-xs-6">
          <p>{{ databaseInfo.databaseSize }}</p>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database.file") }}</p>
        </div>
        <div class="col-xs-6">
          <p>{{ databaseInfo.filedataCount }}</p>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database.total") }}</p>
        </div>
        <div class="col-xs-6">
          <p>{{ databaseInfo.filedataSize }}</p>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database.histroy") }}</p>
        </div>
        <div class="col-xs-6">
          <p>{{ databaseInfo.historyCount }}</p>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.database.event") }}</p>
        </div>
        <div class="col-xs-6">
          <p>{{ databaseInfo.eventCount }}</p>
        </div>
      </div>
      <div class="col-xs-12">
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
    <div v-if="emailSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-11">{{ $t("systemsettings.email.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveEmailSettings()">
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
              <p>{{ $t("systemsettings.email.auth.username") }}Username</p>
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
              <p>{{ $t("systemsettings.email.auth.password") }}Password</p>
            </div>
            <div class="col-xs-6">
              <input
                class="form-control"
                type="text"
                v-model="emailSettings.password"
              />
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
    <div v-if="miscSettings" class="col-md-6">
      <div class="row align-items-center">
        <h2 class="col-xs-10">{{ $t("systemsettings.misc.title") }}</h2>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="saveMiscSettings()">
            <i class="glyphicon glyphicon-floppy-disk"></i>
          </btn>
        </div>
        <div class="col-xs-1">
          <btn block size="lg" type="link" @click="purgeData()">
            <i class="glyphicon glyphicon-trash"></i>
          </btn>
        </div>
      </div>
      <div>
        <div class="row">
          <div class="col-xs-6">
            <p>{{ $t("systemsettings.misc.performance") }}</p>
          </div>
          <div class="col-xs-6">
            <select class="form-control" v-model="miscSettings.uiPerformance">
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
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.eventPurgePeriodType"
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
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.reportPurgePeriodType"
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
            />
          </div>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="miscSettings.futureDateLimitPeriodType"
            >
              <option v-bind:value="2">{{ $t("timeperiod.minutes") }}</option>
              <option v-bind:value="3">{{ $t("timeperiod.hours") }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import store from "../store";

export default {
  el: "#systemsettings",
  name: "systemsettings",
  mounted() {
    store.dispatch("getAllSettings");
    this.getDatabaseSize();
  },
  methods: {
    getDatabaseSize() {
      store.dispatch("getDatabaseSize");
    },
    purgeData() {
      this.$confirm({
        title: "Purge data?",
        content:
          "This operation will pernamently delete datapoint history content. Continue?",
      }).then(() => {
        store.dispatch("purgeData").then((resp) => {
          if (resp === true) {
            this.getDatabaseSize();
            this.generateNotification("success", "Successfully cleared data!");
          }
        });
      });
    },
    saveSystemInfoSettings() {
      this.$store.dispatch("saveSystemInfoSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "System Info Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveEmailSettings() {
      this.$store.dispatch("saveEmailSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Email Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveHttpSettings() {
      this.$store.dispatch("saveHttpSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Http Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveMiscSettings() {
      this.$store.dispatch("saveMiscSettings").then((resp) => {
        if (resp) {
          this.generateNotification("success", "Other Settings saved!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveSystemEventAlarms() {
      this.$store.dispatch("saveSystemEventTypes").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            "System Event Types Settings saved!"
          );
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveAuditEventAlarms() {
      this.$store.dispatch("saveAuditEventTypes").then((resp) => {
        if (resp) {
          this.generateNotification(
            "success",
            "Audit Event Types Settings saved!"
          );
        } else {
          this.generateNotification("danger", "Something went wrong!");
        }
      });
    },
    saveDatabase(databaseType) {
      this.$store.dispatch("saveDatabaseType", databaseType).then((resp) => {
        if (resp) {
          this.generateNotification("success", "Database changed!");
        } else {
          this.generateNotification("danger", "Something went wrong!");
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
  },
};
</script>
<style lang="sass" scoped>
</style>