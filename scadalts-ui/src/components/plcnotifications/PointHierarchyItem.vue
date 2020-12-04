<template>
  <div class="item">
    <div class="inline">
      {{ item.title }}
      <span v-if="item.folder" @click="toggle">
        [{{ showChildren ? "-" : "+" }}]
      </span>
      <div v-if="showChildren">
        <ph-item
          v-for="node in item.children"
          :item="node"
          :schedulers="schedulers"
          :key="node"
        >
        </ph-item>
      </div>
    </div>
    <div v-if="!item.folder" class="inline right">
      <div class="col-xs-12">
        <select class="col-xs-3" v-model="activeScheduler" @change="changed">
          <optgroup label="Available schedulers">
            <option v-for="s in schedulers" :key="s" :value="s">
              {{ s.id }}
            </option>
          </optgroup>
          <optgroup label="Assigned schedulers" v-if="localSchedulers">
            <option v-if="localSchedulers.length == 0">-- None --</option>
            <option v-for="s in localSchedulers" :key="s" :value="s">
              {{ s.id }}
            </option>
          </optgroup>
        </select>
        <input
          type="checkbox"
          class="col-xs-2"
          v-model="activeScheduler.perMail"
          disabled
        />
        <input
          type="checkbox"
          class="col-xs-2"
          v-model="activeScheduler.perSms"
          disabled
        />
        <input
          type="text"
          class="col-xs-4"
          v-model="activeScheduler.description"
          disabled
        />
        <button class="col-xs-1" v-if="saveChanges" @click="saveDataPoint">Save</button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ["item", "schedulers"],
  data() {
    return {
      showChildren: false,
      localSchedulers: undefined,
      saveChanges: false,
      activeScheduler: {
        perMail: false,
        perSms: false,
        description: "--Blank--",
      },
    };
  },
  name: "ph-item",
  mounted() {
    if (this.item.folder) {
      this.fetchPointHierarchyData();
    } else {
      this.fetchDataPointSchedulers();
    }
  },
  methods: {
    toggle() {
      this.showChildren = !this.showChildren;
    },
    changed() {
        if(!!this.localSchedulers) {
            if(this.localSchedulers.find(x => x.id == this.activeScheduler.id)) {
                return;
            }
        }
        this.saveChanges = true;
    },
    saveDataPoint() {
        this.$store.dispatch("bindSchedulerWithDataPoint", { 
            sid: this.activeScheduler.id,
            dpid: this.item.key 
        }).then(() => {
            if(!this.localSchedulers) {
                this.localSchedulers = [];  
            } 
            this.localSchedulers.push(this.activeScheduler);
            this.saveChanges = false;
            console.log("added!")
        })
    },
    async fetchPointHierarchyData() {
      this.item.children = await this.$store.dispatch(
        "getPointHierarchy",
        this.item.key
      );
      console.log(this.item);
    },
    async fetchDataPointSchedulers() {
      this.localSchedulers = await this.$store.dispatch(
        "getSchedulersByDataPoint",
        this.item.key
      );
      if (!!this.localSchedulers) {
        if (this.localSchedulers.length != 0) {
          this.activeScheduler = this.localSchedulers[0];
        } else {
          this.localSchedulers = null;
        }
      }
    },
  },
};
</script>
<style>
.inline {
  display: inline;
}
.right {
  float: right;
  min-width: 40vw;
}
.item {
  padding: 4px 0 4px 20px;
  margin-bottom: 5px;
  border-bottom: 1px #dddddd solid;
}
</style>
<style scoped>
p {
  margin: 0;
}
</style>