export function getAppLocation() {
   let location = window.location;
   let pattern = location.origin + "/(.*?)/";
   let myLocation = location.origin + "/";
   let groups = location.href.match(pattern);
   if(groups && groups.length > 1 && groups[1]) {
       let appName = groups[1];
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