<template>
<div>
    <v-btn-toggle v-if="!!controlsLevel0">
        <v-btn v-for="(item, index) in controlsLevel0" :key="index" @click="saveValues(item)">
            {{item.name}}
        </v-btn>
    </v-btn-toggle>
    <v-btn-toggle v-if="!!controlsLevel1">
        <v-btn v-for="(item, index) in controlsLevel1" :key="index" @click="saveValues(item)">
            {{item.name}}
        </v-btn>
    </v-btn-toggle>
    <ConfirmationDialog
        :btnvisible="false"
        :dialog="confirmationDialogVisible"
        title="Are you sure?"
        message="Are you sure to set that values??"
        @result="onConfirmationDialogResult"
    ></ConfirmationDialog>
</div>

    
</template>

<script>
import ConfirmationDialog from '../../../layout/dialogs/ConfirmationDialog.vue';
import SetValuePointDTO from './SetValuePointDTO';

/**
 * Auto Manual Controls component
 * 
 * @version 2.0.0
 * @author Radek Jajko <rjajko@softq.pl>
 */
export default {
  components: { ConfirmationDialog },
    props: {
        controls: {
            type: Object,
        }
    },

    data() {
        return {
            controlsLevel0: [],
            controlsLevel1: [],
            pointDefinition: new Map(),
            confirmationDialogVisible: false,
            queuedRequest: null,
        }
    },

    mounted() {
        this.controlsLevel0 = this.controls.toChange;
        this.controls.definitionPointToSaveValue.forEach(def => {
            this.pointDefinition.set(def.def, {xid: def.xid, comments: def.comments});
        });

    },

    methods: {
        saveValues(item) {
            this.controlsLevel1 = [];
            if(!!item.toChange && item.toChange.length > 0) {
                this.controlsLevel1 = item.toChange;
            } 
            console.log(item);
            if(!!item.confirmation) {
                this.confirmationDialogVisible = true;
                this.queuedRequest = item;
            } else {
                this.postValue(null, item.name, item.save);
            }
            

        },

        postValue(id, name, changes) {
            let requestData = [];
            changes.forEach(c => {
                requestData.push(
                    new SetValuePointDTO(this.pointDefinition.get(c.refDefPoint).xid, c.value)
                )
            });

            this.$store.dispatch('setCmpValue', {id, name, requestData});
        },

        onConfirmationDialogResult(result) {
            this.confirmationDialogVisible = false;
            if(result) {
                this.postValue(null, this.queuedRequest.name, this.queuedRequest.save);
            }
        }
    }
    
    
}
</script>
<style scoped>

</style>