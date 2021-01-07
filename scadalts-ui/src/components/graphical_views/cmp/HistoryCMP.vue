<template>
	<div>
		<ag-grid-vue
			style="height: 350px"
			class="ag-theme-balham"
			:columnDefs="columnDefs"
			:rowData="rowData"
		>
		</ag-grid-vue>
	</div>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue';
import moment from 'moment';
import store from '../../../store';

export default {
	name: 'history-cmp',
	props: ['pxIdViewAndIdCmp', 'value'],
	data() {
		return {
			columnDefs: null,
			rowData: null,
			xIdViewAndIdCmp: this.pxIdViewAndIdCmp,
		};
	},
	components: {
		AgGridVue,
	},
	beforeMount() {
		this.columnDefs = [
			{ headerName: 'user', field: 'userName', width: 120, resizable: true },
			{
				headerName: 'time',
				field: 'unixTime',
				width: 200,
				resizable: true,
				sortable: true,
				cellRenderer: (params) => {
					return moment(params.value).format('Do MMMM YYYY, HH:mm:ss ');
				},
			},
			{
				headerName: 'to state',
				field: 'interpretedState',
				width: 120,
				resizable: true,
			},
			{
				headerName: 'values',
				field: 'values',
				resizable: true,
				cellRenderer: (params) => {
					return JSON.stringify(params.value);
				},
			},
		];
	},
	mounted() {},
	watch: {
		value: function (val, oldVal) {
			if (val == true) {
				store.dispatch('getHisotryCMP', this.xIdViewAndIdCmp).then((ret) => {
					this.rowData = this._.orderBy(ret.data.history, ['unixTime'], ['desc']);
				});
			} else {
				this.rowData = null;
			}
		},
	},
};
</script>
