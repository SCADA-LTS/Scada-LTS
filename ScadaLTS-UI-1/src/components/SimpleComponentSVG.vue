<template>
  <svg width="100" height="20">
    <rect width="100" height="20" style="fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)" />
    <text x="5" y="16" fill="red">{{text}}</text>
  </svg>
</template>
<script>
    export default {
      propos: ['text'],
      name: "simple-component-svg",
      data() {
        return {
          text: '00'
        }
      }
  }
</script>

<style>
</style>
