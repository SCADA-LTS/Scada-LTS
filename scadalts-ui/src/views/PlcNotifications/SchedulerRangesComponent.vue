<template>
  <div>
    <div class="col-xs-12 table-header">
      <p class="col-xs-1">{{ $t("plcnotifications.table.id") }}</p>
      <p class="col-xs-3">{{ $t("plcnotifications.table.hour.start") }}</p>
      <p class="col-xs-3">{{ $t("plcnotifications.table.hour.stop") }}</p>
      <p class="col-xs-3">{{ $t("plcnotifications.table.description") }}</p>
      <p class="col-xs-2">{{ $t("plcnotifications.table.operations") }}</p>
    </div>
    <div v-for="r in rangeList" :key="r.id" class="col-xs-12">
      <p class="col-xs-1">{{ r.id }}</p>
      <input class="col-xs-3" type="number" v-model="r.hourStart" />
      <input class="col-xs-3" type="number" v-model="r.hourStop" />
      <input class="col-xs-3" type="text" v-model="r.description" />
      <button class="col-xs-1" @click="updateRange(r)">
        {{ $t("plcnotifications.buttons.update") }}
      </button>
      <button class="col-xs-1" @click="deleteRange(r)">
        {{ $t("plcnotifications.buttons.delete") }}
      </button>
    </div>
    <div class="col-xs-12">
      <p class="col-xs-1">#</p>
      <input class="col-xs-3" type="number" v-model="range.hourStart" />
      <input class="col-xs-3" type="number" v-model="range.hourStop" />
      <input class="col-xs-3" type="text" v-model="range.description" />
      <button class="col-xs-2" @click="saveRange()">
        {{ $t("plcnotifications.buttons.add") }}
      </button>
    </div>
  </div>
</template>
<script>
export default {
  name: "SchedulerRangesComponent",

  data() {
    return {
      range: {
        id: undefined,
        hourStart: 0,
        hourStop: 0,
        description: undefined,
      },
    };
  },

  computed: {
    rangeList() {
      return this.$store.state.plcNotifications.rangeList;
    },
  },

  mounted() {},

  methods: {
    
    saveRange() {
      this.$store.dispatch("postRange", this.range).then(() => {
        this.range = {
          id: undefined,
          hourStart: 0,
          hourStop: 0,
          description: undefined,
        };
      });
    },

    updateRange(range) {
        this.$store.dispatch("updateRange", range);
    },

    deleteRange(range) {
        this.$confirm({
            title: "Delete Range",
            content: "Do you realy want to delete this Time Range?"
        }).then(() => {
            this.$store.dispatch("deleteRange", range);
        })
    }
  },
};
</script>
<style>
</style>