<template>
  <section>
    <div>
      <div class="well cmp_one_line" >Hi there.</div>
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
    export default {
        name: "CMP.vue",
        data () {
          return {
            show: false
          }
        }
    }
</script>

<style scoped lang="scss">

  $grey: rgba(0,0,0,.1);
  $green: #6b0;
  $black: rgba(0,0,0,.5);
  $next_grey: hsla(0,0%,100%,.2);


  .cmp_one_line {
    display:inline-block;
  }
  .cmp_button {
    padding: .3em .8em;
    border: 1px solid $grey;
    background: $green linear-gradient($next_grey,transparent);
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
