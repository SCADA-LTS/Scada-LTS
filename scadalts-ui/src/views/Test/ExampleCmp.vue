<template>
	<div>
		<h4>Example of the use of the CMP component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<!-- <CMP /> -->
				</div>
				<div class="col-xs-12">
					<AutoManual 
						pLabel="Auto"
						:pConfig="configuration"
						:pTimeRefresh="4000"
						:pWidth="260"
						
					></AutoManual>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import CMP from '../../components/graphical_views/cmp/CMP';
import AutoManual from '../../components/graphical_views/cmp2/AutoManual';

export default {
	name: 'example-cmp',
	components: {
		CMP,
		AutoManual,
	},

	data() {
		return {
			configuration: {
				state: {
					analiseInOrder: [
						{
							name: 'SPS OFFLINE',
							toChecked: [
								{
									xid: 'DP_sps_offline',
									equals: '==false',
									describe: 'SPS Datasource is disabled!',
									toNote: true,
								},
							],
							disabled: true,
						},
						{
							name: 'Pumpe Ein',
							toChecked: [
								{
									xid: 'DP_pumpe_ein',
									equals: '==true',
									describe: 'Eh Beckenentleerung MM Pumpe Ein',
								},
							],
							
						},
						{
							name: 'PLS Hand',
							toChecked: [
								{
									xid: 'DP_pls_hand',
									equals: '==true',
									describe: 'Eh Beckenentleerung MM PLS-Hand',
								},
							],
						},
						{
							name: 'PLS Hand ein',
							toChecked: [
								{
									xid: 'DP_hand_ein',
									equals: '==true',
									describe: 'Eh Beckenentleerung MM PLS-Hand ein',
								},
							],
						},
						{
							name: 'PLS Hand aus',
							toChecked: [
								{
									xid: 'DP_hand_aus',
									equals: '==true',
									describe: 'Eh Beckenentleerung MM PLS-Hand aus',
								},
							],
						},
						{
							name: 'Auto',
							toChecked: [
								{
									xid: 'DP_auto',
									equals: '==true',
									describe: 'Eh Beckenentleerung MM PLS-Auto',
								},
							],
						},
						{
							name: 'UNKNOW',
							toChecked: [
								{
									last: 'true',
									describe: '',
								},
							],
						},
					],
				},
				control: {
					label: 'change to:',
					definitionPointToSaveValue: [
						{
							xid: 'DP_pls_hand',
							def: 'PLS_AKT',
							comment: '',
						},
						{
							xid: 'DP_auto',
							def: 'AUTO',
							comment: '',
						},
						{
							xid: 'DP_hand_aus',
							def: 'AUS',
							comment: '',
						},
						{
							xid: 'DP_hand_ein',
							def: 'EIN',
							comments: '',
						},
					],
					toChange: [
						{
							name: 'Auto',
							save: [
								{
									refDefPoint: 'PLS_AKT',
									value: '0',
								},
								{
									refDefPoint: 'AUS',
									value: '0',
								},
								{
									refDefPoint: 'EIN',
									value: '0',
								},
								{
									refDefPoint: 'AUTO',
									value: '1',
								},
							],
							runDirectlyBeforeShowSubMenu: 'true',
						},
						{
							name: 'PLS Hand',
							save: [
								{
									refDefPoint: 'EIN',
									value: '0',
								},
								{
									refDefPoint: 'AUS',
									value: '0',
								},
								{
									refDefPoint: 'AUTO',
									value: '0',
								},
								{
									refDefPoint: 'PLS_AKT',
									value: '1',
								},
								{
									refDefPoint: 'AUS',
									value: '1',
								},
							],
							runDirectlyBeforeShowSubMenu: 'true',
							toChange: [
								{
									name: 'Stop',
									save: [
										{
											refDefPoint: 'EIN',
											value: '0',
										},
										{
											refDefPoint: 'AUS',
											value: '1',
										},
									],
									confirmation: 'true',
								},
								{
									name: 'Start',
									save: [
										{
											refDefPoint: 'AUS',
											value: '0',
										},
										{
											refDefPoint: 'EIN',
											value: '1',
										},
									],
									confirmation: 'true',
								},
								{
									name: 'Error',
									save: [
										{
											refDefPoint: 'AUS',
											value: '0',
										},
										{
											refDefPoint: 'EIN',
											value: '1',
										},
									],
									confirmation: 'true',
								},
							],
						},
					],
				},
			},
		};
	},
};
</script>

<style scoped></style>
