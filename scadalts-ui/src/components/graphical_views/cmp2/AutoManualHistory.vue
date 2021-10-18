<template>
<div class="auto-manual--history">
    <div class="flex">
        <h4>
            {{$t('component.automanual.history.title')}}
        </h4>
        <v-btn x-small fab elevation="1" @click="fetchHisoryData">
            <v-icon>
                mdi-refresh
            </v-icon>
        </v-btn>
    </div>
    <v-simple-table>
        <template v-slot:default>
            <thead class="table--header">
                <tr>
                    <th>{{$t('component.automanual.history.table.username')}}
                    </th>
                    <th>{{$t('component.automanual.history.table.time')}}
                    </th>
                    <th>{{$t('component.automanual.history.table.state')}}
                    </th>
                </tr>
            </thead>
            <tbody class="table--values">
                <tr v-for="(item, index) in history" :key="index">
                    <td>{{item.username}}</td>
                    <td>{{new Date(item.time).toLocaleString()}}</td>
                    <td>{{item.state}}</td>
                </tr>
            </tbody>
        </template>
    </v-simple-table>
</div>
</template>
<script>
/**
 * Auto Manual State History component
 * 
 * @version 2.0.0
 * @author Radek Jajko <rjajko@softq.pl>
 */
export default {

    props: ['cmpId'],

    data() {
        return {
            history: [],
        }
    },

    mounted() {
        this.fetchHisoryData();
    },

    methods: {
        async fetchHisoryData() {
            try {
                let data = (await this.$store.dispatch('getHisotryCMP', this.cmpId)).data;
                this.history = [];
                data.history.forEach(entry => {
                    this.history.push({
                        username: entry.userName,
                        time: entry.unixTime,
                        state: entry.interpretedState
                    });
                });
                this.orderDataByDate();
            } catch (error) {
                console.log(error);
            }
        },

        orderDataByDate() {
            this.history.sort((a, b) => {
                return b.time - a.time;
            });
        }
    }
    
}
</script>
<style scoped>
.auto-manual--history {
    max-height: 300px;
    min-width: 400px;
}
.flex {
    padding: 0 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>