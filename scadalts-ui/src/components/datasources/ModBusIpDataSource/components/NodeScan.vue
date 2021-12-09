<template>
	<div>
        <div>

            <v-btn @click="scan" v-if="!scanRunning">
                Scan for nodes
            </v-btn>
            <v-btn @click="cancel" v-else>
                Cancel
            </v-btn>
        </div>
        <div>
            <v-list>
                <v-list-item>
                    Item
                </v-list-item>
            </v-list>
        </div>
		
	</div>
</template>
<script>

export default {
	props: {
		datasource: {
			required: true,
			type: Object,
		},
	},

	data() {
		return {      
            scanRunning: false,
            nodes: [],
		};
	},

    methods: {
        scan() {
            this.scanRunning = true;
            this.$store.dispatch('modbusNodeScan', this.datasource.id).then((res) => {
                this.nodes = res;
            }).catch(() => {
                console.error('Failed to scan for nodes');
            }).finally(() => {
                this.scanRunning = false;
            });
        },

        cancel() {
            this.scanRunning = false;
            this.$store.dispatch('modbusNodeScanCancel', this.datasource.id)
            this.nodes = [];
        }

    }
};
</script>
<style>

</style>
