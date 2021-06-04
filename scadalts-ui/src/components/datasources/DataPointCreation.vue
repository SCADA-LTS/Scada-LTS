<template>
	<v-card>
		<v-card-title>
			<v-row justify="space-between" align="center">
				<v-col cols="8">
					<h1>
						<span v-if="creator"> Create </span>
						<span v-else> Update </span>
						<span>
							{{ title }}
						</span>
					</h1>
				</v-col>
				<v-col cols="4">
                    <slot name="selector">
                    </slot>
				</v-col>
			</v-row>
		</v-card-title>

		<v-card-text>
			<v-row>
				<v-col cols="12" :sm="6">
					<v-text-field v-model="datapoint.name" label="Data Point Name"></v-text-field>
				</v-col>
				<v-col cols="6" :sm="4">
					<v-text-field v-model="datapoint.xid" label="Data Point Export ID"></v-text-field>
				</v-col>
                <v-col cols="6" :sm="2">
                    <v-checkbox v-model="datapoint.settable" label="Settable"></v-checkbox>
                </v-col>
				<v-col cols="12">
					<v-text-field v-model="datapoint.desc" label="Description"></v-text-field>
				</v-col>
			</v-row>
            <slot></slot>
		</v-card-text>

		<v-card-actions>
			<v-spacer></v-spacer>
			<v-btn text @click="cancel()">{{ $t('common.cancel') }}</v-btn>
			<v-btn color="primary" text @click="accept()">
				<span v-if="creator">
					{{ $t('common.create') }}
				</span>
				<span v-else>
					{{ $t('common.update') }}
				</span>
			</v-btn>
		</v-card-actions>
	</v-card>
</template>
<script>
export default {
	props: {
		title: {
			type: String,
			default: 'Data Source Config',
		},
		creator: {
			type: Boolean,
			default: true,
		},
		datapoint: {
			type: Object,
			default: () => {
				return {
					desc: '',
				}
			}
		},
	},

	methods: {
		cancel() {
			this.$emit('cancel');
		},

		accept() {
			this.$emit('accept');
		},
	},
};
</script>
<style scoped></style>
