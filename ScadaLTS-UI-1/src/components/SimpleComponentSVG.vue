<template>
  <div>
    <p>{{label}}</p>
    <svg width="100" height="20">
      <rect width="100" height="20" style="fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)"/>
      <text x="5" y="16" fill="red">{{value}}</text>
    </svg>
    <button @click="getData(this.xidPoint)">getData</button>
  </div>
</template>
<script>
  import axios from 'axios';

  export default {
    props: ['xidPoint', 'timeRefresh', 'label', 'value'],
    data() {
      return {}
    },
    methods: {
      getData(xid) {
        const apiGetValuePoint = `./api/point_value/getValue/${xid}`;
        axios.get(apiGetValuePoint).then(response => {
          this.value = response.data.value;
        }).catch(error => {
          console.log(error);
        });
      }
    },
    created() {
      console.log('prev:'+this.xidPoint);
      console.log('prev:'+this.timeRefresh);
      console.log('prev:'+this.label);
      console.log('prev:'+this.value);
      this.getData(this.xidPoint);
      setInterval(
        this.getData(this.xidPoint),
        1000
      );
      console.log('next:'+this.xidPoint);
      console.log('next:'+this.timeRefresh);
      console.log('next:'+this.label);
      console.log('next:'+this.value);
    }
  }
</script>

<style>
</style>
