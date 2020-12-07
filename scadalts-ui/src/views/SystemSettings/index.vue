<template>
  <div class="container-fluid">
    <div class="col-md-9 mainbar" v-if="isUserRoleAdmin">
      <h1 class="col-xs-12">
        {{ $t("systemsettings.title") }}
        <btn
          v-if="componentsEdited.length > 0"
          type="primary"
          @click="openModal = !openModal"
          class="floated-right"
        >
          {{ $t("systemsettings.label.save") }} <i class="col-xs-2 glyphicon glyphicon-floppy-disk"></i>
        </btn>
      </h1>
      <AuditEventTypesComponent ref="auditEventTypesComponent" @changed="componentChanged"></AuditEventTypesComponent>
      <SystemEventTypesComponent ref="systemEventTypesComponent" @changed="componentChanged"></SystemEventTypesComponent>
      <EmailSettingsComponent ref="emailSettingsComponent" @changed="componentChanged"></EmailSettingsComponent>
      <HttpSettingsComponent ref="httpSettingsComponent" @changed="componentChanged"></HttpSettingsComponent>
      <MiscSettingsComponent ref="miscSettingsComponent" @changed="componentChanged"></MiscSettingsComponent>
      <DefaultLoggingTypeSettingsComponent ref="defaultLoggingTypeSettingsComponent" @changed="componentChanged"></DefaultLoggingTypeSettingsComponent>
      <!-- 'Configuration' components are not changable instead of 'Settings' components -->
      <ScadaConfigurationComponent></ScadaConfigurationComponent>
    </div>
    <div class="col-md-3 sidebar" v-if="isUserRoleAdmin">
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
            @click="initDatabaseSection()"
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
                id="btn-db-derby"
                input-type="radio"
                input-value="derby"
                class="col-xs-6"
                v-model="databaseType"
                @click="saveDatabase('derby')"
                >{{ $t("systemsettings.database.derby") }}</btn
              >
            </btn-group>
            <tooltip :text="$t('systemsettings.tooltip.derbydatabase')" target="#btn-db-derby"/>
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
    <modal v-model="openModal" :title="$t('systemsettings.label.summary')" size="lg">
      <div v-if="componentsEdited">
        
        <div v-for="component in componentsEdited" :key="component" class="col-xs-12">
          <h4>{{$t(component.title)}}</h4>
          <div class="col-xs-12">
            <btn type="link" class="col-xs-6" @click="restoreComponent(component.component)">
              <i class="glyphicon glyphicon-floppy-remove"></i> {{ $t("systemsettings.label.restore") }}
            </btn>
            <btn type="link" class="col-xs-6" @click="saveComponent(component.component)">
              <i class="glyphicon glyphicon-floppy-saved"></i> {{ $t("systemsettings.label.save") }}
            </btn>
          </div>
          <div v-for="e in component.data" :key="e" class="col-xs-12">
            <p class="col-xs-4">{{$t(e.label)}}</p>
            <p class="col-xs-4 red">{{e.originalData | blank | convert}} </p>
            <p class="col-xs-4 green">{{e.changedData | blank | convert}}</p>
          </div>
        </div>

      </div>
      <div slot="footer">
        <btn @click="openModal = false">{{$t("uiv.modal.cancel")}}</btn>
        <btn type="info" @click="restoreAllComponents()">{{ $t("systemsettings.label.restoreall") }}</btn>
        <btn type="success" @click="saveAllComponents()">{{ $t("systemsettings.label.saveall") }}</btn>
      </div>
    </modal>
    <div class="col-md-12"  v-if="!isUserRoleAdmin">
      <p class="alert">Not allowed to see that page</p>
    </div>
  </div>
</template>
<script>
import store from "../../store";
import i18n from "../../i18n";
import IsAlive from "../../components/graphical_views/IsAlive";
import AuditEventTypesComponent from "./AuditEventTypesComponent";
import SystemEventTypesComponent from "./SystemEventTypesComponent";
import EmailSettingsComponent from "./EmailSettingsComponent";
import HttpSettingsComponent from "./HttpSettingsComponent";
import MiscSettingsComponent from "./MiscSettingsComponent";
import DefaultLoggingTypeSettingsComponent from "./DefaultLoggTypeComponent";
import ScadaConfigurationComponent from "./ScadaConfigurationComponent";
import { keys } from "@amcharts/amcharts4/.internal/core/utils/Object";

