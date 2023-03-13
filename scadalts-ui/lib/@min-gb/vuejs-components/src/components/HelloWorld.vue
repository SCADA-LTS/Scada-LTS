<template>
    <div class="hello">
<!--        <div>-->
<!--            <h4>Debug</h4>-->
<!--            <pre>{{$data | json 4}}</pre>-->
<!--        </div>-->
        <Refresh></Refresh>
        <SimplePanel>
            <imask-input class="min-gb-input"
                         v-model="fdate"
                         :mask="Date"
                         radix="."
                         @accept="onAccept"
                         placeholder='Date filter'
            />

            <imask-input class="min-gb-input"
                         v-model="rlikeFilter"
                         :mask="RegExp"
                         radix="."
                         :unmask="true"
                         @accept="onAccept"
                         placeholder='rLike filter'
            />

            <button class="min-gb-button">Filter</button>

        </SimplePanel>
        <SimpleTable v-bind:data="adata" v-bind:columns="columns"></SimpleTable>
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
    import Refresh from "./Refresh"
    import SimpleTable from "./SimpleTable"
    import SimplePanel from "./SimplePanel"
    import {IMaskDirective} from 'vue-imask'
    import {IMaskComponent} from 'vue-imask'
    import SimplePagination from "./SimplePagination"



    export default {
        name: 'HelloWorld',
        data() {
            return {
                fdate:'',
                rlikeFilter:'',
                currentPage: 1,
                pageCount: 10,
                adata: [],
                columns: [{name: 'test'},
                    {name: 'test1'},
                    {name: 'test2'}],
                value: '',
                mask: {
                    mask: '{8}000000',
                    lazy: false
                },
                onAccept (e) {
                    const maskRef = e.detail;
                    this.value = maskRef.value;
                    console.log('accept', maskRef.value);
                },
                onComplete (e) {
                    const maskRef = e.detail;
                    console.log('complete', maskRef.unmaskedValue);
                },
                loadData(date, rlike, page) {
                    let ldata = [];
                    // on page 20 elements
                    for (let i=0;i<20; i++) {
                       ldata.push( {"test": `vtrc1r${ (i * page) }`, "test1": `vtc2r${ (i * page) }`, "test2": `vtc3r${ (i * page) }`, "level": "5"})
                    }
                    return ldata;
                }

            }
        },
        methods: {
            pageChangeHandle(pr) {
                if (pr==='next') {
                    console.log('pageChangeHandle next')
                    return
                } else if (pr==='pref') {
                    console.log('pageChangeHandle pref')
                    return
                }
                this.currentPage = Number(pr)
                this.adata = this.loadData(this.fdate,this.rlikeFilter,this.currentPage)
            },
        },
        props: {
            msg: String
        },
        components: {
            SimplePanel,
            Refresh,
            SimpleTable,
            'imask-input': IMaskComponent,
            SimplePagination,
        },
        directives: {
            imask: IMaskDirective
        },
        created() {
            this.adata = this.loadData(this.fdate,this.rlikeFilter,this.currentPage)
        }
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

    h3 {
        margin: 40px 0 0;
    }

    ul {
        list-style-type: none;
        padding: 0;
    }

    li {
        display: inline-block;
        margin: 0 10px;
    }

    a {
        color: #42b983;
    }
</style>
