<template>
    <div>     
      <alert v-if="bdanger" id="p_is_alive" type="danger"><b>Error</b> - time of the latest server response: {{timeFromServer | moment}} </alert>
      <alert v-if="bwarning" id="p_is_alive" type="warning"><b>Warning</b> - time of the latest server response: {{timeFromServer | moment}}</alert>
      <alert v-if="bsuccess" id="p_is_alive" type="success">{{label}}</alert>
      <popover title="Is Alive" target="#p_is_alive" trigger="hover">
        <template slot="popover">
            <p class="p_is_now">Time from web browser: <b>{{timeInWeb | moment}}</b></p>
            <p class="p_is_last_time_modyfication">Time of the latest server response: <b>{{timeFromServer | moment}}</b></p>
            <p>Refresh time: {{timeRefresh}} [ms]</p>
            <p>Time before warning: {{timeWarning}} [ms]</p>
            <p>Time before error: {{timeError}} [ms]</p>
        </template>
      </popover>
    </div>
</template>

<script>
import moment from "moment";

/**
 * @author grzegorz.bylica@gmail.com 
 */
export default {
  props: ['plabel', 'ptimeWarning', 'ptimeError', 'ptimeRefresh'],
  data() {
    return {
      label: this.plabel,               //    "Is alive"
      timeWarning: this.ptimeWarning,   //     5000 [ms]
      timeError: this.ptimeError,       //    10000 [ms]
      timeRefresh: this.ptimeRefresh,   //     1000 [ms]
      timeInWeb: 0,
      timeFromServer: 0,
      bdanger: false,
      bwarning: false,
      bsuccess: true
    };
  },
  methods: {
    getTime() {
      this.timeInWeb = new Date();
      return this.timeInWeb;
    },
    getTimeFromServer() {
      this.timeFromServer = lasTimeUpdate;
      return this.timeFromServer;
    },
    checkTimeWarning() {
      let isDanger =
        this.getTime() - this.getTimeFromServer() > this.timeError;
      if (isDanger) {
        this.bwarning = false;
        this.bsuccess = false;
        this.bdanger = true;
      } else {
        let isWarning =
          this.getTime() - this.getTimeFromServer() > this.timeWarning;
        if (isWarning) {
          this.bdanger = false;
          this.bsuccess = false;
          this.bwarning = true;
        } else {
          this.bdanger = false;
          this.bwarning = false;
          this.bsuccess = true;
        }
      }
    }
  },
  created() {
    if (this.timeRefresh) {
      setInterval(
        function() {
          this.checkTimeWarning();
        }.bind(this),
        this.timeRefresh
      );
    }
  },
  filters: {
    moment: function(date) {
      return moment(date).format(" hh:mm:ss");
    }
  }
};
</script>

<style>
</style>
