<template>
  <div>
    <v-container fluid>
      <v-row align="center">
        <v-col cols="8" xs="12">
          <h1>PLC Alarms Notification List</h1>
        </v-col>
        <v-col cols="1">
          <v-btn elevation="2" fab dark color="primary" v-if="changes.length !== 0" @click="save">
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
          label="Select mailing list"
          solo dense></v-select>
        </v-col>
      </v-row>
      
    </v-container>

    
    <v-treeview dense :items="items" activatable :load-children="fetchUsers">
      <template v-slot:append="{ item, open }">
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

  //TODO: Optimization!!!

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

    async fetchUsers(item) {
      console.log(item);
      let dp = await this.$store.dispatch("getPlcDataPoints", item.id);
      dp.forEach(e => {
        let i = { id: e.id, name: e.name, mail: {active: false, config: false, handler: null}, sms: {active:false, config: false, handler: null}};
        i.mail.handler = this.checkEmailCheckbox(e.id);
        i.mail.active = i.mail.config = i.mail.handler !== -1;
        
        console.log(i);
        item.children.push(i);
      })
    },

    checkEmailCheckbox(itemId) {
      let find = -1;
      this.eventHandlers.forEach(eh => {
        if(eh.eventTypeRef1 == itemId) {
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

    changeMailingList(item) {
      console.log("MailingListChanged!");
      console.log(item);
      this.items = [];
      this.changes = [];
      this.initDataSources();
    },

    watchPointChange(item) {
      this.changes = this.changes.filter(element => {return element.id !== item.id});
      if(item.mail.active !== item.mail.config) {
        this.changes.push(item);
      }
    },

    save() {
      if(this.changes.length > 0) {
        this.changes.forEach(change => {
          if (change.mail.handler !== -1) {
            this.$store.dispatch("updateEventHandler", {
                ehId: change.mail.handler.ehId,
                activeMailingList: this.activeMailingList,
                typeRef1: change.id,
                typeRef2: change.mail.handler.edId,
                method:"delete"
              }).then(() => {this.initEventHandlers(); this.changes = []})
          } else {
            let data = this.checkEhDefinedForDp(change.id);
            if(!!data) {
              console.log(data);
              this.$store.dispatch("updateEventHandler", {
                ehId: data.ehId,
                activeMailingList: this.activeMailingList,
                typeRef1: change.id,
                typeRef2: data.edId,
                method:"add"
              }).then(() => {this.initEventHandlers(); this.changes = []})
            } else {
              let payload = {
                datapointId: change.id,
                mailingListId: this.activeMailingList
              }
              this.$store.dispatch("createEmailEventHandler", payload).then(() => {this.initEventHandlers(); this.changes = []});
            }
          }
        })
      }
    },

    checkEhDefinedForDp(datapointId) {
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