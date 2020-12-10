<template>
  <div>
    <v-container fluid>
      <v-row align="center">
        <v-col cols="8" xs="12">
          <h1>PLC Alarms Notification List</h1>
        </v-col>
        <v-col cols="1">
          <v-btn elevation="2" fab dark color="primary">
            <v-icon>mdi-plus</v-icon>
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
          <v-btn elevation="2" fab x-small dark color="primary" @click="send(item)">
            <v-icon>mdi-content-save</v-icon>
          </v-btn>
          <v-checkbox :value="open"></v-checkbox>
          <v-checkbox></v-checkbox>
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
      eventDetectorId: 5,
      activeMailingList: undefined,
      mailingLists: undefined,
    };
  },

  mounted() {
    this.initMailingLists();
    this.initDataSources();
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

    async fetchUsers(item) {
      console.log(item);
      let dp = await this.$store.dispatch("getPlcDataPoints", item.id);
      dp.forEach(e => {
        let i = { id: e.id, name: e.name };
        item.children.push(i);
      })
    },

    changeMailingList(item) {
      console.log("MailingListChanged!");
      console.log(item);
    },

    send(item) {
      let payload = {
        datapointId: item.id,
        eventDetectorId: this.eventDetectorId,
        mailingListId: this.activeMailingList
      }
      this.$store.dispatch("createEmailEventHandler", payload);
    }


  },
};
</script>
<style>
</style>