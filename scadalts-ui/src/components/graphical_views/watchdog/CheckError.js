export default class CheckError extends Error {
    constructor(message = 'Validation check error', ...params) {
        super(...params);

        if(Error.captureStackTrace) {
            Error.captureStackTrace(this, CheckError);
        }

        this.message = message;
        this.date = new Date();
    }
}