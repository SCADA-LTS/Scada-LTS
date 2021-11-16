import ViewComponentSimple from './VCSimple.vue';
import ViewComponentHtml from './VCHtml.vue';
import ViewComponentBinaryGraphic from './VCBinaryGraphic.vue';

export const viewComponentDefinitions = {
    components: {
        'SIMPLE': ViewComponentSimple,
        'HTML': ViewComponentHtml,
        'BINARY_GRAPHIC': ViewComponentBinaryGraphic,
    }
}

export default viewComponentDefinitions;