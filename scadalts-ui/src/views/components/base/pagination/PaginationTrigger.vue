<template>
  <span class="base-pagination-trigger" @click="onClick">
    {{ pageNumber }}
  </span>
</template>
<script>
    export default {
        props: {
            pageNumber: {
                type: Number,
                required: true
            }
        },
        methods: {
            onClick() {
                this.$emit("loadPage", this.pageNumber)
            }
        }
    }
</script>
