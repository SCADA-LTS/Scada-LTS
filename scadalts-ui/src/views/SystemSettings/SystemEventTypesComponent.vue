<template>
  <div class="col-md-6">
    <div class="row align-items-center">
      <h2 class="col-xs-12">
        {{ $t("systemsettings.event.title")
        }}<span v-if="isSystemEventEdited">*</span>
      </h2>
    </div>
    <div class="row" v-for="event in systemEventTypes" v-bind:key="event.i1">
      <div class="col-xs-6">
        <p>{{ $t(event.translation) }}</p>
      </div>
      <div class="col-xs-5">
        <select
          class="form-control"
          v-model="event.i2"
          @change="watchDataChange()"
          @input="watchDataChange()"
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
</template>
<script>
import { object } from "@amcharts/amcharts4/core";
import i18n from "../../i18n";

export default {
  name: "SystemEventTypesComponent",

  data() {
    return {
      systemEventTypes: undefined,
      systemEventTypesStore: undefined,
      isSystemEventEdited: false,
    };
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      this.systemEventTypes = await this.$store.dispatch("getSystemEventTypes");
      this.systemEventTypesStore = this.copyDataFromStore();
    },

    saveData() {
      console.log("Saved System!");
      this.$store.commit("setSystemEventTypes", this.systemEventTypes);
      this.systemEventTypesStore = this.copyDataFromStore();
      this.$store
        .dispatch("saveSystemEventTypes")
        .then((resp) => {
          if (resp) {
            this.restoreData();
            this.$notify({
              placement: "top-right",
              type: "success",
              content: i18n.t("systemsettings.notification.save.systemevent"),
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
      console.log("Restored System!");
      this.fetchData();
      this.isSystemEventEdited = false;
    },

    copyDataFromStore() {
      return JSON.parse(
        JSON.stringify(this.$store.state.systemSettings.systemEventTypes)
      );
    },

    async watchDataChange() {
      this.isSystemEventEdited = await this.isDataChanged();
      this.emitData(this.isSystemEventEdited);
    },

    async isDataChanged() {
      for (let i = 0; i < this.systemEventTypes.length; i++) {
        if (
          !(await this.$store.dispatch("configurationEqual", {
            object1: this.systemEventTypes[i],
            object2: this.systemEventTypesStore[i],
          }))
        )
          return true;
      }
      return false;
    },

    emitData(changed) {
      this.$emit("changed", {
        component: "systemEventTypesComponent",
        title: "systemsettings.event.title",
        changed: changed,
        data: this.sumarizeDataChanges(),
      });
    },

    sumarizeDataChanges() {
      let data = [];
      for (let i = 0; i < this.systemEventTypes.length; i++) {
        if (this.systemEventTypes[i].i2 !== this.systemEventTypesStore[i].i2) {
          data.push({
            label: this.systemEventTypes[i].translation,
            originalData: this.convertInfoLevel(
              this.systemEventTypesStore[i].i2
            ),
            changedData: this.convertInfoLevel(this.systemEventTypes[i].i2),
          });
        }
      }
      return data;
    },

    convertInfoLevel(value) {
      switch (Number(value)) {
        case 0:
          return i18n.t("alarmlevels.none");
          break;
        case 1:
          return i18n.t("alarmlevels.information");
          break;
        case 2:
          return i18n.t("alarmlevels.urgent");
          break;
        case 3:
          return i18n.t("alarmlevels.critical");
          break;
        case 4:
          return i18n.t("alarmlevels.lifesafety");
          break;
      }
      return value;
    },
  },
};
</script>
<style></style>
