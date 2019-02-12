<template>
  <section>
    <div>
      <div class="well cmp_one_line">
        <div class="cmp_label">{{label}}</div>
        <div class="cmp_state">{{insideState}}</div>
      </div>
      <btn v-if="disabledChange" class="cmp_button cmp_one_line cmp_button_disable"> > </btn>
      <btn v-else class="cmp_button cmp_one_line" @click="show=!show"> > </btn>
    </div>
    <br/>
    <collapse v-model="show">
      <div class="well cmp_well_details_change">
        <section>
          <btn-group>
            <div v-for="(item, index) in controlsLevel0">
              <btn block v-bind:class="{ selected: (selectActionLevel0==item.name)}"
                   v-on:click="setActionLeve0(item.name)">{{item.name}}
              </btn>
            </div>
          </btn-group>
          <btn-group>
            <div v-if="controlsLevel1.length>0" v-for="(item, index) in controlsLevel1">
              <btn block v-bind:class="{ selected: (selectActionLevel1==item.name)}"
                   v-on:click="setActionLevel1(item.name)">{{item.name}}
              </btn>
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
    <collapse v-model="errorsNotification">
      <div class="well cmp_well_error_notyfication">
        <section>
          <div v-for="(er, index) in errors">
            <alert type="danger"><b>{{er}}</b></alert>
          </div>
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
   *
   */
  class ChangeDataDTO {
    constructor(xid, value, resultOperationSave, error) {
      this.xid = xid;
      this.value = value;
      this.resultOperationSave = resultOperationSave;
      this.error = error;
    }
  }


  /**
   * @author grzegorz.bylica@gmail.com
   */
  class ApiCMP {
    get(xIds) {
      return new Promise((resolve, reject) => {
        const apiCMPChek = `./api/cmp/get/${xIds}`;
        if (xIds.length > 0) {
          axios.get(apiCMPChek).then(response => {
            resolve(response)
          }).catch(error => {
            reject(error);
          });
        } else {
          const reason = new Error('Nothing to do');
          reject(reason);
        }
      })
    }
    set(newData) {
      return new Promise((resolve, reject) => {
        if (newData.length > 0) {
          axios({
              method: 'post',
              url: './api/cmp/set',
              headers: {},
              data: newData
          }).then(response => {
            resolve(response)
          }).catch(error => {
            reject(error);
          });
        } else {
          const reason = new Error('Nothing to do');
          reject(reason);
        }
      })
    }
  };


  /**
   * @author grzegorz.bylica@gmail.com
   */
  export default {
    components: {
      BtnGroup
    },
    props: ['pConfig', 'pLabel', 'pTimeRefresh'],
    data() {
      return {
        show: false,
        errors: [],
        errorsNotification: false,
        strConfig: this.pConfig,
        config: {},
        insideState: 'NOT-CHECKED',
        label: this.pLabel,
        timeRefresh: this.pTimeRefresh,
        controlsLevel0: [],
        controlsLevel1: [],
        selectActionLevel0: '',
        selectActionLevel1: '',
        disabledChange: false
      }
    },
    methods: {
      checkStatus() {
        // create buffor data to query
        let xIds = [];
        for (let j = 0; j < this.config.state.analiseInOrder.length; j++) {
          let entry = this.config.state.analiseInOrder[j];
          for (let i = 0; i < entry.toChecked.length; i++) {
            let entryChecked = entry.toChecked[i];
            if (entryChecked.last == "true") {
              this.insideState = entry.name;
            } else {
              xIds.push(entryChecked.xid);
            }
          }
        }
        // interpreted data from server state
        new ApiCMP().get(xIds).then(response => {
          let errors = [];
          for (let j = 0; j < this.config.state.analiseInOrder.length; j++) {
            let entry = this.config.state.analiseInOrder[j];
            let toBreak = false;
            for (let i = 0; i < entry.toChecked.length; i++) {
              let entryChecked = entry.toChecked[i];
              try {
                if (entryChecked.last == "true") {
                  this.insideState = entry.name;
                } else {
                  for (let k = 0; k < response.data.length; k++) {
                    if (response.data[k].xid.toUpperCase().trim() == entryChecked.xid.toUpperCase().trim()) {
                      let toRun = "" + response.data[k].value + entryChecked.equals;
                      if (eval(toRun)) {
                        this.insideState = entry.name;
                        this.disabledChange = !!entry.disable;
                        if (!!entryChecked.toNoteError) {
                          errors.push(entryChecked.describe);
                        }
                        // if toBreak = true out of any checking next
                        toBreak = !(!!entryChecked.toNext);
                        // out of currently check because we finded in response equals xid
                        break;
                      }
                      toRun = "";
                    }
                  }

                }
              } catch (e) {
                console.log(e);
              }
              // if not checking next (entryChecked.toNext==false)
              if (toBreak) {
                break
              }
            }
            // because we want to not checking next (toBreak==true) or we have errors and we don't need check next analiseInOrder
            this.setErrors(errors);
            if ( (errors.length > 0) || toBreak) {
              break;
            }
          }
        }).catch(er => {
          console.log(er.message);
        });
      },
      setActionLeve0(action) {
        if (this.selectActionLevel0 == action) {
          // unselect
          this.selectActionLevel0 = '';
        } else {
          this.selectActionLevel0 = action;
          let found = _.findWhere(this.controlsLevel0, {name: action});
          this.controlsLevel1 = found.toChange;
          this.selectActionLevel1 = '';
        }
      },
      setActionLevel1(action) {
        if (this.selectActionLevel1 == action) {
          //unselect
          this.selectActionLevel1 = '';
        } else {
          this.selectActionLevel1 = action;
          this.selectActionLevel0 = '';
        }
      },
      setErrors(errors) {
        this.errorsNotification = errors.length>0;
        this.errors = errors;
      },
      tryChangeModePLC() {
        let newData = [];
        let action = null;
        let control = null;
        if (this.selectActionLevel1 != '') {
          action = this.selectActionLevel1;
          control = this.controlsLevel1;
        } else if (this.selectActionLevel0 != '') {
          action = this.selectActionLevel0;
          control = this.controlsLevel0;
        } else {
          // Nothing to do;
        }
        if (action != null) {
          let foundLevel = _.findWhere(control, {name: action});
          let toSave = foundLevel.save;
          for (let i = 0; i < toSave.length; i++) {
            let xid = _.findWhere(this.config.control.definitionPointToSaveValue, {def: toSave[i].refDefPoint});
            let change = new ChangeDataDTO( xid.xid, toSave[i].value, "", "");
            newData.push(change)
          }
          if (newData.length > 0) {
            new ApiCMP().set(newData).then(response => {
              //console.log(response);
            }).catch(er => {
              console.log(er);
            });
          }
        }
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
          function () {
            this.checkStatus();
          }.bind(this),
          this.timeRefresh
        );
      }
    },
    filters: {
      moment: function (date) {
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

  .cmp_well_error_notyfication {
    margin-top: -3.2em;
    margin-bottom: 0;
    background-color: #f4f7f2;
  }

  .selected {
    color: red;
  }

</style>
