/**
 * Validate and conver Data
 *
 * AmChart class require a numeric timestamp
 * to send a GET request so every date provided
 * by user must be converted into valid timestamp
 * value that will mach the further operations.
 *
 * @param {Object} date - Date to be converted
 * @returns {Number} valid Date timestamp
 */
export function getValidDate(date) {
	if (typeof date === 'string') {
		const regex = /\d+-((day)|(month)|(year)|(week)|(hour)|(minute))/g;
		if (!!date.match(regex)) {
			return convertDate(date);
		} else {
			date = new Date(date);
			if (!isNaN(date)) {
				return date.getTime();
			} else {
				throw new Error('Not valid date!');
			}
		}
	} else {
		if (date instanceof Date) {
			return date.getTime();
		} else {
			return date;
		}
	}
}

/**
 * Convert String Data
 * @private
 *
 * Convert dynamic string start date
 * to specific timestamp.
 *
 * @param {string} dateString
 * @returns
 */
function convertDate(dateString) {
	let date = dateString.split('-');
	if (date.length === 2) {
		let now = new Date();
		let multiplier = 1000;
		switch (date[1]) {
			case 'minute':
			case 'minutes':
				multiplier = multiplier * 60;
				break;
			case 'day':
			case 'days':
				multiplier = multiplier * 60 * 60 * 24;
				break;
			case 'week':
			case 'weeks':
				multiplier = multiplier * 60 * 60 * 24 * 7;
				break;
			case 'month':
			case 'months':
				multiplier = multiplier * 60 * 60 * 24 * 7 * 4;
				break;
			case 'year':
			case 'years':
				multiplier = multiplier * 60 * 60 * 24 * 7 * 4 * 12;
				break;
			default:
				multiplier = multiplier * 60 * 60;
		}
		return now.getTime() - Number(date[0]) * multiplier;
	} else {
		throw new Error('Not valid date format! [Use for example: "1-day"]');
	}
}