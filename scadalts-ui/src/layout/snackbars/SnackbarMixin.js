/**
 * @deprecated
*/
export const snackbarMixin = {
	data() {
		return {
			snackbar: {
				visible: false,
				message: '',
				color: undefined,
				timeout: 5000,
			},
		};
	},

	methods: {
		/**
		 * Show Snackbart message
		 *
		 * Change visibility of the object
		 * and provide a new message to be displayed.
		 * Additionaly you can change snackbar color
		 * or visibility duration.
		 *
		 * @deprecated
		 * @param {string} message - Message to be displayed
		 * @param {string} color - (Optional) Snackbar color
		 * @param {number} timeout - (Optional) Visibility duration
		 */
		showSnackbar(message, color = undefined, timeout = 5000) {
			this.snackbar = {
				visible: true,
				message: message,
				color: color,
				timeout: timeout,
			};
		},

		/**
		 * Show Crud operation result message
		 *
		 * Use generic message provider to display the
		 * Create/Read/Update/Delete operation result status
		 * as a snackbar.
		 * 
		 * @deprecated
		 * @param {string} type - [add, update, delete] Possible message types
		 * @param {boolean} success - (Optional) If is failed show the 'fail' message.
		 */
		showCrudSnackbar(type, success = true) {
			const validTypes = ['add', 'update', 'delete'];
			if (!validTypes.includes(type)) {
				throw `Not valid operation type!\nYou use ${type}\nTry to use one of this types: [add, update, delete]`;
			}

			if (!!success) {
				this.showSnackbar(this.$t(`common.snackbar.${type}.success`), 'success');
			} else {
				this.showSnackbar(this.$t(`common.snackbar.${type}.fail`), 'danger');
			}
		},
	},
};

export default snackbarMixin;
