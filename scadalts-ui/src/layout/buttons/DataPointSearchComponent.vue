<template>
    <v-autocomplete
        :items="datapoints"
        item-text="name"
        prepend-icon="mdi-magnify">
    </v-autocomplete>
    
</template>
<script>
export default {
    name: 'DataPointSearchComponent',

    data() {
        return {
            datapoints: undefined,
        }
    },

    mounted() { 
        this.fetchDataPointSimpleList();
    },

    methods: {
        async fetchDataPointSimpleList() {
            this.datapoints = await this.$store.dispatch("getDataPointSimpleList");
        }
    },
    
}
</script>
<style scoped>

</style>