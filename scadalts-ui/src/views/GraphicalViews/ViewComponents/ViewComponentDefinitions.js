import ViewComponentSimple from './VCSimple';
import ViewComponentSimpleJs from './VCSimple/model';
import ViewComponentBinaryGraphic from './VCBinaryGraphic';
import ViewComponentBinaryGraphicJs from './VCBinaryGraphic/model'
import ViewComponentHtml from './VCHtml';
import ViewComponentHtmlJs from './VCHtml/model'


export const viewComponentDefinitions = {
    components: {
        'SIMPLE': ViewComponentSimple,
        'HTML': ViewComponentHtml,
        'BINARY_GRAPHIC': ViewComponentBinaryGraphic,
    },

    data() {
        return {
            classicComponentList: [{
                title: 'Simple Point',
                datapointTypes: '',
                description: '',
                definition: ViewComponentSimpleJs,
            }, {
                title: 'Binary Graphic',
                datapointTypes: 'Binary',
                description: '',
                definition: ViewComponentBinaryGraphicJs,
            }, {
                title: 'Html Component',
                datapointTypes: '',
                description: '',
                definition: ViewComponentHtmlJs,
            }]
        }
    }
}

export default viewComponentDefinitions;