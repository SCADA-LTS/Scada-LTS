<template>
    <div>
        <div class="min-gb-pagination">
        <button class="btn-pagination"
                :disabled="isPreviousButtonDisabled"
                @click="previousPage"
        >Pref
        </button>
        <span v-if="section > 1" v-on:click="section=section-1" class="btn-pagination" style="font-weight: bolder">...</span>
        <SimplePaginationTrigger
                v-for="(item, index) in paginationTriggers"
                :key="item*((section-1)*pageCount)"
                v-bind:class='{"min-gb-current-page": (currentPage===(index+1+((section-1)*pageCount)))}'
                :pageNumber="index+1+((section-1)*pageCount)"
                @loadPage="onLoadPage(index+1+((section-1)*pageCount))"
        />
            <span v-on:click="section=section+1" class="btn-pagination" style="font-weight: bolder">...</span>
        <button class="btn-pagination"
                :disabled="isNextButtonDisabled"
                @click="nextPage">
            Next
        </button>
        </div>

    </div>
</template>

<script>

    import SimplePaginationTrigger from "./SimplePaginationTrigger"


    export default {
        name: 'pagination',
        components: {
            SimplePaginationTrigger
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
                section: 1
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
                return Array(this.pageCount)
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
                console.log(`onLoadPage: ${value}`)
                this.$emit('loadPage', value)

            }
        },
        created() {

        },
        mounted() {

        },
    }
</script>

<style scoped>
    .btn-pagination {
        padding: 5px 5px 5px 5px;
        border: none;
    }
    .min-gb-pagination {
        padding: 0px 0px 0px 0px;
        margin-top: 5px;
        margin-left: 5px;
        margin-right: 5px;
        width: auto;
    }
    .min-gb-current-page {
        font-weight: bold;
        color: darkred;
    }

</style>
