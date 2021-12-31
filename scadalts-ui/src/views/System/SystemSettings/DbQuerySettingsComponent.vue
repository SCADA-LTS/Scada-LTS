<template>
  <v-col cols="12" md="6">
    <v-card>
      <v-card-title>
        {{ $t('systemsettings.dbquery.title') }}
        <span v-if="isDbQueryEdited">*</span>
      </v-card-title>
      <v-card-text>
          <v-row>
            <v-col cols="12">
              <v-switch
                  v-model="dbQuerySettings.readEnabled"
                  :label="$t('systemsettings.dbquery.readEnabled')"
                  @click="watchRadioDataChagne(dbQuerySettings.readEnabled)"
              ></v-switch>
            </v-col>
          </v-row>
      </v-card-text>
    </v-card>

    <v-snackbar v-model="response.status" :color="response.color">
      {{ response.message }}
    </v-snackbar>
  </v-col>
</template>
<script>

export default {
  name: 'DbQuerySettingsComponent',

  data() {
    return {
      dbQuerySettings: undefined,
      dbQuerySettingsStore: undefined,
      isDbQueryEdited: false,
      response: {
        color: 'success',
        status: false,
        message: '',
      },
    };
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      this.dbQuerySettings = await this.$store.dispatch('getDbQuerySettings');
      this.dbQuerySettingsStore = this.copyDataFromStore();
    },

    saveData() {
      console.log('Saved!');
      this.$store.commit('setDbQuerySettings', this.dbQuerySettings);
      this.dbQuerySettingsStore = this.copyDataFromStore();
      this.$store
          .dispatch('saveDbQuerySettings')
          .then((resp) => {
            if (resp) {
              this.restoreData();
              this.response = {
                status: true,
                message: this.$t('systemsettings.notification.save.dbquery'),
                color: 'success',
              };
            }
          })
          .catch(() => {
            this.response = {
              status: true,
              message: this.$t('systemsettings.notification.fail'),
              color: 'danger',
            };
          });
    },

    restoreData() {
      this.fetchData();
      this.isDbQueryEdited = false;
    },

    copyDataFromStore() {
      return JSON.parse(JSON.stringify(this.$store.state.systemSettings.dbQuerySettings));
    },

    watchRadioDataChagne(value) {
      this.dbQuerySettings.readEnabled = value;
      this.watchDataChange();
    },

    async watchDataChange() {
      this.isDbQueryEdited = await this.isDataChanged();
      this.emitData(this.isDbQueryEdited);
    },

    async isDataChanged() {
      return !(await this.$store.dispatch('configurationEqual', {
        object1: this.dbQuerySettings,
        object2: this.dbQuerySettingsStore,
      }));
    },

    emitData(changed) {
      this.$emit('changed', {
        component: 'dbQuerySettingsComponent',
        title: 'systemsettings.dbquery.title',
        changed: changed,
        data: this.sumarizeDataChanges(),
      });
    },

    sumarizeDataChanges() {
      let data = [];
      for (let key in this.dbQuerySettings) {
        if (this.dbQuerySettings[key] !== this.dbQuerySettingsStore[key]) {
          data.push({
            label: `systemsettings.dbquery.${key}`,
            originalData: this.dbQuerySettingsStore[key],
            changedData: this.dbQuerySettings[key],
          });
        }
      }
      return data;
    },
  },
};
</script>
<style></style>
