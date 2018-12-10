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
