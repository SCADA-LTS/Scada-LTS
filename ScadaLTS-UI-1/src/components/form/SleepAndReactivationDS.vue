<template>
  <div class="container move-top format_font">
    <div class="panel panel-default col-ld-4, col-md-4, col-sm-6">
      <div class="panel-body">
        <div>
          <btn-group aria-describedby="sleepHelp" style="display: inline">
            <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_STOP"  v-model="reactivation">Stop</btn>
            <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_SLEEP" v-model="reactivation">Sleep</btn>
            <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_NONE"  v-model="reactivation">None</btn>
          </btn-group>  
          <small id="sleepHelp" class="form-text text-muted" style="margin:10px">It will disable the datasource 
    when the attempted connections fail and try reconnect according to the reactivity configuration</small>
        </div>
        <div v-if="reactivation=='sleep'" style="padding:0px; margin:30px" >
          <table>
            <tr>
              <td>
                <input v-model="reactivationValue" type="number" min="1" max="31" class="form-control number-width" style="display: inline"/>
              </td>
              <td>
                <btn-group style="display: inline">                                                             
                  <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_TIME_MIN" v-model="reactivationTimeType">Minute</btn>
                  <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_TIME_HOUR" v-model="reactivationTimeType">Hour</btn>
                  <btn input-type="radio" :input-value="STR_REACTIVATION_TYPE_TIME_DAY" v-model="reactivationTimeType">Day</btn>
                </btn-group>
                
              </td>
            </tr>
          </table>
          <div>
            <p>time to next attempt to enable data source: {{timeToNextTryEnableDs | infoTime}}</p>
          </div>  
        </div>
      </div>
    </div>
  </div>      
</template>

<script>
 import axios from 'axios';

/**
 * @author grzegorz.bylica@gmail.com
 *
 * Associated with class in java ReactivationDs.java
 */

export default {
  data() {
    return {
      
      STR_REACTIVATION_TYPE_TIME_MIN: "min",
      STR_REACTIVATION_TYPE_TIME_HOUR: "hour",
      STR_REACTIVATION_TYPE_TIME_DAY: "day",
      INT_REACTIVATION_TYPE_TIME_MIN: 0,
      INT_REACTIVATION_TYPE_TIME_HOUR: 1,
      INT_REACTIVATION_TYPE_TIME_DAY: 2,

      STR_REACTIVATION_TYPE_NONE: "none",
      STR_REACTIVATION_TYPE_SLEEP: "sleep",
      STR_REACTIVATION_TYPE_STOP: "stop",
      TIME_REFRESH: 1000,
      //
      reactivation: this.defaultReactivation(), 
      reactivationTimeType: this.defaultTimeType(), 
      reactivationTimeValue: this.defaultValue(),

      idDs:0,

      timeToNextTryEnableDs: 0
    };
  },
  methods: {
    defaultReactivation() {
      return this.STR_REACTIVATION_TYPE_NONE
    },
    defaultTimeType() {
      return this.STR_REACTIVATION_TYPE_TIME_MIN
    },
    defaultValue() {
      return 1
    },
    checkWhenNextTryEnableDs() {
      const apiCheckReactivation = `./api/check-reactivation/${this.idDs}`;
      axios.get(apiCheckReactivation).then(response => {
          this.timeToNextTryEnableDs = response.data;
        }).catch(error => {
          console.log(error);
        });
    }
  },
  created() {
    // get from variable to communicate between old ui and new
    
    if (editDSNewUI.reactivation.sleep) {
      this.reactivation = this.STR_REACTIVATION_TYPE_SLEEP;  
    } else if (editDSNewUI.stop) {
      this.reactivation = this.STR_REACTIVATION_TYPE_STOP;
    } else {
      this.reactivation = this.STR_REACTIVATION_TYPE_NONE;
    } 
         
    if (editDSNewUI.reactivation.type == this.INT_REACTIVATION_TYPE_TIME_MIN) {
      this.reactivationTimeType = this.STR_REACTIVATION_TYPE_TIME_MIN
    } else if (editDSNewUI.reactivation.type == this.INT_REACTIVATION_TYPE_TIME_HOUR) {
      this.reactivationTimeType = this.STR_REACTIVATION_TYPE_TIME_HOUR
    } else if (editDSNewUI.reactivation.type == this.INT_REACTIVATION_TYPE_TIME_DAY) {
      this.reactivationTimeType = this.STR_REACTIVATION_TYPE_TIME_DAY
    } 

    this.reactivationValue = editDSNewUI.reactivation.value;

    this.idDs = editDSNewUI.id;

    setInterval(
        function() {
          this.checkWhenNextTryEnableDs();
        }.bind(this),
        this.TIME_REFRESH
      );

  },
  watch: {
    reactivationType() {
      if (this.reactivationTimeType == this.STR_REACTIVATION_TYPE_TIME_MIN) {
        editDSNewUI.reactivation.type = this.INT_REACTIVATION_TYPE_TIME_MIN
      } else if (this.reactivationType == this.STR_REACTIVATION_TYPE_TIME_HOUR) {
        editDSNewUI.reactivation.type = this.INT_REACTIVATION_TYPE_TIME_HOUR;
      } else if (this.reactivationType == this.STR_REACTIVATION_TYPE_TIME_DAY) {
        editDSNewUI.reactivation.type = this.INT_REACTIVATION_TYPE_TIME_DAY;
      }
    },
    reactivation() {
      if (this.reactivation == this.STR_REACTIVATION_TYPE_SLEEP) {
        editDSNewUI.stop = false;
        editDSNewUI.reactivation.sleep = true;
      } else if (this.reactivation == this.STR_REACTIVATION_TYPE_STOP) {
        editDSNewUI.stop = true;
        editDSNewUI.reactivation.sleep = false;
      } else {
        editDSNewUI.stop = false;
        editDSNewUI.reactivation.sleep = false;
      }
    },
    reactivationValue() {
      editDSNewUI.reactivation.value = this.reactivationValue;
    }
  },
  filters: {
      infoTime: function (date) {
        var seconds = parseInt(date/1000);

        var days = Math.floor(seconds / (3600*24));
        seconds  -= days*3600*24;
        var hrs   = Math.floor(seconds / 3600);
        seconds  -= hrs*3600;
        var mnts = Math.floor(seconds / 60);
        seconds  -= mnts*60;
        return days+" days, "+hrs+" Hrs, "+mnts+" Minutes, "+seconds+" Seconds";

      }
  },
};
</script>

<style scoped>
.number-width {
  width:70px;
}
.move-top {
  top:-170px; 
}
.format_font {
  font-size:12px
}
</style>
