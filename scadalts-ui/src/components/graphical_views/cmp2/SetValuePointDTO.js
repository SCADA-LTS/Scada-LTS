export class SetValuePointDTO {

    constructor(xid, value) {
        this.xid = xid;
        this.value = value;
        this.resultOperationSave = "";
        this.error = "";
    }
}

export default SetValuePointDTO;