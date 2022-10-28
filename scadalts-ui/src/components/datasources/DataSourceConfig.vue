<template>
	<v-card id="datasource-config">
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
					<v-col cols="12" :md="8" :sm="12" id="datasource-config--name">
						<v-text-field
							autofocus
							v-model="datasource.name"
							label="DataSource Name"
							:rules="[ruleNotNull]"
							required
						></v-text-field>
					</v-col>
					<v-col cols="12" :md="4" :sm="12" id="datasource-config--xid">
						<v-text-field
							v-model="datasource.xid"
							label="DataSource Export Id"
							@input="checkXidUnique"
							:rules="[ruleNotNull, ruleXidUnique]"
							required
						></v-text-field>
					</v-col>
				</v-row>
				<v-row v-if="polling">
					<v-col cols="6" id="datasource-config--update-periods">
						<v-text-field
							v-model="datasource.updatePeriods"
							label="Update Period"
							:rules="[ruleNotNull, ruleOnlyNumber]"
							required
						></v-text-field>
					</v-col>
					<v-col cols="6" id="datasource-config--period-type">
						<UpdatePeriodType
							:value="datasource.updatePeriodType"
							@update="onUpdatePeriodTypeUpdate"
							:types="availablePeriodTypes"
						>
						</UpdatePeriodType>
					</v-col>
				</v-row>

				<!-- Other component definition slot -->
				<slot></slot>
			</v-form>
		</v-card-text>

		<v-card-actions>
			<v-spacer></v-spacer>
			<v-btn text @click="cancel()">{{ $t('common.cancel') }}</v-btn>
			<v-btn color="primary" text @click="accept()" :disabled="!formValid" id="datasource-config--accept">
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
/**
 * DataSource Configuration Component
 *
 * Base component for configuring a DataSource that handle the common logic for
 * each type of DataSource. It provide the edit such values as Name, Xid and UpdatePeriod.
 * In <slot> tag there are provided the DataSource specific component definition.
 * This component handle the logic for creation and update of DataSource so in
 * the child component that methods are not needed.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
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
		availablePeriodTypes: {
			type: String,
			default: '1,2,3'
		},
		datasource: {
			type: Object,
			default: () => {
				return {
					name: '',
					xid: '',
				};
			},
		}
	},

	mounted() {
		if (this.creator) {
			this.$store.dispatch('getUniqueDataSourceXid').then((resp) => {
				this.datasource.xid = resp;
			});
		}
	},

	data() {
		return {
			formValid: false,
			xidUnique: true,
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
			ruleOnlyNumber: (v) => !isNaN(v) || this.$t('validation.rule.onlyNumber'),
			ruleXidUnique: () => this.xidUnique || this.$t('validation.rule.xid.notUnique'),
		};
	},

	methods: {
		cancel() {
			console.debug('datasources.DataSourceConfig.vue::cancel()');
			this.$emit('cancel');
		},

		accept() {
			console.debug('datasources.DataSourceConfig.vue::accept()');
			this.$emit('accept');
		},

		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		},

		async checkXidUnique() {
			try {
				this.datasource.id = this.datasource.id || -1;
				let resp = await this.$store.dispatch(
					'requestGet',
					`/datasource/validate?xid=${this.datasource.xid}&id=${this.datasource.id}`,
				);
				this.xidUnique = resp.unique;
				this.$refs.datasourceForm.validate();
			} catch (e) {
				console.error('Failed to fetch data');
			}
		},
	},
};
</script>
