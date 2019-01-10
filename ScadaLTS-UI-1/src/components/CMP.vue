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
      <div class="well cmp_well_details_change">Hi there.</div>
    </collapse>
  </section>

</template>



<script>
  import moment from "moment";
  import axios from 'axios';

  /**
   * @author grzegorz.bylica@gmail.com
   */
  export default {
    props: ['pConfig', 'pLabel', 'pTimeRefresh'],
    data() {
      return {
        show: false,
        strConfig: this.pConfig,
        config:{},
        insideState: 'NOT-CHECKED',
        label: this.pLabel,
        timeRefresh: this.pTimeRefresh,
        control: []
      }
    },
    methods: {
      checkStatus(){
        this.config.state.analiseInOrder.forEach(function(entry){
          //const apiCMPChek = `./api/cmp/check`;
          //axios.get(apiCMPChek).then(response => {
            let response = {};
            response.data = 0;
            entry.toChecked.forEach(function(entryChecked){
              const toRun = ""+response.data + entryChecked.equals;

              if (eval(toRun)) {
                console.log('test');
                this.insideState = entry.name;
              };
            });
          //}).catch(error => {
          //  console.log(error);
          //});

          //console.log(entry);
        })
      }
    },
    created() {
      console.log(this.label);
      console.log(this.timeRefresh);
      console.log(this.strConfig);
      try {
        this.config = JSON.parse(this.strConfig);
      } catch (e) {
        console.log(e);
      }
      console.log(this.config.state);

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

</style>
