<template>
  <div class="container-fluid">
    <h1>{{ $t("plcnotifications.title") }}</h1>
    <p>Create scheduler</p>
    <tabs @change="onChange">
      <tab title="Show schedulers">
        <div class="col-xs-12">
          <p class="col-xs-1">Id</p>
          <p class="col-xs-2">Hour start</p>
          <p class="col-xs-2">Hour stop</p>
          <p class="col-xs-2">Description</p>
          <p class="col-xs-1">Mail</p>
          <p class="col-xs-1">SMS</p>
          <p class="col-xs-2">Modification Time</p>
          <p class="col-xs-1">Actions</p>
        </div>
        <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
          <p class="col-xs-1">{{ s.id }}</p>
          <p class="col-xs-2">{{ s.hourStart }}</p>
          <p class="col-xs-2">{{ s.hourStop }}</p>
          <p class="col-xs-2">{{ s.description }}</p>
          <input class="col-xs-1" type="checkbox" v-model="s.perMail" disabled/>
          <input class="col-xs-1" type="checkbox" v-model="s.perSms" disabled />
          <p class="col-xs-2">{{ s.mtime }}</p>
          <p class="col-xs-1"><button @click="toggleSchedulerModal(s.id)">More</button></p>
        </div>
      </tab>
      <tab title="Create scheduler">
        <div class="col-xs-12">
          <p class="col-xs-4">DataPoint</p>
          <p class="col-xs-2">User to bind</p>
          <p class="col-xs-2">Range to bind</p>
          <p class="col-xs-1">SMS</p>
          <p class="col-xs-1">Mail</p>
          <p class="col-xs-2">Actions</p>
        </div>
        <div v-for="dp in datapointList" :key="dp.id" class="col-xs-12">
          <p class="col-xs-2">{{ dp.name }}</p>
          <p class="col-xs-2">{{ dp.xid }}</p>
          <select v-model="dp.user" class="col-xs-2">
            <option v-for="u in userList" :key="u.id" :value="u.id">
              {{ u.name }}
            </option>
          </select>
          <select v-model="dp.range" class="col-xs-2">
            <option v-for="r in rangeList" :key="r.id" :value="r.id">
              {{ r.description }}
            </option>
          </select>

          <input
            type="checkbox"
            :id="dp.id + 'perSms'"
            v-model="dp.sms"
            class="col-xs-1"
          />
          <input
            type="checkbox"
            :id="dp.id + 'perMail'"
            v-model="dp.mail"
            class="col-xs-1"
          />
          <button @click="savePlcNotification(dp)" class="col-xs-2">
            Save
          </button>
        </div>
      </tab>
      <tab title="Show User Schedulers">
        <select v-model="selectedUser" @change="getSchedulersByUser()">
          <option v-for="u in userList" :key="u.id" :value="u.id">
            {{ u.name }}
          </option>
        </select>
        <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
          <p class="col-xs-1">{{ s.id }}</p>
          <p class="col-xs-1">{{ s.perMail }}</p>
          <p class="col-xs-1">{{ s.perSms }}</p>
          <p class="col-xs-1">{{ s.hourStart }}</p>
          <p class="col-xs-1">{{ s.hourStop }}</p>
          <p class="col-xs-2">{{ s.description }}</p>
          <p class="col-xs-1">{{ s.mtime }}</p>
          <p class="col-xs-1">{{ s.dataPointId }}</p>
          <p class="col-xs-2">{{ s.userEmail }}</p>
          <p class="col-xs-1">{{ s.userPhone }}</p>
        </div>
      </tab>
      <tab title="Show DataPoint Schedulers">
        <select
          v-model="selectedDatapoint"
          @change="getSchedulersByDataPoint()"
        >
          <option v-for="dp in datapointList" :key="dp.id" :value="dp.id">
            {{ dp.name }} - {{ dp.xid }}
          </option>
        </select>
        <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
          <p class="col-xs-1">{{ s.id }}</p>
          <p class="col-xs-1">{{ s.perMail }}</p>
          <p class="col-xs-1">{{ s.perSms }}</p>
          <p class="col-xs-1">{{ s.hourStart }}</p>
          <p class="col-xs-1">{{ s.hourStop }}</p>
          <p class="col-xs-2">{{ s.description }}</p>
          <p class="col-xs-1">{{ s.mtime }}</p>
          <p class="col-xs-1">{{ s.dataPointId }}</p>
          <p class="col-xs-2">{{ s.userEmail }}</p>
          <p class="col-xs-1">{{ s.userPhone }}</p>
        </div>
      </tab>
      <tab title="Ranges">
        <div class="col-xs-12">
          <p class="col-xs-1">ID</p>
          <p class="col-xs-3">Start Hour</p>
          <p class="col-xs-3">End Hour</p>
          <p class="col-xs-3">Description</p>
          <p class="col-xs-2">Options</p>
        </div>
        <div v-for="r in rangeList" :key="r.id" class="col-xs-12">
          <p class="col-xs-1">{{ r.id }}</p>
          <input class="col-xs-3" type="number" v-model="r.hourStart" />
          <input class="col-xs-3" type="number" v-model="r.hourStop" />
          <input class="col-xs-3" type="text" v-model="r.description" />
          <p class="col-xs-1"><button @click="updateRange(r)">Upd</button></p>
          <p class="col-xs-1"><button @click="deleteRange(r)">Del</button></p>
        </div>
        <div class="col-xs-12">
          <p class="col-xs-1">#</p>
          <input class="col-xs-3" type="number" v-model="range.hourStart" />
          <input class="col-xs-3" type="number" v-model="range.hourStop" />
          <input class="col-xs-3" type="text" v-model="range.description" />
          <p class="col-xs-2"><button @click="postRange()">Add</button></p>
        </div>
      </tab>
      <tab title="Notifications">
        <div class="col-xs-12">
            <p class="col-xs-1">Id</p>
            <p class="col-xs-1">Mail</p>
            <p class="col-xs-1">SMS</p>
            <p class="col-xs-7">Modification time</p>
            <p class="col-xs-2">Actions</p>
        </div>
        <div class="col-xs-12" v-for="n in notificationList" :key="n.id">
            <p class="col-xs-1">{{n.id}}</p>
            <input class="col-xs-1" type="checkbox" v-model="n.perEmail"/>
            <input class="col-xs-1" type="checkbox" v-model="n.perSms"/>
            <p class="col-xs-7">{{n.mtime}}</p>
            <p class="col-xs-2"><button @click="updateNotif(n)">Upd</button></p>            
        </div>
      </tab>
    </tabs>
    <modal v-model="schedulerModalVisible" title="Scheduler">
        <div v-if="schedulerModalVisible">
            <p>{{selectedScheduler.id}}</p>
            <p>{{selectedScheduler.ranges_id}}</p>
            <p>{{selectedScheduler.notifications_id}}</p>
        </div>
    </modal>
  </div>
