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
					<slot name="selector"> </slot>
				</v-col>
			</v-row>
		</v-card-title>

		<v-card-text>
			<v-form ref="datapointForm" v-model="formValid">
				<v-row>
					<v-col cols="12" :sm="6">
						<v-text-field
							autofocus
							v-model="datapoint.name"
							label="Data Point Name"
							:rules="[ruleNotNull]"
							required
              @focusout = 'handleFocusOut'
						></v-text-field>
					</v-col>
					<v-col cols="6" :sm="4">
						<v-text-field
							v-model="datapoint.xid"
							label="Data Point Export ID"
							@input="checkXidUnique"
							:rules="[ruleNotNull, ruleXidUnique]"
							required
              @focusout = 'handleFocusOut'
						></v-text-field>
					</v-col>
					<v-col cols="6" :sm="2">
						<v-checkbox
							v-model="datapoint.pointLocator.settable"
							label="Settable"
							:disabled="settableDisabled"
						></v-checkbox>
					</v-col>
					<v-col cols="12">
						<v-text-field
							v-model="datapoint.description"
							label="Description"
              @focusout = 'handleFocusOut'
						></v-text-field>
					</v-col>
				</v-row>
				<slot></slot>
			</v-form>
		</v-card-text>

		<v-card-actions>
			<v-spacer></v-spacer>
			<v-btn text @click="cancel()">{{ $t('common.cancel') }}</v-btn>
			<v-btn color="primary" text @click="accept()" :disabled="!formValid">
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
					description: '',
				};
			},
		},
		settableDisabled: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			formValid: false,
			xidUnique: true,
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
			ruleXidUnique: () => this.xidUnique || this.$t('validation.rule.xid.notUnique'),
		};
	},

	methods: {
		cancel() {
			console.debug('datasources.DataPointCreation.vue::cancel()');
			this.$emit('cancel');
		},

		accept() {
			console.debug('datasources.DataPointCreation.vue::accept()');
			if (this.formValid) {
				this.$emit('accept');
			}
		},

		async checkXidUnique() {
			try {
				let resp = await this.$store.dispatch(
					'requestGet',
					`/datapoint/validate?xid=${this.datapoint.xid}&id=${this.datapoint.id}`,
				);
				this.xidUnique = resp.unique;
				this.$refs.datapointForm.validate();
			} catch (e) {
				console.error('Failed to fetch data');
			}
		},
    async handleFocusOut(){
      this.$emit('textarea-focusout');
    }
	},
};
</script>
<style scoped></style>
