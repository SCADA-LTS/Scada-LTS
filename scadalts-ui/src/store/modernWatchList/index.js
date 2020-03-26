/**
 * @author rjajko@softq.pl
 */
const modernWatchList = {
    state: {
        activeWatchList: null,
        activeUser: null,
        chartCounter: 0,
        chartList: [],
    },
    mutations: {
        loadActiveUser(state) {
            state.activeUser = document.getElementsByClassName("userName").item(0).innerText;
        },
        loadCharts(state) {
            if (state.chartList.length == 0) {
                let loadedChart = localStorage.getItem(`MWL_Chart_${state.activeUser}`);
                if (loadedChart != null) {
                    state.chartList = JSON.parse(loadedChart);
                    state.chartCounter = state.chartList[state.chartList.length - 1].id;
                }
            }
        },
        addChart(state, chart) {
            state.chartList.push(chart);
            this.commit('saveCharts');
        },
        editChart(state, chart) {
            let chartId = state.chartList.findIndex(e => e.id == chart.id);
            state.chartList[chartId] = chart;
            this.commit('saveCharts');
        },
        deleteChart(state, chart) {
            state.chartList = state.chartList.filter(e => e.id != chart.id);
            this.commit('saveCharts');
        },
        saveCharts(state) {
            localStorage.setItem(`MWL_Chart_${state.activeUser}`, JSON.stringify(state.chartList));
        },
        incrementChartId(state) {
            state.chartCounter = state.chartCounter + 1;
        },
    },
    actions: {

    },
    getters: {
        getCharts(state) {
            return state.chartList;
        },
        getNextChartId(state) {
            return state.chartCounter;
        }
    }
}
export default modernWatchList