export default {
  el: "#systemsettings",
  name: "systemsettings",
  components: {
    IsAlive,
    AuditEventTypesComponent,
    SystemEventTypesComponent,
    EmailSettingsComponent,
    HttpSettingsComponent,
    MiscSettingsComponent,
    DefaultLoggingTypeSettingsComponent,
    ScadaConfigurationComponent
  },
  filters: {
    blank: function (value) {
      if (value === "") return "---";
      return value;
    },
    convert: function (value) {
      if (typeof value === "boolean") {
        if (value) {
          return i18n.t("uiv.enable");
        } else {
          return i18n.t("uiv.disable");
        }
      }
      return value;
    },
  },
  data() {
    return {
      componentsEdited: [],
      isUserRoleAdmin: false,
      systemRunningTime: undefined,
      openModal: false,
    };
  },
  mounted() {
    if(!this.isUserRoleAdmin) {
      this.getUserRole()
    }
    store.dispatch("getSystemInfoSettings");
    this.initDatabaseSection();
    this.loadClock();
  },
  methods: {
    async getUserRole() {
      this.isUserRoleAdmin = await store.dispatch("getUserRole")
    },
    initDatabaseSection() {
      store.dispatch("getDatabaseType");
      store.dispatch("getDatabaseSize");
      store.dispatch("getSchemaVersion");
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
    async loadClock() {
      let result = await store.dispatch("getSystemStartupTime");
      if (result) {
        this.runClock();
      }
    },
    runClock() {
      setInterval(() => {
        let now = new Date().getTime();
        let distance = now - this.systemStartupTime.getTime();
        let days = Math.floor(distance / (1000 * 60 * 60 * 24));
        let hours = Math.floor(
          (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        );
        let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        let seconds = Math.floor((distance % (1000 * 60)) / 1000);
        this.systemRunningTime = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      }, 1000);
    },

    async componentChanged(object) {
      console.log(object)
      let idx = this.componentsEdited.findIndex(x => x.component == object.component);
      if(idx == -1 && object.changed) {
        this.componentsEdited.push(object)
      } else if (idx != -1 && !object.changed) {
        this.componentsEdited.splice(idx, 1);
      } else if (idx != -1 && object.changed) {
        this.componentsEdited[idx] = object;
      }
      console.log(this.componentsEdited)
    },

    saveComponent(component) {
      this.$refs[component].saveData();
      this.removeComponent(component);
    },

    saveAllComponents() {
      this.componentsEdited.forEach(e => {
        this.$refs[e.component].saveData()
      })
      this.componentsEdited = [];
      this.openModal = false;
    },

    restoreComponent(component) {
      this.$refs[component].restoreData();
      this.removeComponent(component);
    },

    restoreAllComponents() {
      this.componentsEdited.forEach(e => {
        this.$refs[e.component].restoreData()
      })
      this.componentsEdited = [];
      this.openModal = false;
    },

    removeComponent(component) {
      this.componentsEdited = this.componentsEdited.filter(x => x.component !== component);
      if(this.componentsEdited.length == 0) this.openModal = false;
    }

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
    systemStartupTime() {
      return this.$store.state.systemSettings.systemStartupTime;
    },
    schemaVersion() {
      return this.$store.state.systemSettings.schemaVersion;
    },
  },
};
</script>
<style>
body > .alert {
  z-index: 2000;
}
</style>
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
.red {
  background-color: rgba(255, 0, 0, 0.3);
}
.green {
  background-color: rgba(0, 255, 0, 0.3);
}
.modal-footer{
  border: none;
}
.floated-right {
  float: right;
  margin-top: 8px;
}
.alert {
  text-align: center;
  font-size: 2em;
  padding-top: 50px;
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