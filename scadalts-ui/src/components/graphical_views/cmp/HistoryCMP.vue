<template>
    <div>
        <ag-grid-vue style="height: 350px;"
                     class="ag-theme-balham"
                     :columnDefs="columnDefs"
                     :rowData="rowData">
        </ag-grid-vue>
    </div>
</template>

<script>
  import {AgGridVue} from 'ag-grid-vue'
  import moment from 'moment'
  import store from '../../../store'

  export default {
    name: 'history-cmp',
    props: [ 'pxIdViewAndIdCmp'],
    data () {
      return {
        columnDefs: null,
        rowData: null,
        xIdViewAndIdCmp: this.pxIdViewAndIdCmp
      }
    },
    components: {
      AgGridVue
    },
    mounted() {

      this.columnDefs = [
        {headerName: 'id', field: 'id', width: 50, resizable: true},
        {headerName: 'user', field: 'userName', width: 120, resizable: true},
        {
          headerName: 'time', field: 'unixTime', width: 200, resizable: true,
          cellRenderer: (params) => {
            return moment(params.value).format('Do MMMM YYYY, HH:mm:ss ');
          }
        },
        {headerName: 'to state', field: 'interpretedState', width: 120, resizable: true},
        {headerName: 'values', field: 'values', resizable: true,
        cellRenderer: (params) => {
          return JSON.stringify(params.value)
        }}
      ]
      console.log(`xIdViewAndIdCmp:${this.xIdViewAndIdCmp}`)
      store.dispatch('getHisotryCMP', this.xIdViewAndIdCmp ).then((ret) => {
           this.rowData = ret.data.history
      })
      // this.$store.dispatch('getHisotryCMP', 1 ).then((ret) => {
      // this.$store.dispatch('getHisotryCMP', 1 ).then((ret) => {
      //   this.rowData = ret.data.history
      // })
    }
  }
</script>
