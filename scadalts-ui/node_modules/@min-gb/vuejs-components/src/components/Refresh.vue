<template>
    <div v-bind:class='{
                        "min-gb-alert-refresh": isAlert(),
                        "min-gb-warning-refresh": isWarning(),
                        "min-gb-ok-refresh": isOk()
                    }'>
        <slot></slot>
<!--        Last Refresh: <span>{{lastDateRun | moment("yyyy-MM-dd h:mm:ss")}}</span>-->
    </div>
</template>

<script>

    import moment from 'vue-moment'

    export default {
        data() {
            return {
                history: [],
                lastDateRun: new Date(),
                interval: 1000  // miliseconds
            }
        },
        methods: {
            isAlert() {
                return (new Date() - this.lastDateRun > 10000)
            },
            isWarning() {
                return (new Date() - this.lastDateRun > 50000)
            },
            isOk() {
                return (new Date() - this.lastDateRun >= 2000)
            }

        },
        filters: {
            moment: function (date) {
                return moment(date).format('yyyy-MM-dd h:mm:ss a');
            }
        }
    }
</script>

<style scoped>
    .min-gb-alert-refresh {
        background-color: red;
        color: white;
    }

    .min-gb-warning-refresh {
        background-color: orange;
        color: #2c3e50;
    }

    .min-gb-ok-refresh {
        background-color: white;
        color: grey;
    }
</style>
