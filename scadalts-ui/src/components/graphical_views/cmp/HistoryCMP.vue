<template>
	<div>
		<table>
			<tr>
				<th v-for="(column, index) in columns" :key="index">{{ column.name }} |</th>
			</tr>
			<tr v-for="(record, index) in nData" :key="index">
				<td v-for="(column, indexColumn) in columns" :key="indexColumn">
					{{ record[column.name] }} |
				</td>
			</tr>
		</table>
	</div>
</template>

<script>
import store from '../../../store';
import moment from 'moment';

export default {
	name: 'history-cmp',
	props: ['pxIdViewAndIdCmp'],
	data() {
		return {
			xIdViewAndIdCmp: this.pxIdViewAndIdCmp,
			nData: [],
			columns: [{ name: 'user name' }, { name: 'time' }, { name: 'interpreted state' }],
		};
	},
	methods: {
		loadData: function () {
			let rowData = [];
			store.dispatch('getHisotryCMP', this.xIdViewAndIdCmp).then((ret) => {
				rowData = this._.orderBy(ret.data.history, ['unixTime'], ['desc']);
				this.nData = [];
				for (let i = 0; i < rowData.length; i++) {
					this.nData.push({
						'user name': rowData[i].userName,
						time: moment(rowData[i].unixTime).format('Do MMMM YYYY, HH:mm:ss '),
						'interpreted state': rowData[i].interpretedState,
					});
				}
			});
		},
	},
	created() {
		this.loadData();
	},
};
</script>

<style lang="scss" scoped>
@import '../../../../node_modules/@min-gb/vuejs-components/dist/min-gb.css';
</style>
