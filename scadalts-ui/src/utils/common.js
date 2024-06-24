export function getAppLocation() {
   var location = window.location;
   var pattern = location.origin + "/(.*?)/";
   var myLocation = location.origin + "/";
   var groups = location.href.match(pattern);
   if(groups && groups.length > 1 && groups[1]) {
       var appName = groups[1];
       if(!appName.includes('.html') && !appName.includes('.htm') && !appName.includes('.shtm') && !appName.includes('#'))
            myLocation = location.origin + "/" + appName+ "/";
   }
   return myLocation;
}
export function isInt32(state) {
   if(!(/^([+-]?[1-9]\d*|0).[0]$/.test(state))
       && !(/^([+-]?[1-9]\d*|0)$/.test(state))) {
       return false;
   }
   const view = new DataView(new ArrayBuffer(32));
   view.setInt32(1, state);
   return Number.parseInt(state) === view.getInt32(1);
}