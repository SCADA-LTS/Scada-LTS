<template>
  <div class="col-md-6">
    <div class="row align-items-center">
      <h2 class="col-xs-12">
        {{ $t("systemsettings.http.title")
        }}<span v-if="isHttpSettingsEdited">*</span>
      </h2>
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
              @click="watchRadioDataChagne(true)"
              >{{ $t("systemsettings.http.proxy.enable") }}</btn
            >
            <btn
              input-type="radio"
              :input-value="false"
              class="col-xs-6"
              v-model="httpSettings.useProxy"
              @click="watchRadioDataChagne(false)"
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
            @change="watchDataChange()"
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
            @change="watchDataChange()"
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
            @change="watchDataChange()"
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
              @change="watchDataChange()"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { object } from "@amcharts/amcharts4/core";
import i18n from "../../i18n";

export default {
  name: "HttpSettingsComponent",

  data() {
    return {
      httpSettings: undefined,
      httpSettingsStore: undefined,
      isHttpSettingsEdited: false,
    };
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      this.httpSettings = await this.$store.dispatch("getHttpSettings");
      this.httpSettingsStore = this.copyDataFromStore();
    },

    saveData() {
      console.log("Saved!");
      this.$store.commit("setHttpSettings", this.httpSettings);
      this.httpSettingsStore = this.copyDataFromStore();
      this.$store
        .dispatch("saveHttpSettings")
        .then((resp) => {
          if (resp) {
            this.restoreData();
            this.$notify({
              placement: "top-right",
              type: "success",
              content: i18n.t("systemsettings.notification.save.http"),
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
    },

    restoreData() {
      this.fetchData();
      this.isHttpSettingsEdited = false;
    },

    copyDataFromStore() {
      return JSON.parse(
        JSON.stringify(this.$store.state.systemSettings.httpSettings)
      );
    },

    watchRadioDataChagne(value) {
      this.httpSettings.useProxy = value;
      this.watchDataChange();
    },

    async watchDataChange() {
      this.isHttpSettingsEdited = await this.isDataChanged();
      this.emitData(this.isHttpSettingsEdited);
    },

    async isDataChanged() {
      return !(await this.$store.dispatch("configurationEqual", {
        object1: this.httpSettings,
        object2: this.httpSettingsStore,
      }));
    },

    emitData(changed) {
      this.$emit("changed", {
        component: "httpSettingsComponent",
        title: "systemsettings.http.title",
        changed: changed,
        data: this.sumarizeDataChanges(),
      });
    },

    sumarizeDataChanges() {
      let data = [];
      for (let key in this.httpSettings) {
        if (this.httpSettings[key] !== this.httpSettingsStore[key]) {
          data.push({
            label: `systemsettings.http.proxy.${key}`,
            originalData: this.httpSettingsStore[key],
            changedData: this.httpSettings[key],
          });
        }
      }
      return data;
    },

    togglePassword(elementId) {
      let x = document.getElementById(elementId);
      if (x.type === "password") {
        x.type = "text";
      } else {
        x.type = "password";
      }
    },
  },
};
</script>
<style></style>
