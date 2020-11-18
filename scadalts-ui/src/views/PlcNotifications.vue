<template>
  <div class="container-fluid">
    <h1>{{ $t("plcnotifications.title") }}</h1>

    <tabs @change="onChange" justified>
      <tab :title="$t('plcnotifications.tab.schedulers')">
        <div class="col-xs-12">
          <div class="col-md-4">
            <div class="col-xs-12">
              <h3>{{ $t("plcnotifications.tab.create") }}</h3>
            </div>

            <div class="col-xs-12">
              <div class="col-xs-6">
                <p>Select TimeRange</p>
              </div>
              <div class="col-xs-6">
                <select v-model="newScheduler.range" class="col-xs-12">
                  <option v-for="r in rangeList" :key="r.id" :value="r.id">
                    {{ r.hourStart }}-{{ r.hourStop }} {{ r.description }} | 
                  </option>
                </select>
              </div>
            </div>
            <div class="col-xs-12">
              <div class="col-xs-6">
                <p>Select Notification scheme</p>
              </div>
              <div class="col-xs-6">
                <select
                  class="col-xs-12"
                  v-model="newScheduler.notification"
                  @change="notificationChange"
                >
                  <option v-for="n in notificationList" :key="n.id" :value="n">
                    #{{n.id}}, Modified: {{ n.mtime }}
                  </option>
                  <option :value="newNotification">Create new</option>
                </select>
              </div>
            </div>
            <div class="col-xs-12">
              <div class="col-xs-6">
                <p>Email</p>
              </div>
              <div class="col-xs-6">
                <input
                  type="checkbox"
                  v-model="newScheduler.notification.perEmail"
                  :disabled="notificationDisabled"
                />
              </div>
            </div>
            <div class="col-xs-12">
              <div class="col-xs-6">
                <p>Sms</p>
              </div>
              <div class="col-xs-6">
                <input
                  type="checkbox"
                  v-model="newScheduler.notification.perSms"
                  :disabled="notificationDisabled"
                />
              </div>
            </div>
            <div class="col-xs-12">
              <button @click="saveNewScheduler()" class="col-xs-12">
                Save my data
              </button>
            </div>
          </div>
          <div class="col-md-8">
            <div class="col-xs-12">
              <h3>{{ $t("plcnotifications.tab.scheduler.list") }}</h3>
            </div>
            <div class="col-xs-12 table-header">
              <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
              <p class="col-xs-2">
                {{ $t("plcnotifications.table.hour.start") }}
              </p>
              <p class="col-xs-2">
                {{ $t("plcnotifications.table.hour.stop") }}
              </p>
              <p class="col-xs-2">
                {{ $t("plcnotifications.table.description") }}
              </p>
              <p class="col-xs-1">{{ $t("plcnotifications.table.mail") }}</p>
              <p class="col-xs-1">{{ $t("plcnotifications.table.sms") }}</p>
              <p class="col-xs-2">{{ $t("plcnotifications.table.mtime") }}</p>
              <p class="col-xs-1">
                {{ $t("plcnotifications.table.operations") }}
              </p>
            </div>
            <div class="col-xs-12 constraint-height">
              <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
                <p class="col-xs-1">{{ s.id }}</p>
                <p class="col-xs-2">{{ s.hourStart }}</p>
                <p class="col-xs-2">{{ s.hourStop }}</p>
                <p class="col-xs-2">{{ s.description }}</p>
                <input
                  class="col-xs-1"
                  type="checkbox"
                  v-model="s.perMail"
                  disabled
                />
                <input
                  class="col-xs-1"
                  type="checkbox"
                  v-model="s.perSms"
                  disabled
                />
                <p class="col-xs-2">{{ s.mtime }}</p>
                <button class="col-xs-1" @click="toggleSchedulerModal(s.id)">
                  {{ $t("plcnotifications.buttons.more") }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="col-xs-12" v-if="pointHierarchy">
          <div class="col-xs-12">
            <h3>PointHierarchy notification configuration</h3>
          </div>
          <div class="col-xs-12">
            <div class="inline right">
              <div class="col-xs-12 table-header">
                <p class="col-xs-3">Active Scheduler</p>
                <p class="col-xs-2">Mail</p>
                <p class="col-xs-2">SMS</p>
                <p class="col-xs-4">TimeRange</p>
              </div>
            </div>

          </div>
          <ph-item
            v-for="node in pointHierarchy"
            :item="node"
            :schedulers="schedulerList"
            :key="node"
          >
          </ph-item>
        </div>
      </tab>
      <!-- <tab :title="$t('plcnotifications.tab.user')">
        <div class="col-xs-12">
          <p class="col-xs-9 text-right">
            {{ $t("plcnotifications.tab.user.description") }}
          </p>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="selectedUser"
              @change="getSchedulersByUser()"
            >
              <option v-for="u in userList" :key="u.id" :value="u.id">
                {{ u.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="col-xs-12 table-header">
          <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.mail") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.sms") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.hour.start") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.hour.stop") }}</p>
          <p class="col-xs-2">{{ $t("plcnotifications.table.description") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.mtime") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.datapoint") }}</p>
          <p class="col-xs-2">{{ $t("plcnotifications.table.mailaddress") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.phone") }}</p>
        </div>
        <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
          <p class="col-xs-1">{{ s.id }}</p>
          <input
            class="col-xs-1"
            type="checkbox"
            v-model="s.perMail"
            disabled
          />
          <input class="col-xs-1" type="checkbox" v-model="s.perSms" disabled />
          <p class="col-xs-1">{{ s.hourStart }}</p>
          <p class="col-xs-1">{{ s.hourStop }}</p>
          <p class="col-xs-2">{{ s.description }}</p>
          <p class="col-xs-1">{{ s.mtime }}</p>
          <p class="col-xs-1">{{ s.dataPointId }}</p>
          <p class="col-xs-2">{{ s.userEmail }}</p>
          <p class="col-xs-1">{{ s.userPhone }}</p>
        </div>
      </tab> -->
      <!-- <tab :title="$t('plcnotifications.tab.datapoint')">
        <div class="col-xs-12">
          <p class="col-xs-9 text-right">
            {{ $t("plcnotifications.tab.datapoint.description") }}
          </p>
          <div class="col-xs-3">
            <select
              class="form-control"
              v-model="selectedDatapoint"
              @change="getSchedulersByDataPoint()"
            >
              <option v-for="dp in datapointList" :key="dp.id" :value="dp.id">
                {{ dp.name }} - {{ dp.xid }}
              </option>
            </select>
          </div>
        </div>

        <div class="col-xs-12 table-header">
          <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.mail") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.sms") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.hour.start") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.hour.stop") }}</p>
          <p class="col-xs-3">{{ $t("plcnotifications.table.description") }}</p>
          <p class="col-xs-4">{{ $t("plcnotifications.table.mtime") }}</p>
        </div>
        <div v-for="s in schedulerList" :key="s.id" class="col-xs-12">
          <p class="col-xs-1">{{ s.id }}</p>
          <input
            class="col-xs-1"
            type="checkbox"
            v-model="s.perMail"
            disabled
          />
          <input class="col-xs-1" type="checkbox" v-model="s.perSms" disabled />
          <p class="col-xs-1">{{ s.hourStart }}</p>
          <p class="col-xs-1">{{ s.hourStop }}</p>
          <p class="col-xs-3">{{ s.description }}</p>
          <p class="col-xs-4">{{ s.mtime }}</p>
        </div>
      </tab> -->
      <tab :title="$t('plcnotifications.tab.ranges')">
        <div class="col-xs-12 table-header">
          <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
          <p class="col-xs-3">{{ $t("plcnotifications.table.hour.start") }}</p>
          <p class="col-xs-3">{{ $t("plcnotifications.table.hour.stop") }}</p>
          <p class="col-xs-3">{{ $t("plcnotifications.table.description") }}</p>
          <p class="col-xs-2">{{ $t("plcnotifications.table.operations") }}</p>
        </div>
        <div v-for="r in rangeList" :key="r.id" class="col-xs-12">
          <p class="col-xs-1">{{ r.id }}</p>
          <input class="col-xs-3" type="number" v-model="r.hourStart" />
          <input class="col-xs-3" type="number" v-model="r.hourStop" />
          <input class="col-xs-3" type="text" v-model="r.description" />
          <button class="col-xs-1" @click="updateRange(r)">
            {{ $t("plcnotifications.buttons.update") }}
          </button>
          <button class="col-xs-1" @click="deleteRange(r)">
            {{ $t("plcnotifications.buttons.delete") }}
          </button>
        </div>
        <div class="col-xs-12">
          <p class="col-xs-1">#</p>
          <input class="col-xs-3" type="number" v-model="range.hourStart" />
          <input class="col-xs-3" type="number" v-model="range.hourStop" />
          <input class="col-xs-3" type="text" v-model="range.description" />
          <button class="col-xs-2" @click="postRange()">
            {{ $t("plcnotifications.buttons.add") }}
          </button>
        </div>
      </tab>
      <tab :title="$t('plcnotifications.tab.notifications')">
        <div class="col-xs-12 table-header">
          <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.mail") }}</p>
          <p class="col-xs-1">{{ $t("plcnotifications.table.sms") }}</p>
          <p class="col-xs-7">{{ $t("plcnotifications.table.mtime") }}</p>
          <p class="col-xs-2">{{ $t("plcnotifications.table.operations") }}</p>
        </div>
        <div class="col-xs-12" v-for="n in notificationList" :key="n.id">
          <p class="col-xs-1">{{ n.id }}</p>
          <input class="col-xs-1" type="checkbox" v-model="n.perEmail" />
          <input class="col-xs-1" type="checkbox" v-model="n.perSms" />
          <p class="col-xs-7">{{ n.mtime }}</p>
          <button class="col-xs-1" @click="updateNotif(n)">
            {{ $t("plcnotifications.buttons.update") }}
          </button>
          <button class="col-xs-1" @click="deleteNotif(n)">
            {{ $t("plcnotifications.buttons.delete") }}
          </button>
        </div>
      </tab>
    </tabs>
    <modal v-model="schedulerModalVisible" title="Scheduler" size="lg">
      <span slot="title"
        >Scheduler #{{ selectedScheduler.id
        }}<span class="dialog-span-right"
          ><btn type="danger" @click="deleteScheduler()">
            <i class="glyphicon glyphicon-trash"></i></btn></span
      ></span>
      <div v-if="schedulerModalVisible">
        <div class="col-xs-12">
          <h3 class="col-xs-12">Scheduler configuration:</h3>
          <div class="col-xs-3">
            <select class="form-control" v-model="selectedScheduler.ranges_id">
              <option v-for="r in rangeList" :key="r.id" :value="r.id">
                {{ r.description }}
              </option>
            </select>
          </div>
          <div class="col-xs-7">
            <select
              class="form-control"
              v-model="selectedScheduler.notifications_id"
            >
              <option v-for="n in notificationList" :key="n.id" :value="n.id">
                ID:{{ n.id }} Modification Time:{{ n.mtime }}
              </option>
            </select>
          </div>
          <div class="col-xs-2">
            <btn block @click="updateScheduler(selectedScheduler)">Update</btn>
          </div>
        </div>
        <div class="col-xs-12">
          <h3 class="col-xs-12">Schdeuler DataPoints</h3>
          <div class="col-xs-12 table-header">
            <p class="col-xs-4">ID</p>
            <p class="col-xs-4">ExportID</p>
            <p class="col-xs-4">Name</p>
          </div>
          <div
            class="col-xs-12"
            v-for="dp in selectedSchedulerDpIds"
            :key="dp.id"
          >
            <p class="col-xs-4">{{ dp.id }}</p>
            <p class="col-xs-4">{{ dp.xid }}</p>
            <p class="col-xs-4">{{ dp.name }}</p>
          </div>
          <div class="col-xs-8">
            <select class="form-control" v-model="selectedSchedulerNewDP">
              <option v-for="dp in datapointList" :key="dp.id" :value="dp">
                {{ dp.name }} {{ dp.xid }}
              </option>
            </select>
          </div>
          <div class="col-xs-4">
            <btn block @click="bindSchedulerWithDP()"> Bind with DP </btn>
          </div>
        </div>
        <div class="col-xs-12">
          <h3 class="col-xs-12">Scheduler Users</h3>
          <div class="col-xs-12 table-header">
            <p class="col-xs-4">{{ $t("plcnotifications.table.username") }}</p>
            <p class="col-xs-4">{{ $t("plcnotifications.table.phone") }}</p>
            <p class="col-xs-4">
              {{ $t("plcnotifications.table.mailaddress") }}
            </p>
          </div>
          <div
            class="col-xs-12"
            v-for="u in selectedSchedulerUserIds"
            :key="u.id"
          >
            <p class="col-xs-4">{{ u.name }}</p>
            <p class="col-xs-4">{{ u.phone }}</p>
            <p class="col-xs-4">{{ u.email }}</p>
          </div>
          <div class="col-xs-8">
            <select class="form-control" v-model="selectedSchedulerNewUser">
              <option v-for="u in userList" :key="u.id" :value="u">
                {{ u.name }}
              </option>
            </select>
          </div>
          <div class="col-xs-4">
            <btn block @click="bindSchedulerWithUser()"> Bind with User </btn>
          </div>
        </div>
      </div>
      <div><p>.</p></div>
    </modal>
  </div>
