<template>
	<div>
		<div v-if="loaded">
			<slot></slot>
		</div>
		<v-skeleton-loader v-else type="article"></v-skeleton-loader>
		<v-dialog v-model="editorVisible" width="900">
			<div v-if="loaded">
				<slot name="config"></slot>
			</div>
			<v-skeleton-loader v-else type="article"></v-skeleton-loader>
		</v-dialog>
	</div>
</template>
<script>
export default {
    props: {
        loaded: {
            type: Boolean,
            default: false
        }
    },

    data() {
        return {
            editorVisible: false,
        }
    },

    methods: {
        openEditor() {
            this.editorVisible = true;
        }
    }
}
</script>