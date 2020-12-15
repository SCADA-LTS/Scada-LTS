<template>
  <div>
    <v-container fluid>
      <v-row align="center">
        <v-col cols="8" xs="12">
          <h1>{{$t("plcalarms.notification.title")}}</h1>
        </v-col>
        <v-col cols="1">
          <v-btn elevation="2" fab dark color="primary" v-if="changes.length !== 0" @click="saveConfiguration">
            <v-icon>mdi-content-save</v-icon>
          </v-btn>
        </v-col>
        <v-col cols="3" xs="12">
          <v-select
          @change="changeMailingList"
          v-model="activeMailingList"
          :items="mailingLists"
          item-value="id"
          item-text="name"
          :label="$t('plcalarms.notification.select.mailinglist')"
          solo dense></v-select>
        </v-col>
      </v-row>      
    </v-container>

    <v-treeview dense :items="items" activatable :load-children="fetchDataPoints">
      <template v-slot:append="{ item }">
        <v-row align="center" class="d-flex" v-if="!item.children">
          <v-checkbox 
            v-model="item.mail.active" 
            on-icon="mdi-email"
            off-icon="mdi-email-outline"
            @click="watchPointChange(item)"></v-checkbox>
            <!-- mdi-Android-messages as alternative -->
          <v-checkbox 
            v-model="item.sms.active" 
            on-icon="mdi-cellphone"
            off-icon="mdi-cellphone-off"
            @click="watchPointChange(item)" disabled></v-checkbox>
        </v-row>
      </template>
    </v-treeview>
  </div>
</template>
<script>
export default {
  name: "AlarmNotifications",

  data() {
    return {
      items: [ ],
      activeMailingList: undefined,
      mailingLists: undefined,
      eventHandlers: undefined,
      changes: [],
    };
  },

  mounted() {
    this.initMailingLists();
    this.initEventHandlers();
  },

  methods: {
    async initDataSources() {
      let ds = await this.$store.dispatch("getAllPlcDataSources");
      ds.forEach(e => {
        let item = { id: e.id, name: e.name, children: [] };
        this.items.push(item);
      });
    },

    async initMailingLists() {
      this.mailingLists = await this.$store.dispatch("getAllMailingLists");
    },

    async initEventHandlers() {
      this.eventHandlers = await this.$store.dispatch("getPlcEventHandlers");
    },

    async fetchDataPoints(item) {
      console.log(item);
      let dp = await this.$store.dispatch("getPlcDataPoints", item.id);
      dp.forEach(e => {
        let i = { id: e.id, name: e.name, mail: {}, sms: {}};
        i.mail.handler = this.bindEmailEventHandler(e.id);
        i.mail.active = i.mail.config = i.mail.handler !== -1;
        i.sms.handler = this.bindSmsEventHandler(e.id);
        i.sms.active = i.sms.config = i.sms.handler !== -1;
        
        console.log(i);
        item.children.push(i);
      })
    },

    bindEmailEventHandler(datapointId) {
      let find = -1;
      this.eventHandlers.forEach(eh => {
        if(eh.eventTypeRef1 == datapointId) {
          if(!!eh.recipients) {
            eh.recipients.forEach(r => {
              if(r.referenceId == this.activeMailingList) {
                find = { ehId: eh.id, edId: eh.eventTypeRef2 };
              }
            })
          }
        }
      })
      return find;
    },

    bindSmsEventHandler(datapointId) {
      /** @TODO When SmsEventHandler will be ready add this logic */
      return -1;
    },

    changeMailingList(item) {
      this.items = [];
      this.changes = [];
      this.initDataSources();
    },

    watchPointChange(item) {
      this.changes = this.changes.filter(element => {return element.id !== item.id});
      if((item.mail.active !== item.mail.config) || (item.sms.active !== item.sms.config)) {
        this.changes.push(item);
      }
    },

    saveConfiguration() {
      if(this.changes.length > 0) {
        this.changes.forEach(change => {
          if (change.mail.handler !== -1) {
            this.updateEventHandler(
              change.mail.handler.ehId,
              this.activeMailingList,
              change.id,
              change.mail.handler.edId,
              "delete"
            );
          } else {
            let data = this.getExistingEventHandler(change.id);
            if(!!data) {
              this.updateEventHandler(
                data.ehId, 
                this.activeMailingList, 
                change.id, 
                data.edId, 
                "add"
              );
            } else {
              this.createEmailEventHandler(this.activeMailingList, change.id);
            }
          }
        })
      }
    },

    async updateEventHandler(ehId, mlId, dpId, edId, method) {
      let updateData = {
        ehId: ehId, activeMailingList: mlId, typeRef1: dpId, typeRef2: edId, method: method
      };
      await this.$store.dispatch("updateEventHandler", updateData);
      this.initEventHandlers();
      this.changes = [];
    },

    async createEmailEventHandler(mlId, dpId) {
      let createData = {
        datapointId: dpId, mailingListId: mlId
      };
      await this.$store.dispatch("createEmailEventHandler", createData);
      this.initEventHandlers();
      this.changes = [];
    },

    getExistingEventHandler(datapointId) {
      let result = null;
      this.eventHandlers.forEach(eh => {
        if(eh.eventTypeRef1 == datapointId) {
          result = { ehId: eh.id, edId: eh.eventTypeRef2 }
        }
      })
      return result;
    },

  },
};
</script>
<style>
</style>