</template>
<script>
import store from "../store";
import i18n from "../i18n";
import { keys } from "@amcharts/amcharts4/.internal/core/utils/Object";
import PointHierarchyItem from "../components/plcnotifications/PointHierarchyItem";

export default {
  el: "#plcnotifications",
  name: "#plcnotifications",
  components: {
    "ph-item": PointHierarchyItem,
  },
  data() {
    return {
      datapointList: [],
      userList: [],
      rangeList: [],
      range: { id: null, hourStart: 0, hourStop: 0, description: null },
      schedulerList: [],
      schedulerModalVisible: false,
      selectedScheduler: { id: null, ranges_id: null, notifications_id: null },
      selectedSchedulerDpIds: [],
      selectedSchedulerUserIds: [],
      selectedSchedulerNewDP: undefined,
      selectedSchedulerNewUser: undefined,
      selectedNewSchedulerDpId: {
        user: undefined,
        range: undefined,
        sms: undefined,
        mail: undefined,
      },
      notificationList: [],
      selectedDatapoint: 1,
      selectedUser: 1,
      pointHierarchy: null,
      newScheduler: {
        range: undefined,
        notification: {
          id: -1,
          perEmail: false,
          perSms: false,
          mtime: "",
        },
      },
      newNotification: {
        id: -1,
        perEmail: false,
        perSms: false,
        mtime: "",
      },
      notificationDisabled: false,
    };
  },
  computed: {},
  mounted() {
    this.initData();
  },
  methods: {
    async initData() {
      store.dispatch("getDataPointList").then((r) => {
        this.datapointList = r;
      });
      store.dispatch("getUserList").then((r) => {
        this.userList = r;
      });
      store.dispatch("getRangeList").then((r) => {
        this.rangeList = r;
      });
      this.getNotificationList();
      this.getSchedulerList();
      this.pointHierarchy = await this.fetchPointHierarchyData(0);
    },
    async fetchPointHierarchyData(key) {
      let x = await store.dispatch("getPointHierarchy", key);
      console.debug(x);
      return x;
    },
    savePlcNotification(dp) {
      this.$store.dispatch("postSchedulerWithNotification", dp).then((r) => {
        this.getSchedulerList();
      });
    },
    onChange(index) {
      if (index === 2) {
        this.getNotificationList();
      // } else if (index === 1) {
      //   this.getSchedulersByDataPoint();
      // } else if (index === 1) {
      //   this.getSchedulersByUser();
      } else if (index === 0) {
        this.getSchedulerList();
      }
    },
    notificationChange() {
      this.notificationDisabled = this.newScheduler.notification.id >= 0;
    },
    async saveNewScheduler() {
      if (this.newScheduler.notification.id < 0) {
        await this.createNotification();
      }

      this.$store
        .dispatch("createScheduler", {
          rangeId: this.newScheduler.range,
          notificationId: this.newScheduler.notification.id,
        })
        .then((result) => {
          this.schedulerList.push(result);
        });
    },

    async createNotification() {
      let now = new Date();
      let date = `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`;
      let time = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
      this.newScheduler.notification.mtime = `${date} ${time}`;
      this.newScheduler.notification = await this.$store.dispatch(
        "createNotification",
        this.newScheduler.notification
      );
    },

    toggleSchedulerModal(id) {
      this.schedulerModalVisible = !this.schedulerModalVisible;
      if (this.schedulerModalVisible && id != null && id != undefined) {
        this.getSchedulerRaw(id);
        this.getSchedulerUserIds(id);
        this.getSchedulerDpIds(id);
      }
    },
    getSchedulerRaw(schedulerId) {
      this.$store.dispatch("getSchedulerRaw", schedulerId).then((r) => {
        this.selectedScheduler = r;
      });
    },
    getSchedulerDpIds(schedulerId) {
      this.$store.dispatch("getSchedulerDpIds", schedulerId).then((r) => {
        this.selectedSchedulerDpIds = this.datapointList.filter((e) => {
          return r.indexOf(e.id) > -1;
        });
      });
    },
    getSchedulerUserIds(schedulerId) {
      this.$store.dispatch("getSchedulerUserIds", schedulerId).then((r) => {
        this.selectedSchedulerUserIds = this.userList.filter((e) => {
          return r.indexOf(e.id) > -1;
        });
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
    updateScheduler(scheduler) {
      this.$store.dispatch("updateScheduler", scheduler).then((r) => {
        this.schedulerList[
          this.schedulerList.findIndex((s) => s.id == r.id)
        ] = r;
        // this.schedulerList = r;
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
      this.$confirm({
        title: "Delete Range",
        content: `Do you want to delete range #${range.id}? This operation will not be reversable.`,
      }).then(() => {
        this.$store.dispatch("deleteRange", range).then((r) => {
          this.rangeList = this.rangeList.filter((r) => r.id !== range.id);
        });
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
    deleteNotif(notification) {
      this.$confirm({
        title: "Delete Notification",
        content: `Do you realy want to delete notification #${notification.id}? This operation will not be reversable.`,
      }).then(() => {
        this.$store.dispatch("deleteNotification", notification).then(() => {
          this.notificationList = this.notificationList.filter(
            (n) => n.id !== notification.id
          );
        });
      });
    },
    bindSchedulerWithDP() {
      this.$store
        .dispatch("bindSchedulerWithDataPoint", {
          sid: this.selectedScheduler.id,
          dpid: this.selectedSchedulerNewDP.id,
        })
        .then(() => {
          this.selectedSchedulerDpIds.push(this.selectedSchedulerNewDP);
        });
    },
    bindSchedulerWithUser() {
      this.$store
        .dispatch("bindSchedulerWithUser", {
          sid: this.selectedScheduler.id,
          uid: this.selectedSchedulerNewUser.id,
        })
        .then(() => {
          this.selectedSchedulerUserIds.push(this.selectedSchedulerNewUser);
        });
    },
    deleteScheduler() {
      this.$confirm({
        title: "Delete Scheduler",
        content: `Do you realy want to delete this Scheduler?`,
      }).then(() => {
        this.$store
          .dispatch("deleteScheduler", this.selectedScheduler)
          .then(() => {
            this.schedulerList = this.schedulerList.filter(
              (s) => s.id !== this.selectedScheduler.id
            );
            this.schedulerModalVisible = false;
          });
      });
    },
  },
};
</script>
<style scoped>
.dialog-span-right {
  float: right;
}
.table-header {
  border-bottom: solid 1px #ddd;
  margin-bottom: 5px;
  margin-top: 15px;
  font-weight: bold;
  line-height: 15px;
}
.text-right {
  text-align: right;
  margin: 7px 0;
}
.constraint-height {
  max-height: 100px;
  overflow-y: auto;
}
</style>