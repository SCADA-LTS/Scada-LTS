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
					<slot name="selector"></slot>
				</v-col>
			</v-row>
		</v-card-title>

		<v-card-text>
			<v-form ref="datasourceForm" v-model="formValid">
				<v-row>
					<v-col cols="12" :md="8" :sm="12">
						<v-text-field
							autofocus
							v-model="datasource.name"
							label="DataSource Name"
							:rules="[ruleNotNull]"
							required
						></v-text-field>
					</v-col>
					<v-col cols="12" :md="4" :sm="12">
						<v-text-field
							v-model="datasource.xid"
							label="DataSource Export Id"
							:rules="[ruleNotNull]"
							required
						></v-text-field>
					</v-col>
				</v-row>
				<v-row v-if="polling">
					<v-col cols="6">
						<v-text-field
							v-model="datasource.updatePeriod"
							label="Update Period"
							:rules="[ruleNotNull, ruleOnlyNumber]"
							required
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<UpdatePeriodType
							:value="datasource.updatePeriodType"
							@update="onUpdatePeriodTypeUpdate"
							types="1,2,3,4,5"
						>
						</UpdatePeriodType>
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
import UpdatePeriodType from '@/layout/forms/UpdatePeriodType';

export default {
	components: {
		UpdatePeriodType,
	},

	props: {
		title: {
			type: String,
			default: 'Data Source Config',
		},
		creator: {
			type: Boolean,
			default: true,
		},
		polling: {
			type: Boolean,
			default: true,
		},
		datasource: {
			type: Object,
			default: () => {
				return {
					name: '',
					xid: '',
				};
			},
		},
	},

	data() {
		return {
			formValid: false,
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
			ruleOnlyNumber: (v) => !isNaN(v) || this.$t('validation.rule.onlyNumber'),
		}
	},

	methods: {
		cancel() {
			this.$emit('cancel');
		},

		accept() {
			this.$emit('accept');
		},

		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		},

	},
};
</script>
