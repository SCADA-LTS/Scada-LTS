<template>
  <div>
    <h3 class="col-xs-12">{{ $t("plcnotifications.tab.scheduler.list") }}</h3>
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
        <input class="col-xs-1" type="checkbox" v-model="s.perMail" disabled />
        <input class="col-xs-1" type="checkbox" v-model="s.perSms" disabled />
        <p class="col-xs-2">{{ s.mtime }}</p>
        <button class="col-xs-1" @click="toggleModal(s.id)">
          {{ $t("plcnotifications.buttons.more") }}
        </button>
      </div>
    </div>
    <modal v-model="isModalVisible" title="Scheduler" size="lg">
      <span slot="title" v-if="scheduler"
        >Scheduler #{{ scheduler.info.id
        }}<span class="dialog-span-right">
          <btn type="danger" @click="deleteScheduler()">
            <i class="glyphicon glyphicon-trash"></i
          ></btn>
        </span>
      </span>
      <div v-if="isModalVisible && scheduler">
        <div class="col-xs-12">
          <h3 class="col-xs-12">Scheduler configuration:</h3>
          <div class="col-xs-3">
            <select class="form-control" v-model="scheduler.info.ranges_id">
              <option v-for="r in rangeList" :key="r.id" :value="r.id">
                {{ r.description }}
              </option>
            </select>
          </div>
          <div class="col-xs-7">
            <select
              class="form-control"
              v-model="scheduler.info.notifications_id"
            >
              <option v-for="n in notificationList" :key="n.id" :value="n.id">
                ID:{{ n.id }} Modification Time:{{ n.mtime }}
              </option>
            </select>
          </div>
          <div class="col-xs-2">
            <btn block @click="updateScheduler()">Update</btn>
          </div>
        </div>
        <div class="col-xs-12">
          <h3 class="col-xs-12">Schdeuler DataPoints</h3>
          <div class="col-xs-12 table-header">
            <p class="col-xs-4">ID</p>
            <p class="col-xs-4">ExportID</p>
            <p class="col-xs-4">Name</p>
          </div>
          <div class="col-xs-12" v-for="dp in scheduler.dpids" :key="dp.id">
            <p class="col-xs-4">{{ dp.id }}</p>
            <p class="col-xs-4">{{ dp.xid }}</p>
            <p class="col-xs-4">{{ dp.name }}</p>
          </div>
          <div class="col-xs-8">
            <select class="form-control" v-model="scheduler.dpid">
              <option v-for="dp in datapointList" :key="dp.id" :value="dp">
                {{ dp.name }} {{ dp.xid }}
              </option>
            </select>
          </div>
          <div class="col-xs-4">
            <btn block @click="bindWithDataPoint()"> Bind with DP </btn>
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
          <div class="col-xs-12" v-for="u in scheduler.usrids" :key="u.id">
            <p class="col-xs-4">{{ u.name }}</p>
            <p class="col-xs-4">{{ u.phone }}</p>
            <p class="col-xs-4">{{ u.email }}</p>
          </div>
          <div class="col-xs-8">
            <select class="form-control" v-model="scheduler.usrid">
              <option v-for="u in userList" :key="u.id" :value="u">
                {{ u.name }}
              </option>
            </select>
          </div>
          <div class="col-xs-4">
            <btn block @click="bindWithUser()"> Bind with User </btn>
          </div>
        </div>
      </div>
      <div><p>.</p></div>
    </modal>
  </div>
</template>
<script>
export default {
  name: "SchedulerListComponent",

  data() {
    return {
      isModalVisible: false,
      scheduler: undefined,
    };
  },

  computed: {
    schedulerList() {
      return this.$store.state.plcNotifications.schedulerList;
    },
    datapointList() {
      return this.$store.state.plcNotifications.datapointList;
    },
    userList() {
      return this.$store.state.plcNotifications.userList;
    },
    rangeList() {
      return this.$store.state.plcNotifications.rangeList;
    },
    notificationList() {
      return this.$store.state.plcNotifications.notificationList;
    },
  },

  mounted() {

  },

  methods: {
    toggleModal(schedulerId) {
      this.isModalVisible = !this.isModalVisible;
      if (this.isModalVisible && !!schedulerId) {
        this.fetchSchedulerDetails(schedulerId);
      }
    },

    async fetchSchedulerDetails(id) {
      this.scheduler = await this.$store.dispatch("getSchedulerDetails", id);
    },

    updateScheduler() {
      this.$store.dispatch("updateScheduler", this.scheduler.info);
    },

    deleteScheduler() {
      this.$confirm({
        title: "Delete Scheduler",
        content: "Do you realy want to delete this Scheduler?",
      }).then(() => {
        this.$store
          .dispatch("deleteScheduler", this.scheduler.info)
          .then(() => {
            this.isModalVisible = false;
          });
      });
    },

    bindWithUser() {
      this.$store
        .dispatch("bindSchedulerWithUser", {
          sid: this.scheduler.info.id,
          uid: this.scheduler.usrid.id,
        })
        .then(() => {
          this.scheduler.usrids.push(this.scheduler.usrid);
          delete this.scheduler.usrid;
        });
    },

    bindWithDataPoint() {
      this.$store
        .dispatch("bindSchedulerWithDataPoint", {
          sid: this.scheduler.info.id,
          dpid: this.scheduler.dpid.id,
        })
        .then(() => {
          this.scheduler.dpids.push(this.scheduler.dpid);
          delete this.scheduler.dpid;
        });
    },
  },
};
</script>
<style>
</style>