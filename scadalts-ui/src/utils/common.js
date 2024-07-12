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

export function getEventList(event, sortByParam, sortDescParam, eventList) {

    let temp = eventList;
    let sortBy = sortByParam;
    let sortDesc = sortDescParam;

    if(!sortByParam || sortByParam.length == 0) {
        sortBy = ['id'];
    }

    if(!sortDescParam || sortDescParam.length == 0) {
        sortDesc = [true];
    }

    if(event.action === 'CREATE') {
        temp.push(event);
        temp.sort(function(a,b) {return comparatorBy(a, b, sortBy, sortDesc)});
        temp.splice(-1);
        return temp;
    } else if(event.action === 'DELETE') {
        temp = temp.filter(function(item) {
            return item.id != event.id;
        });
        temp.sort(function(a,b) {return comparatorBy(a, b, sortBy, sortDesc)});
        return temp;
    } else if(event.action === 'UPDATE') {
        temp = temp.filter(function(item) {
            return item.id != event.id;
        });
        temp.push(event);
        temp.sort(function(a,b) {return comparatorBy(a, b, sortBy, sortDesc)});
        return temp;
    } else if(event.action === 'RESET') {
        return [];
    }
}

function comparatorBy(a, b, sortBy, sortDesc) {
   for(let i = 0; i < sortBy.length; i++) {
       let by = sortBy[i];
       let desc = sortDesc[i];
       let actual = 0;
       if(desc) {
           actual = b[by] - a[by];
       } else {
           actual = a[by] - b[by];
       }
       if(actual != 0) {
           return actual;
       }
   }
   return 0;
}