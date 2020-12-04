<template>
  <div class="container-fluid">
    <h1>{{ $t("plcnotifications.title") }}</h1>

    <tabs @change="onChange" justified>
      <tab :title="$t('plcnotifications.tab.schedulers')">
        <div class="col-xs-12">
          <div class="col-md-4">
            <SchedulerCreatorComponent></SchedulerCreatorComponent>
          </div>
          <div class="col-md-8">
            <SchedulerListComponent></SchedulerListComponent>
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
      <tab :title="$t('plcnotifications.tab.ranges')">
          <SchedulerRangesComponent></SchedulerRangesComponent>
      </tab>
      <tab :title="$t('plcnotifications.tab.notifications')">
          <SchedulerNotificationComponent></SchedulerNotificationComponent>
      </tab>
    </tabs>
  </div>
</template>
<script>
import i18n from "../../i18n";
import PointHierarchyItem from "../../components/plcnotifications/PointHierarchyItem";
import SchedulerListComponent from "./SchedulerListComponent";
import SchedulerCreatorComponent from "./SchedulerCreatorComponent";
import SchedulerRangesComponent from "./SchedulerRangesComponent";
import SchedulerNotificationComponent from "./SchedulerNotificationComponent";

export default {
  el: "#plcnotifications",
  name: "#plcnotifications",
  components: {
    "ph-item": PointHierarchyItem,
    SchedulerListComponent,
    SchedulerCreatorComponent,
    SchedulerRangesComponent,
    SchedulerNotificationComponent,
  },
  data() {
    return {
      pointHierarchy: null,
    };
  },
  computed: {
      schedulerList() {
        return this.$store.state.plcNotifications.schedulerList;
      },
  },
  mounted() {
    this.initData();
  },
  methods: {
    async initData() {
        this.$store.dispatch("getSchedulerList");
        this.$store.dispatch("getDataPointListV2");
        this.$store.dispatch("getUserListV2");
        this.$store.dispatch("getRangeListV2");
        this.$store.dispatch("getNotificationListV2");
      this.pointHierarchy = await this.fetchPointHierarchyData(0);
    },
    async fetchPointHierarchyData(key) {
      return await this.$store.dispatch("getPointHierarchy", key);
    },
    
    onChange(index) {
      console.debug(index)
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