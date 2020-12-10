<template>
      <div class="panel-body">
        <div>
          <input type="url" name="url" id="url"
            placeholder="example.com"
            pattern=".*" size="30"
            required
            v-model="url">
        </div>
        <div>
            <btn size="xs" type="success" v-on:click="save()">Save</btn>
        </div>
      </div>
</template>

<script>
 import axios from 'axios';

/**
 * @author grzegorz.bylica@gmail.com
 *
 */

export default {
  data() {
    return {
      url:''
    };
  },
  methods: {
    save() {
      const api = `./api/systemSettings/saveSMSDomain/${this.url}`;
      axios.post(api).then(response => {
        //TODO change color or disable button
      }).catch(error => {
        console.error(error);
      });
    },
    load() {
      const api= `./api/systemSettings/getSMSDomain`;
      axios.get(api).then(response => {
          this.url = response.data
      }).catch(error => {
        console.error(error);
      });
    }
  },
  mounted() {
    this.load();
  }
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
