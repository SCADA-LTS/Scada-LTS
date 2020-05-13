<template>
    <div class="pagination">
        <button
                :disabled="isPreviousButtonDisabled"
                @click.native="previousPage"
        >Pref
        </button>

        <PaginationTrigger
                v-for="(paginationTrigger, index) in paginationTriggers"
                :key="paginationTrigger"
                :pageNumber="paginationTrigger"
                class=""
                :pageNumber="index+1"
                @loadPage="onLoadPage"
        />

        <button
                :disabled="isNextButtonDisabled"
                @click.native="nextPage">
            Next
        </button>


    </div>
</template>

<script>

    import PaginationTrigger from "./PaginationTrigger"

    export default {
        name: 'pagination',
        components: {
            PaginationTrigger
        },
        props: {
            currentPage: {
                type: Number,
                required: true
            },
            pageCount: {
                type: Number,
                required: true
            },
            visiblePagesCount: {
                type: Number,
                default: 5
            }
        },
        data() {
            return {
            }
        },
        computed: {
            isPreviousButtonDisabled() {
                return this.currentPage === 1
            },
            isNextButtonDisabled() {
                return this.currentPage === this.pageCount
            },
            paginationTriggers() {
                // const currentPage = this.currentPage
                //
                // const pageCount = this.pageCount
                //
                // const visiblePagesCount = this.visiblePagesCount
                //
                // const visiblePagesThreshold = (visiblePagesCount -1) / 2
                // console.log(`visiblePagesThreshold ${visiblePagesThreshold}`)
                //const paginationTriggersArray = Array(this.visiblePagesCount -1)
                const paginationTriggersArray = Array(this.pageCount -1)
                console.log(`paginationTriggersArray ${JSON.stringify(paginationTriggersArray)}`)
                return paginationTriggersArray

                // if (currentPage <= visiblePagesThreshold + 1) {
                //     pagintationTriggersArray[0] = 1
                //     const pagintationTriggers = pagintationTriggersArray.map(
                //         (paginationTrigger, index) => {
                //             return pagintationTriggersArray[0] + index
                //         }
                //     )
                //     pagintationTriggers.push(pageCount)
                //     return pagintationTriggers
                // }
                // if (currentPage >= pageCount - visiblePagesThreshold + 1) {
                //     const pagintationTriggers = pagintationTriggersArray.map(
                //         (paginationTrigger, index) => {
                //             return pageCount - index
                //         }
                //     )
                //     pagintationTriggers.reverse().unshift(1)
                //     return pagintationTriggers
                // }
                // pagintationTriggersArray[0] = currentPage - visiblePagesThreshold + 1
                // const pagintationTriggers = pagintationTriggersArray.map(
                //     (paginationTrigger, index) => {
                //         return pagintationTriggersArray[0] + index
                //     }
                // )
                // pagintationTriggers.unshift(1);
                // pagintationTriggers[pagintationTriggers.length - 1] = pageCount
                // return pagintationTriggers
            }
        },
        methods: {
            nextPage() {
                this.$emit('nextPage')
            },
            previousPage() {
                this.$emit('previousPage')
            },
            onLoadPage(value) {
                this.$emit('loadPage', value)
            }
        },
        created() {

        },
        mounted() {

        },
    }
</script>

<style lang="scss" scoped>

</style>
