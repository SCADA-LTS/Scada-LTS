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
          <input type="text" v-model="eventDetectorId"/>
          <v-checkbox 
            v-model="item.email" 
            on-icon="mdi-email"
            off-icon="mdi-email-outline"
            @click="watchPointChange(item)"></v-checkbox>
            <!-- mdi-Android-messages as alternative -->
          <v-checkbox 
            v-model="item.sms" 
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
      eventDetectorId: 5,
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
        let i = { id: e.id, name: e.name, email: this.checkEmailCheckbox(e.id), semail:false, sms: false, ssms:false};
        i.semail = i.email;
        console.log(i);
        item.children.push(i);
      })
    },

    checkEmailCheckbox(itemId) {
      let find = false;
      this.eventHandlers.forEach(eh => {
        if(eh.eventTypeRef1 == itemId) {
          if(!!eh.recipients) {
            eh.recipients.forEach(r => {
              if(r.referenceId == this.activeMailingList) {
                find = true;
                return true;
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
      if(item.semail !== item.email) {
        this.changes.push(item);
      }

      console.log(this.changes)
    },

    save() {
      if(this.changes.length > 0) {
        this.changes.forEach(change => {
          if (change.semail) {
            //TODO: Methods for deleting active mailing list from specific EH
            console.log("delete from exisitng EH")
          } else {
            let data = this.checkEhDefinedForDp(change.id);
            if(!!data) {
              console.log(data);
              //TODO: Methods to add active mailing list to specific EH
              console.log("Append a new ML to existing EH")
            } else {
              //TODO: Create a new EH. If PED exist try to append EH for this if not create a new one.
              console.log("Create a new EH")
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


    send(item) {
      let payload = {
        datapointId: item.id,
        eventDetectorId: this.eventDetectorId,
        mailingListId: this.activeMailingList
      }
      this.$store.dispatch("createEmailEventHandler", payload);
    },


  },
};
</script>
<style>
</style>