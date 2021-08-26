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
				<v-row v-if="datasource.snmpVersion == '1' || datasource.snmpVersion == '2c'">
					<v-col>
						<v-text-field v-model="datasource.community" label="Community"></v-text-field>
					</v-col>
					<v-col v-if="datasource.snmpVersion == '2c'">
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
						<v-row v-if="datasource.securityLevel !== 0">
							<v-col cols="6">
								<v-select label="Security Level" v-model="datasource.authProtocol" :items="authProtocols"></v-select>
							</v-col>
							<v-col cols="6">
								<v-text-field v-model="datasource.authPassphrase" label="Context Name"></v-text-field>
							</v-col>
						</v-row>
						<v-row v-if="datasource.securityLevel === 2">
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
			snmpVersions: ['1', '2c', '3'],
			snmpSecurityLevel: [
				{
					text: 'Disable authentication and Encryption',
					value: 0
				},
				{
					text: 'Only authentication',
					value: 1
				},
				{
					text: 'Authentication and Encryption',
					value: 2
				}
			],
			privProtocols: [
				{
					text: 'None',
					value: 0
				},
				{
					text: 'DES',
					value: 1
				},
				{
					text: 'AES128',
					value: 2
				},
				{
					text: 'AES192',
					value: 3
				},
				{
					text: 'AES256',
					value: 4
				},
				{
					text: '3DES',
					value: 5
				},
				{
					text: 'AES192With3DES',
					value: 6
				},
				{
					text: 'AES256With3DES',
					value: 7
				},
			],
			authProtocols: [
				{
					text: 'None',
					value: 0
				},
				{
					text: 'MD5',
					value: 1
				},
				{
					text: 'SHA',
					value: 2
				},
				{
					text: 'HMAC128SHA224',
					value: 3
				},
				{
					text: 'HMAC192SHA256',
					value: 4
				},
				{
					text: 'HMAC256SHA384',
					value: 5
				},
				{
					text: 'HMAC384SHA512',
					value: 6
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
