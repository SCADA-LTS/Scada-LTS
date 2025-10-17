//Deprecated since = v2.7.8
export const browserUtilsMixin = {
    methods: {
        convertBrowserPath(page) {
            const DEVTOOLS = true;

            let locale = window.location.pathname.split('/')[1];
			if(!!locale) {
				locale += '/';
			}
			let protocol = window.location.protocol;
			let host = window.location.host.split(':');


            if (DEVTOOLS && host[1] === '3000') {
                host[1] = '8080';
                locale = 'Scada-LTS/';
            }

			let x = `${protocol}//${host[0]}:${host[1]}/${locale}${page}`;
			return x;
        }
    }
}

export default browserUtilsMixin;