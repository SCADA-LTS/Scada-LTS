
Vue.component('export-import', {
    data: function () {
        return {
            json: ''
        }
    },
    template: `
         <div>
            
            <textarea v-model="json" placeholder="JSON with definition hierarchy" rows="15" cols="200">
            
            </textarea></br>
            <button v-on:click="alert('export');">Export</button>
            <button v-on:click="alert('import');">Import</button>
            <p>{{json}}</p>
            
        </div>
    `
});

