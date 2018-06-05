
var ExportImportHierarchiPoints = {
    methods: {
        export(json) {

            /*var v = new Validator();
            var instance = 4;
            var schema = {"type": "number"};
            console.log(v.validate(instance, schema));*/

            try {
                axios({
                    method: 'post',
                    url: 'http://localhost:8080/ScadaBR/api/auth/admin/admin',
                }).then(function (response) {
                    alert(response.data);
                    alert(response.status);
                    alert(response.statusText);
                    alert(response.headers);
                    alert(response.config);
                });
            } catch( err ) {
                alert( err );
            }
            alert('export mixins');
        },
        import() {
            alert('import mixins');
        }
    },
}