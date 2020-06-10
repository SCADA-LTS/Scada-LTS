<template>
    <div class="historical-alarms-components">
        <SimplePanel>

            <datepicker :value="fdate" :format="formatter" ref="datapicker" style="margin-top:5px; margin-left:5px; position: relative; width: 120px; float:left; line-height: 30px;"></datepicker>


            <input class="min-gb-input" v-model="frlike" placeholder="rLike filter" style="margin-left: 45px">


            <button class="min-gb-button" style="margin-top: 4px;">Filter</button>

        </SimplePanel>
        <SimpleTable v-bind:data="historicalAlarms" v-bind:columns="columns"></SimpleTable>
        <SimplePanel>

            <SimplePagination class="min-gb-pagination"
                              :current-page="currentPage"
                              :page-count="pageCount"
                              :visible-pages-count="8"
                              @nextPage="pageChangeHandle('next')"
                              @previousPage="pageChangeHandle('pref')"
                              @loadPage="pageChangeHandle"
            ></SimplePagination>

        </SimplePanel>
    </div>
</template>

<script>
    import Components from '@min-gb/vuejs-components'
    import store from '../../store'
    import moment from 'moment'
    import Datepicker from 'vuejs-datepicker';
    //import util from 'util'

    export default {
        el: '#historicalAlarms_component',
        name: 'historicalAlarmsComponent',
        components: {
            ...Components,
            Datepicker
        },
        data() {
            return {
                filterOn: 0,
                fdate: new Date(),
                frlike: '',
                columns: [{name: 'time'},
                    {name: 'name'},
                    {name: 'description'}],
                historicalAlarms: [],
                currentPage: 1,
                pageCount: 10
            }
        },
        methods: {

            formatter(date) {
                return moment(date).format('YYYY-MM-DD');
            },

            getHistoricalAlarms(adate, arlike, page) {

                try {

                    let ldate = '';
                    let recordsCount = 20
                    let loffset = String(recordsCount * page)
                    let llimit = String(recordsCount * (page - 1))
                    let lrlike = ''

                    if (this.filterOn === 1 ) {
                        ldate = moment(adate).format('YYYY-MM-dd')
                    }

                    if (!(arlike === undefined || arlike === null)) {
                        lrlike = String(arlike)
                    }


                    store.dispatch('fakeGetHistoryAlarms', {
                        dateDay: ldate,
                        filterRLike: lrlike,
                        offset: loffset,
                        limit: llimit
                    }).then((ret) => {
                        this.historicalAlarms = ret

                    })
                } catch (e) {
                    console.log(`getHA:${e}`)
                }
            },
            pageChangeHandle(pr) {
                try {
                    if (pr === 'next') {
                        console.log('pageChangeHandle next')
                        return
                    } else if (pr === 'pref') {
                        console.log('pageChangeHandle pref')
                        return
                    }
                    this.currentPage = Number(pr)
                    this.getHistoricalAlarms(this.fdate, this.rlikeFilter, this.currentPage)
                } catch (e) {
                    console.log(`pageCH:${e}`)
                }
            },
            onFilter() {
                try {
                    this.getHistoricalAlarms(this.fdate, this.frlike, this.currentPage)
                } catch (e) {
                    console.log(`created:${e}`)
                }
            }
        },
        created() {
            try {
                this.getHistoricalAlarms(this.fdate, this.frlike, this.currentPage)
            } catch (e) {
                console.log(`created:${e}`)
            }
        },
        mounted() {
            //this.$refs.datapicker.style = "position: relative; width: 120px; float:left; line-height: 34px;"
        }

    }
</script>

<style scoped>
    .historical-alarms-components {
        margin-top: 40px;
        margin-left: 20px;
    }
    input:-moz-read-only { /* For Firefox */
        padding: 0;
        border: 0;
        width: 120px;
        line-height: 32px;
    }

    input {
        padding: 0;
        margin-top: 5px;
        border: 0;
        line-height: 32px;
        width: 120px;
    }

    input[type="text"] {
        width: 120px;
    }

    button:-moz-any {
        padding: 0;
    }

</style>
