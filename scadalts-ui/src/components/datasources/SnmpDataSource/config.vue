<template>
	<DataSourceConfig
		title="SNMP Data Source"
		:datasource="datasource"
		:creator="createMode"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>

		<v-row>
			<v-col cols="3">
				<v-text-field v-model="datasource.host" label="Host Address"></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field v-model="datasource.port" label="Port Number"></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field v-model="datasource.retries" label="Retries"></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field v-model="datasource.timeout" label="Timeout"></v-text-field>
			</v-col>
			<v-col cols="2">
				<v-select label="Snmp Version" v-model="datasource.snmpVersion" :items="snmpVersions">
				</v-select>
			</v-col>
			<v-col cols="10">
				<v-row v-if="datasource.snmpVersion == 0 || datasource.snmpVersion == 1">
					<v-col>
						<v-text-field v-model="datasource.community" label="Community"></v-text-field>
					</v-col>
					<v-col v-if="datasource.snmpVersion == 1">
						<v-checkbox
							label="Traps Enabled"
							v-model="datasource.trapEnabled"
						></v-checkbox>						
					</v-col>
				</v-row>
				<v-row v-else>
					<v-col cols="6">
						<v-text-field v-model="datasource.securityName" label="Security Name"></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-text-field v-model="datasource.contextName" label="Context Name"></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-select label="Security Level" v-model="datasource.securityLevel" :items="snmpSecurityLevel">
						</v-select>
					</v-col>
					<v-col cols="12">
						<v-row v-if="datasource.securityLevel !== 1">
							<v-col cols="6">
								<v-select label="Security Level" v-model="datasource.authProtocol" :items="authProtocols"></v-select>
							</v-col>
							<v-col cols="6">
								<v-text-field v-model="datasource.authPassphrase" label="Context Name"></v-text-field>
							</v-col>
						</v-row>
						<v-row v-if="datasource.securityLevel === 3">
							<v-col cols="6">
								<v-select label="Privacy Protocol" v-model="datasource.privProtocol" :items="privProtocols"></v-select>
							</v-col>
							<v-col cols="6">
								<v-text-field v-model="datasource.privPassphrase" label="Privacy Passphrase"></v-text-field>
							</v-col>
						</v-row>

					</v-col>
				</v-row>
				
			</v-col>

		</v-row>
	</DataSourceConfig>
</template>
<script>
import DataSourceConfig from '../DataSourceConfig';

export default {
	components: {
		DataSourceConfig,
	},

	props: {
		datasource: {
			required: false,
			type: Object,
			default: () => {
				return {
					name: '',
					xid: 'DS_VDS_',
					updatePeriods: 5,
					updatePeriodType: 1,
				};
			},
		},
		createMode: {
			type: Boolean,
			default: true,
		},
	},

	data() {
		return {
			snmpVersions: [{
					text: '1',
					value: 0
				},{
					text: '2c',
					value: 1
				},{
					text: '3',
					value: 3
				}],
			snmpSecurityLevel: [
				{
					text: 'Disable authentication and Encryption',
					value: 1
				},
				{
					text: 'Only authentication',
					value: 2
				},
				{
					text: 'Authentication and Encryption',
					value: 3
				}
			],
			privProtocols: [
				{
					text: 'None',
					value: null
				},
				{
					text: 'DES',
					value: 'DES'
				},
				{
					text: 'AES-128',
					value: 'AES128'
				},
				{
					text: 'AES-192',
					value: 'AES192'
				},
				{
					text: 'AES-256',
					value: 'AES256'
				},
				{
					text: '3DES',
					value: '3DES'
				},
				{
					text: 'AES-192 with 3DES',
					value: 'AES192With3DES'
				},
				{
					text: 'AES-256 with 3DES',
					value: 'AES256With3DES'
				},
			],
			authProtocols: [
				{
					text: 'None',
					value: null
				},
				{
					text: 'MD5',
					value: 'MD5'
				},
				{
					text: 'SHA',
					value: 'SHA'
				},
				{
					text: 'HMAC128-SHA224',
					value: 'HMAC128SHA224'
				},
				{
					text: 'HMAC192-SHA256',
					value: 'HMAC192SHA256'
				},
				{
					text: 'HMAC256-SHA384',
					value: 'HMAC256SHA384'
				},
				{
					text: 'HMAC384-SHA512',
					value: 'HMAC384SHA512'
				},
			]
		}
	},

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			this.$emit('saved', this.datasource);
		},
	},
};
</script>
<style></style>
