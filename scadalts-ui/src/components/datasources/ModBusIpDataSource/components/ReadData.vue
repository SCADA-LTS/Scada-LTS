<template>
	<div>
        <div>
            <v-text-field
                label="Slave ID"
            ></v-text-field>
            <v-text-field
                label="Register range"
            ></v-text-field>
            <v-text-field
                label="Offset"
            ></v-text-field>
            <v-text-field
                label="Number of registers"
            ></v-text-field>
            <v-btn elevation="1">
                Read data
            </v-btn>
            
        </div>
        
		
	</div>
</template>
<script>

export default {
	data() {
		return {      
		};
	},
};
</script>
<style>

</style>
