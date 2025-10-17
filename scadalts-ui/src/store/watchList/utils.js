/**
 * Search for DataPoint in PointHierarchy
 * 
 * Deep search for DataPoint in PointHierarchy structure.
 * Returns the DataPoint object if found, otherwise returns null.
 * 
 * @param {Array} array - PointHierarchy array level to search in
 * @param {Number} elementId - DataPointID to search for
 * @param {Boolean} debug [default: false] - Debug mode
 * @returns DataPoint object if found.
 */
export function searchDataPointInHierarchy(array, elementId, debug=false) {
    if(debug) console.debug("UTILS::SearchDataPointInPointHierarchy\nSearching...");
    if(!!array && array.length > 0) {
        let result = array.find(item => (!item.folder && Number(item.id.slice(1)) === elementId));
        if(!!result) {
            return result;
        } else {
            for(let i = 0; i < array.length; i++) {
                if(array[i].folder && !!array[i].children && array[i].children.length > 0) {
                    return searchDataPointInHierarchy(array[i].children, elementId);
                }
            }            
        }
    }
    return null;
}