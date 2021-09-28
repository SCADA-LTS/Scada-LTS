<template>
<div>
    <v-dialog max-width="300" v-model="dialogVisible" persistent>
        <v-card>
            <v-card-title>
                <h2>{{$t('watchlist.datapoint.pointvalue.set')}} {{pointDetails.name}}</h2>
            </v-card-title>
            <v-card-text>
                <v-text-field 
                    :label="$t('watchlist.datapoint.pointvalue.new')" 
                    v-model="newPointValue"
                    append-icon="mdi-send"
					@click:append="sendValue"
                ></v-text-field>
            </v-card-text>
            <v-card-actions>
                <v-btn @click="closeDialog">
                    {{$t('common.close')}}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</div>
</template>
<script>

/**
 * 
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
    name: 'PointValueSet',

    props: {
        pointDetails: {
            type: Object,
            required: true
        },
        dialogVisible: {
            type: Boolean,
            default: false
        }
    },

    data() {
        return {
            newPointValue: null
        }
    },

    methods: {

        sendValue() {
			let request = {
				xid: this.pointDetails.xid,
				type: this.pointDetails.type,
				value: this.newPointValue,
			};

			if (request.type === 1) {
				if (request.value === true || request.value === 'true') {
					request.value = 1;
				} else if (request.value === false || request.value === 'false') {
					request.value = 0;
				}
			}

			this.$store.dispatch('setDataPointValue', request).then(() => {
                this.newPointValue = null;
                this.$emit('closed');
			}).catch(e => {
                console.error(e);
            });
		},

        closeDialog() {
            this.$emit('closed');
        }
    },


    
}
</script>
<style scoped>

</style>