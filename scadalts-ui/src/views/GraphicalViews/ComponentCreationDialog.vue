<template>
    <v-dialog max-width="600" persistent v-model="dialog">
        <v-card>
            <v-card-title>
                Add component
            </v-card-title>
            <v-card-text>
                <v-row>
                    <v-col cols="6" v-for="(cmp, index) in classicComponentList" :key="index">
                        <v-hover v-slot="{hover}">
                        <v-card
                            :elevation="hover ? 6: 2"
                            :class="{'on-hover': hover}"
                        >
                        <v-card-title>
                            {{cmp.title}}
                        </v-card-title>
                        <v-card-text>
                            DataPoint: {{cmp.datapointTypes}}
                        </v-card-text>
                        <div class="component-overlay" v-if="hover" @click="addComponent(cmp.definition)">
                            <v-icon class="component-overlay--icon">mdi-plus</v-icon>
                            
                        </div>


                        </v-card>
                        

                    </v-hover>
                    </v-col>
                </v-row>

            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn text @click="closeDialog" color="success">
                    {{ $t('common.close') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    
</template>
<script>
import ViewComponentMixin from './ViewComponents/ViewComponentDefinitions';

export default {

    mixins: [ViewComponentMixin],

    data() {
        return {
            dialog: false,
        }
    },

    methods: {

        addComponent(definition) {
            this.$store.commit('ADD_COMPONENT_TO_PAGE', new definition());
            this.closeDialog();
        },

        openDialog() {
            this.dialog = true;
        },
        closeDialog() {
            this.dialog = false;
        },
    },


}
</script>
<style scoped>
.component-overlay {
    background-color: #00000026;
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    justify-content: center;
    align-items: center;
}
.component-overlay--icon {
    padding: 10px;
    background-color: white;
    border-radius: 50%;
    box-shadow: 0px 2px 5px 0px #00000033;

}
</style>