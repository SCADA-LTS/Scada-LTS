/**
 * Connection Status Utils Mixin
 * @mixin
 *
 * Additional methods for control the internet connection status.
 * Detect the internet connection changes and invoke the callback functions.
 *
 * @version 1.0.0
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
export const connStatusUtilsMixin = {
	data() {
		return {
			appOnline: navigator.onLine,
			onAppOnline: () => {
				console.log('Internet connection established!');
			},
			onAppOffline: () => {
				console.log('Internet connection lost!');
			},
		};
	},

	mounted() {
		window.addEventListener('online', this.connStatusChange);
		window.addEventListener('offline', this.connStatusChange);
	},

	beforeDestroy() {
		window.removeEventListener('online', this.connStatusChange);
		window.removeEventListener('offline', this.connStatusChange);
	},

	methods: {
		connStatusChange() {
			this.appOnline = navigator.onLine;
			if (this.appOnline) {
				this.onAppOnline();
			} else {
				this.onAppOffline();
			}
		},
	},
};

export default connStatusUtilsMixin;
