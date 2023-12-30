import { fileURLToPath, URL } from 'node:url';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import { defineConfig } from 'vite';

export default defineConfig({
	build: {
		lib: {
			entry: resolve(__dirname, 'src/main.js'),
			name: 'MyLib',
			fileName: 'my-lib',
		},
		rollupOptions: {
			external: ['vue', '@/utils/web-socket-utils', 'vuetify/lib'],
			output: {
				globals: {
					vue: 'Vue',
				},
			},
		},
	},
	plugins: [
		vue(),
		{
			name: 'debugger-plugin',
			resolveId(id) {
				console.log('Resolving module ID:', id);
				return null;
			}
		}
	],
	resolve: {
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url)),
			'@s': fileURLToPath(new URL('./src/store', import.meta.url)),
			'@c': fileURLToPath(new URL('./src/components', import.meta.url)),
			'@layout': fileURLToPath(new URL('./src/layout', import.meta.url)),
			'@dialogs': fileURLToPath(new URL('./src/layout/dialogs', import.meta.url)),
			'@models': fileURLToPath(new URL('./src/models', import.meta.url)),
		},
	},
});