</template>
<script>
import store from "../store";
import i18n from "../i18n";

export default {
  el: "#plcnotifications",
  name: "#plcnotifications",
  data() {
    return {
      datapointList: [],
      userList: [],
      rangeList: [],
      range: { id: null, hourStart: 0, hourStop: 0, description: null },
      schedulerList: [],
      schedulerModalVisible: false,
      selectedScheduler: {id: null, ranges_id: null, notifications_id: null},
      notificationList: [],
      selectedDatapoint: 1,
      selectedUser: 1,
    };
  },
  computed: {},
  mounted() {
    store.dispatch("getDataPointList").then((r) => {
      this.datapointList = r;
    });
    store.dispatch("getUserList").then((r) => {
      this.userList = r;
    });
    store.dispatch("getRangeList").then((r) => {
      this.rangeList = r;
    });
  },
  methods: {
    savePlcNotification(dp) {
      console.log(dp);
      this.$store.dispatch("postSchedulerWithNotification", dp).then((r) => {
        console.log(r);
      });
    },
    onChange(index) {
      if (index === 5) {
        this.getNotificationList();
      } else if (index === 3) {
        this.getSchedulersByDataPoint();
      } else if (index === 2) {
        this.getSchedulersByUser();
      } else if (index === 0) {
        this.getSchedulerList();
      }
    },
    toggleSchedulerModal(id) {
        this.schedulerModalVisible = !this.schedulerModalVisible;
        if(this.schedulerModalVisible && id != null && id != undefined) {
            this.getSchedulerRaw(id)
        }
    },
    getSchedulerRaw(schedulerId) {
      this.$store.dispatch("getSchedulerRaw", schedulerId).then((r) => {
        this.selectedScheduler = r;
      });  
    },
    getSchedulerList() {
      this.$store.dispatch("getSchedulersList").then((r) => {
        this.schedulerList = r;
      });
    },
    getSchedulersByDataPoint() {
      this.$store
        .dispatch("getSchedulersByDataPoint", this.selectedDatapoint)
        .then((r) => {
          this.schedulerList = r;
        });
    },
    getSchedulersByUser() {
      this.$store
        .dispatch("getSchedulersByUser", this.selectedUser)
        .then((r) => {
          this.schedulerList = r;
        });
    },
    postRange() {
      this.$store.dispatch("postRange", this.range).then((r) => {
        this.rangeList.push(r);
        this.range = { id: null, hourStart: 0, hourStop: 0, description: null };
      });
    },
    updateRange(range) {
      this.$store.dispatch("updateRange", range).then(() => {});
    },
    deleteRange(range) {
      this.$store.dispatch("deleteRange", range).then((r) => {
        this.rangeList = this.rangeList.filter((r) => r.id !== range.id);
      });
    },
    getNotificationList() {
      this.$store.dispatch("getNotificationList").then((r) => {
        this.notificationList = r;
      });
    },
    updateNotif(notification) {
        this.$store.dispatch("updateNotification", notification).then(() => {});
    }, 
  },
};
</script>
<style scoped>
</style>