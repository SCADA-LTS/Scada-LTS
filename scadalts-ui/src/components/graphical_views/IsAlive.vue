<template>
    <div>
      <alert v-if="bdanger" id="p_is_alive_bdanger" type="danger"><b>Error</b> - time of the latest server response:
        {{timeFromServerEpoch | moment}}
      </alert>
      <alert v-if="bwarning" id="p_is_alive_bwarning" type="warning"><b>Warning</b> - time of the latest server response:
        {{timeFromServerEpoch | moment}}</alert>
      <alert v-if="bsuccess" id="p_is_alive" type="success">{{label}}</alert>
      <popover title="Is Alive" target="#p_is_alive" trigger="hover">
        <template slot="popover">
            <p class="p_is_now">Time from web browser: <b>{{timeInWebEpoch | moment}}</b></p>
            <p class="p_is_last_time_modyfication">Time of the latest server response: <b>{{timeFromServerEpoch |
              moment}}</b></p>
            <p>Refresh time: {{timeRefresh}} [ms]</p>
            <p>Time before warning: {{timeWarningEpoch}} [ms]</p>
            <p>Time before error: {{timeErrorEpoch}} [ms]</p>
        </template>
      </popover>
    </div>
</template>

<script>
import moment from 'moment'
import httpClient from 'axios'

/**
 * @author grzegorz.bylica@gmail.com
 */
class ApiIsAlive {
  time () {
    return new Promise((resolve, reject) => {
      try {
        const apiIsAliveTime = `./api/is_alive/time`
        httpClient.get(apiIsAliveTime, {timeout: 5000}).then(response => {
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      } catch (e) {
        reject(e)
      }
    })
  }
}

/**
 * @author grzegorz.bylica@gmail.com
 */
export default {
    name: 'is-alive',
  props: ['plabel', 'ptimeWarning', 'ptimeError', 'ptimeRefresh'],
  data () {
    return {
      label: this.plabel, //                             Is alive
      timeWarningEpoch: this.ptimeWarning / 1000, //     5000 [ms]
      timeErrorEpoch: this.ptimeError / 1000, //         10000 [ms]
      timeRefresh: this.ptimeRefresh, //                 1000 [ms]
      timeInWebEpoch: 0,
      timeFromServerEpoch: 0,
      bdanger: false,
      bwarning: false,
      bsuccess: true
    }
  },
  methods: {
    getTime () {
      this.timeInWebEpoch = Date.now() / 1000
      return this.timeInWebEpoch
    },
    interpretationOfTheState () {
      let isDanger =
        this.getTime() - this.timeFromServerEpoch > this.timeErrorEpoch
      if (isDanger) {
        this.bwarning = false
        this.bsuccess = false
        this.bdanger = true
      } else {
        let isWarning =
          this.getTime() - this.timeFromServerEpoch > this.timeWarningEpoch
        if (isWarning) {
          this.bdanger = false
          this.bsuccess = false
          this.bwarning = true
        } else {
          this.bdanger = false
          this.bwarning = false
          this.bsuccess = true
        }
      }
    },
    checkTimeWarning () {
      new ApiIsAlive().time().then(response => {
        this.timeFromServerEpoch = response.data
        this.interpretationOfTheState()
      }).catch(() => {
        this.interpretationOfTheState()
      });
    }
  },
  created () {
    if (this.timeRefresh) {
      setInterval(
        function () {
          this.checkTimeWarning()
        }.bind(this),
        this.timeRefresh
      )
    }
  },
  filters: {
    moment: function (epoch) {
      let date = new Date(epoch * 1000)
      return moment(date).format(' hh:mm:ss')
    }
  }
}
</script>

<style>
</style>
