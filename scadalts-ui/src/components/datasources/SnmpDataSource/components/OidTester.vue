<template>
	<div class="snmp-widged">
		<v-card elevation="1">
			<v-card-title> SNMP Host testing </v-card-title>
			<v-card-text>
				<v-text-field
					v-model="oid"
					label="OID Number"
					placeholder=".1.3.6.1.2.1.1.3.0"
				></v-text-field>

				<div class="message-box-container">
					<div class="message-box" v-if="loading">Loading...</div>
					<div v-if="!!status" class="message-box">
						<v-icon v-if="status.type === 'error'"> mdi-alert-circle </v-icon>
						{{ status.message }}
					</div>
				</div>

				<v-btn elevation="1" @click="snmpTest" class="full-width"> Test OID </v-btn>
			</v-card-text>
		</v-card>
	</div>
</template>
<script>
export default {
	data() {
		return {
			oid: '',
			loading: false,
			status: {
				type: '',
				message: '',
			},
		};
	},

	props: {
		datasource: {
			required: true,
			type: Object,
		},
	},

	methods: {
		snmpTest() {
			this.loading = true;
			this.status = null;
			this.$store
				.dispatch('snmpTest', {
					datasource: this.datasource,
					oid: this.oid,
				})
				.then((r) => {
                    if(r.response.includes('snmp.tester.noResponse')) {
                        this.status = {
						    type: 'error',
						    message: this.$t(r.response),
					    };
                    } else {
                        this.status = {
						    type: 'success',
						    message: r.response,
                        }
					};
				})
				.catch((e) => {
					this.status = {
						type: 'error',
						message: this.$t(e.data.response),
					};
				})
				.finally(() => {
					this.loading = false;
				});
		},
	},
};
</script>
<style scoped>
.message-box-container {
	min-height: 40px;
	box-shadow: inset 1px 2px 4px 0px #aaa;
	border-radius: 5px;
	background-color: #0000000d;
	margin: 5px;
	display: flex;
	justify-content: center;
}
.message-box {
	margin: 10px;
	font-style: italic;
}
.modbus-widged {
	max-width: 500px;
}
.full-width {
	width: 100%;
}
</style>
