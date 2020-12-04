<template>
  <div>
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
      <button class="col-xs-1" @click="updateNotification(n)">
        {{ $t("plcnotifications.buttons.update") }}
      </button>
      <button class="col-xs-1" @click="deleteNotificaion(n)">
        {{ $t("plcnotifications.buttons.delete") }}
      </button>
    </div>
  </div>
</template>
<script>
export default {
  name: "SchedulerNotificationComponent",

  data() {
    return {};
  },

  computed: {
    notificationList() {
      return this.$store.state.plcNotifications.notificationList;
    },
  },

  mounted() {},

  methods: {
    updateNotification(notification) {
      this.$store.dispatch("updateNotification", notification);
    },

    deleteNotificaion(notification) {
      this.$confirm({
          title: "Delete Notification",
          content: "Do you realy want to delete this Notification Scheme?"
      }).then(() => {
          this.$store.dispatch("deleteNotification", notification);
      })
    },
  },
};
</script>
<style>
</style>