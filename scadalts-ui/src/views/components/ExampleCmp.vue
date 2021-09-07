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
				<!-- <CMP pxIdViewAndIdCmp="1" pLabel="test" pTimeRefresh="2000" :pConfig='{"state": {"analiseInOrder": [{"name": "Working","toChecked": [{"xid": "DPCB_01","equals": "==true","describe": "Pump is working"}]},{"name": "Disabled","toChecked": [{"xid": "DPCB_01","equals": "==false","describe": "Pump is disabled"}]},{"name":"Oth2", "toChecked":[{"xid": "DPCB_02","equals": "==true","describe": "Engine is working"}]},{"name":"EnNotW", "toChecked": [{"xid": "DPCB_02","equals": "==false","describe": "Engine is not working"}]}]},"control": {"label": "change to:","definitionPointToSaveValue": [{"xid": "DPCB_01","def": "PUMP","comment": "Pump reference"}, {"xid": "DPCB_02","def": "ENGINE","comment": "Engine reference"}],"toChange": [{"name": "ON","save": [{"refDefPoint": "PUMP","value": "TRUE"}],"runDirectlyBeforeShowSubMenu": "true"}, {"name": "OFF","save": [{"refDefPoint": "PUMP","value": "FALSE"}],"runDirectlyBeforeShowSubMenu": "true"}]}}'></CMP> -->
				<RadCMP :pConfig='{"state": {"analiseInOrder": [{"name": "Working","toChecked": [{"xid": "DPCB_01","equals": "==true","describe": "Pump not working", "toNote":"true", "toNext":"true"},{"xid": "DPCB_01","equals": "==false","describe": "Pump is working", "toNote":"true"}]},{"name": "Disabled","toChecked": [{"xid": "DPCB_01","equals": "==false","describe": "Pump is disabled"}]},{"name":"Oth2", "toChecked":[{"xid": "DPCB_02","equals": "==true","describe": "Engine is working", "toNote":"true"}]},{"name":"EnNotW", "toChecked": [{"xid": "DPCB_02","equals": "==false","describe": "Engine is not working"}]}]},"control": {"label": "change to:","definitionPointToSaveValue": [{"xid": "DPCB_01","def": "PUMP","comment": "Pump reference"}, {"xid": "DPCB_02","def": "ENGINE","comment": "Engine reference"}],"toChange": [{"name": "ON","save": [{"refDefPoint": "PUMP","value": "TRUE"}],"runDirectlyBeforeShowSubMenu": "true"}, {"name": "OFF","save": [{"refDefPoint": "PUMP","value": "FALSE"}],"runDirectlyBeforeShowSubMenu": "true"}]}}'></RadCMP>
			</div>
		</div>
	</div>
</template>

<script>
import CMP from '../../components/graphical_views/cmp/CMP';
import RadCMP from '../../components/graphical_views/cmp/RadCMP';

export default {
	name: 'example-cmp',
	components: {
		CMP,
		RadCMP
	},
};
</script>

<style scoped></style>
