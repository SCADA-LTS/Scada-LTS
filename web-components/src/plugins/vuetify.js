import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader

import Vue from 'vue';
import Vuetify from 'vuetify/lib';
import 'vuetify/dist/vuetify.min.css'
import colors from 'vuetify/lib/util/colors'



Vue.use(Vuetify);

export default new Vuetify({
  theme: {
    themes: {
      light: {
        primary: colors.green.darken2, // #388E3C
        secondary: colors.brown.darken2, // #5D4037
        accent: colors.green.accent2, // #69F0AE
      },
    },
  },
  icons: {
    iconfont: 'mdi',
  },
});
