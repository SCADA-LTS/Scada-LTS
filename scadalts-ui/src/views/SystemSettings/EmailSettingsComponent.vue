<template>
  <div class="col-md-6">
    <div class="row align-items-center">
      <h2 class="col-xs-11">
        {{ $t("systemsettings.email.title")
        }}<span v-if="isEmailSettingsEdited">*</span>
      </h2>
      <div class="col-xs-1">
        <btn size="lg" type="link" @click="sendTestEmail()" id="btn-test-email">
          <i class="glyphicon glyphicon-send"></i>
        </btn>
        <tooltip
          :text="$t('systemsettings.tooltip.sendtestemail')"
          target="#btn-test-email"
        />
      </div>
    </div>

    <div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.email.host") }}</p>
        </div>
        <div class="col-xs-6">
          <input
            class="form-control"
            type="text"
            v-model="emailSettings.host"
            @change="watchDataChange()"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.email.port") }}</p>
        </div>
        <div class="col-xs-6">
          <input
            class="form-control"
            type="number"
            v-model="emailSettings.port"
            @change="watchDataChange()"
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
            @change="watchDataChange()"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.email.name") }}</p>
        </div>
        <div class="col-xs-6">
          <input
            class="form-control"
            type="text"
            v-model="emailSettings.name"
            @change="watchDataChange()"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6">
          <p>{{ $t("systemsettings.email.contentType") }}</p>
        </div>
        <div class="col-xs-6">
          <select
            class="form-control"
            v-model="emailSettings.contentType"
            @input="watchDataChange()"
          >
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
              @click="watchRadioDataChagne(true, 'auth')"
              >{{ $t("systemsettings.email.auth.enable") }}</btn
            >
            <btn
              input-type="radio"
              :input-value="false"
              v-model="emailSettings.auth"
              class="col-xs-6"
              @click="watchRadioDataChagne(false, 'auth')"
              >{{ $t("systemsettings.email.auth.disable") }}</btn
            >
          </btn-group>
        </div>
      </div>
      <div v-if="emailSettings.auth">
        <div class="row">
          <div class="col-xs-6">
            <p>{{ $t("systemsettings.email.username") }}</p>
          </div>
          <div class="col-xs-6">
            <input
              class="form-control"
              type="text"
              v-model="emailSettings.username"
              @change="watchDataChange()"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6">
            <p>{{ $t("systemsettings.email.password") }}</p>
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
                @change="watchDataChange()"
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
              @click="watchRadioDataChagne(true, 'tls')"
              >{{ $t("systemsettings.email.tls.enable") }}</btn
            >
            <btn
              input-type="radio"
              :input-value="false"
              v-model="emailSettings.tls"
              class="col-xs-6"
              @click="watchRadioDataChagne(false, 'tls')"
              >{{ $t("systemsettings.email.tls.disable") }}</btn
            >
          </btn-group>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { object } from "@amcharts/amcharts4/core";
import i18n from "../../i18n";

export default {
  name: "EmailSettingsComponent",

  data() {
    return {
      emailSettings: undefined,
      emailSettingsStore: undefined,
      isEmailSettingsEdited: false,
    };
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      this.emailSettings = await this.$store.dispatch("getEmailSettings");
      this.emailSettingsStore = this.copyDataFromStore();
    },

    saveData() {
      console.log("Saved Email!");
      this.$store.commit("setEmailSettings", this.emailSettings);
      this.emailSettingsStore = this.copyDataFromStore();
      this.$store
        .dispatch("saveEmailSettings")
        .then((resp) => {
          if (resp) {
            this.restoreData();
            this.$notify({
              placement: "top-right",
              type: "success",
              content: i18n.t("systemsettings.notification.save.email"),
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
      console.log("Restored Email!")
      this.fetchData();
      this.isEmailSettingsEdited = false;
    },

    copyDataFromStore() {
      return JSON.parse(
        JSON.stringify(this.$store.state.systemSettings.emailSettings)
      );
    },

    watchRadioDataChagne(value, parameter) {
        if(parameter == "auth") {
            this.emailSettings.auth = value;
        } else if (parameter == "tls") {
            this.emailSettings.tls = value;
        }
        this.watchDataChange();
    },

    async watchDataChange() {
      this.isEmailSettingsEdited = await this.isDataChanged();
      this.emitData(this.isEmailSettingsEdited);
    },

    async isDataChanged() {
      return !(await this.$store.dispatch("configurationEqual", {
          object1: this.emailSettings,
          object2: this.emailSettingsStore,
        })); 
    },

    emitData(changed) {
      this.$emit("changed", {
        component: "emailSettingsComponent",
        title: "systemsettings.email.title",
        changed: changed,
        data: this.sumarizeDataChanges(),
      });
    },

    sumarizeDataChanges() {
      let data = [];
      for (let key in this.emailSettings) {
          if(this.emailSettings[key] !== this.emailSettingsStore[key]) {
              data.push({
                  label: `systemsettings.email.${key}`,
                  originalData: this.emailSettingsStore[key],
                  changedData: this.emailSettings[key]
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

    sendTestEmail() {
        this.$store.dispatch("sendTestEmail").then(resp => {
            if(resp) {
                this.$notify({
                    placement: "top-right",
                    type: "success",
                    content: `${i18n.t("systemsettings.notification.send.email")} ${resp.recipient}`
                });
            }
        }).catch(() => {
            this.$notify({
                placement: "top-right",
                type: "danger",
                content: i18n.t("systemsettings.notification.fail")
            });
        })
    }
  },
};
</script>
<style>
</style>