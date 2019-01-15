<template>
  <section>
    <div>
      <div class="well cmp_one_line">
        <div class="cmp_label">{{label}}</div>
        <div class="cmp_state">{{insideState}}</div>
      </div>
      <btn class="cmp_button cmp_one_line" @click="show=!show">
        >
      </btn>
    </div>
    <br/>
    <collapse v-model="show">
      <div class="well cmp_well_details_change">
        <section>
          <btn-group>
            <div v-for="(item, index) in controlsLevel0">
              <btn block v-bind:class="{ selected: (selectActionLevel0==item.name)}" v-on:click="setActionLever0(item.name)">{{item.name}}</btn>
            </div>
          </btn-group>
          <btn-group>
            <div v-if="controlsLevel1.length>0" v-for="(item, index) in controlsLevel1">
              <btn block v-bind:class="{ selected: (selectActionLevel1==item.name)}" v-on:click="setActionLevel1(item.name)">{{item.name}}</btn>
            </div>
          </btn-group>
          <btn-group>
            <btn type="primary" v-on:click="tryChangeModePLC">Confirm changes</btn>
          </btn-group>
          <hr/>
          <alert>Selected: {{selectActionLevel0}} | {{selectActionLevel1}}</alert>
        </section>
      </div>
    </collapse>
  </section>

</template>



<script>
  import moment from "moment";
  import axios from 'axios';
  import {_} from 'vue-underscore';
  import BtnGroup from "uiv/src/components/button/BtnGroup";

  /**
   * @author grzegorz.bylica@gmail.com
   */
  export default {
    components: {BtnGroup},
    props: ['pConfig', 'pLabel', 'pTimeRefresh'],
    data() {
      return {
        show: false,
        strConfig: this.pConfig,
        config:{},
        insideState: 'NOT-CHECKED',
        label: this.pLabel,
        timeRefresh: this.pTimeRefresh,
        controlsLevel0: [],
        controlsLevel1: [],
        selectActionLevel0: '',
        selectActionLevel1: '',
        fruits: [{name:'apple'}, {name:'banana'}, {name:'orange'}]
      }
    },
    methods: {
      checkStatus(){
        Out:
        for (let j=0; j<this.config.state.analiseInOrder.length;j++) {
          let entry = this.config.state.analiseInOrder[j];

          //const apiCMPChek = `./api/cmp/check`;
          //axios.get(apiCMPChek).then(response => {

          let response = {};
          response.data = 1;
          for (let i=0; i<entry.toChecked.length; i++) {
            let entryChecked = entry.toChecked[i];
            try {
              if (entryChecked.last == "true") {
                this.insideState = entry.name;
              } else {
                let toRun = "" + response.data + entryChecked.equals;

                if (eval(toRun)) {
                  this.insideState = entry.name;
                  break Out;
                };
                toRun = "";
              }
            } catch (e) {
              console.log(e);
            }
          }

          //}).catch(error => {
          //  console.log(error);
          //});

        }
      },
      setActionLever0(action) {
        this.selectActionLevel0 = action;
        let found = _.findWhere(this.controlsLevel0, {name:action});
        this.controlsLevel1 = found.toChange;
      },
      setActionLevel1(action) {
        this.selectActionLevel1 = action;
      },
      tryChangeModePLC() {
        alert('try')
      }
    },
    created() {

      try {
        this.config = JSON.parse(this.strConfig);
        this.controlsLevel0 = this.config.control.toChange;
      } catch (e) {
        console.log(e);
      }

      if (this.timeRefresh) {
        setInterval(
          function() {
            this.checkStatus();
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
  }
</script>

<style scoped lang="scss">

  $grey: rgba(0, 0, 0, .1);
  $green: #6b0;
  $black: rgba(0, 0, 0, .5);
  $next_grey: hsla(0, 0%, 100%, .2);


  .cmp_one_line {
    display: inline-block;
  }

  .cmp_button {
    padding: .3em .8em;
    border: 1px solid $grey;
    background: $green linear-gradient($next_grey, transparent);
    border-radius: .2em;
    box-shadow: 0 .05em .25em $black;
    color: white;
    line-height: 1.5;
  }

  .cmp_button_disable {
    background-color: $grey;
  }

  .cmp_well_details_change {
    margin-top: -3.2em;
    margin-bottom: 0;
  }

  .selected {
    color: red;
  }

</style>
