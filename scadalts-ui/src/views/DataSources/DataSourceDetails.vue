<template>
	<div class="flex">
        
		<component
			:is="`${datasource.type}`"
			:id="datasource.id"
            ref="component"
		/>

		<v-spacer> </v-spacer>
		<v-btn icon elevation="0">
			<v-icon> mdi-alert-decagram </v-icon>
		</v-btn>
		<v-btn icon elevation="0" @click="openEditor()">
			<v-icon> mdi-pencil </v-icon>
		</v-btn>
		<v-btn icon elevation="0">
			<v-icon> mdi-delete </v-icon>
		</v-btn>
	</div>
</template>
<script>
import dataSourceMixin from '../../components/datasources/DataSourcesMixin.js';

export default {
    props: ['datasource'],

    mixins: [dataSourceMixin],

    methods: {
        openEditor() {
            this.$refs.component.openEditor()
        }
    }
};
</script>
<style></style>
