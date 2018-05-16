
var script = document.createElement("script"); // Make a script DOM node
script.src = "resources/vue-components/export-import/form-on-dlg-export-import.js";


var ExportImportJSON = {
    methods: {
        confirm(callback, options) {
            options = Object.assign({
                title: "Are you sure?",
                text: "To confirm",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes",
                closeOnConfirm: false
            }, options);

            swal(options, callback);
        }
    },
}