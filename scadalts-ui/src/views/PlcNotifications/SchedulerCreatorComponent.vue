<template>
  <div>
    <div class="col-xs-12">
      <h3>{{ $t("plcnotifications.tab.create") }}</h3>
    </div>

    <div class="col-xs-12">
      <div class="col-xs-6">
        <p>Select TimeRange</p>
      </div>
      <div class="col-xs-6">
        <select v-model="scheduler.range" class="col-xs-12">
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
          v-model="scheduler.notification"
          @change="notificationChange"
        >
          <option v-for="n in notificationList" :key="n.id" :value="n">
            #{{ n.id }}, Modified: {{ n.mtime }}
          </option>
          <option :value="notification.template">Create new</option>
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
          v-model="scheduler.notification.perEmail"
          :disabled="notification.disabled"
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
          v-model="scheduler.notification.perSms"
          :disabled="notification.disabled"
        />
      </div>
    </div>
    <div class="col-xs-12">
      <button @click="saveScheduler()" class="col-xs-12">
        Save my data
      </button>
    </div>
  </div>
</template>
<script>
export default {
    name: "SchedulerCreatorComponent",

    data() {
        return {
            scheduler: {
                range: undefined,
                notification: {
                    perEmail: false,
                    perSms: false,
                },
            },
            notification: {
                disabled: false,
                template: {
                    id: -1,
                    perEmail: false,
                    perSms: false,
                }
                
            }
        }
    },

    computed: {
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

        notificationChange() {
            this.notification.disabled = this.scheduler.notification.id > -1
        },

        async saveScheduler() {
            if(this.scheduler.notification.id < 0) {
                await this.createNotification();
            }

            this.createScheduler();
        },

        async createNotification() {
            let now = new Date();
            let date = `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`;
            let time = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
            this.scheduler.notification.mtime = `${date} ${time}`;
            this.scheduler.notification = await this.$store.dispatch(
                "createNotification", this.scheduler.notification);
        },

        createScheduler() {
            this.$store.dispatch("createScheduler", this.scheduler);
        }
 
    }
};
</script>
<style>
</style>