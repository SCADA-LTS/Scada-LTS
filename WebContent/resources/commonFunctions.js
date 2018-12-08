
function getMyLocation(){
    var pathArray = location.href.split( '/' );
    var protocol = pathArray[0];
    var host = pathArray[2];
    var appScada = pathArray[3];
    var myLocation;
    if (!myLocation) {
        myLocation = protocol + "//" + host + "/" + appScada + "/";
    }
    return myLocation;
}
function byAjaxRestMethod_GET(async,url){
    if(async==null || async==''){
        confirm("One of parameters required by method byAjaxRestMethod_GET in commonFunctions <async> is empty)");
        return null;
    }
    if(url==null || url==''){
        confirm("One of parameters required by method byAjaxRestMethod_GET in commonFunctions <url> is empty)");
        return null;
    }
    return jQuery.ajax({
        type: "GET",
        dataType: "json",
        url:url,
        async: (async==false)?false:true
    }).responseText;
}