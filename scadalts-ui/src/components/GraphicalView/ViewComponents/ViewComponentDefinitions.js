import ViewComponentSimple from '@/components/GraphicalView/ViewComponents/VCSimple/index.vue';
import ViewComponentSimpleJs from '@/components/GraphicalView/ViewComponents/VCSimple/model.js';
import ViewComponentBinaryGraphic from '@/components/GraphicalView/ViewComponents/VCBinaryGraphic/index.vue';
import ViewComponentBinaryGraphicJs from '@/components/GraphicalView/ViewComponents/VCBinaryGraphic/model.js'
import ViewComponentHtml from '@/components/GraphicalView/ViewComponents/VCHtml/index.vue';
import ViewComponentHtmlJs from '@/components/GraphicalView/ViewComponents/VCHtml/model.js'
import ViewComponentAlarmList from '@/components/GraphicalView/ViewComponents/VCAlarmList/index.vue';
import ViewComponentAlarmListJs from '@/components/GraphicalView/ViewComponents/VCAlarmList/model.js';
import ViewComponentButton from '@/components/GraphicalView/ViewComponents/VCButton/index.vue';
import ViewComponentButtonJs from '@/components/GraphicalView/ViewComponents/VCButton/model.js';
import ViewComponentImageChart from '@/components/GraphicalView/ViewComponents/VCImageChart/index.vue';
import ViewComponentImageChartJs from '@/components/GraphicalView/ViewComponents/VCImageChart/model.js';
import ViewComponentMultistateGraphic from '@/components/GraphicalView/ViewComponents/VCMultistateGraphic/index.vue';
import ViewComponentMultistateGraphicJs from '@/components/GraphicalView/ViewComponents/VCMultistateGraphic/model.js';
import ViewComponentLink from '@/components/GraphicalView/ViewComponents/VCLink/index.vue';
import ViewComponentLinkJs from '@/components/GraphicalView/ViewComponents/VCLink/model.js';
import ViewComponentScript from '@/components/GraphicalView/ViewComponents/VCScript/index.vue';
import ViewComponentScriptJs from '@/components/GraphicalView/ViewComponents/VCScript/model.js';
import ViewComponentAnalogGraphic from '@/components/GraphicalView/ViewComponents/VCAnalogGraphic/index.vue'
import ViewComponentAnalogGraphicJs from '@/components/GraphicalView/ViewComponents/VCAnalogGraphic/model.js'

export const viewComponentDefinitions = {
    components: {
        'SIMPLE': ViewComponentSimple,
        'HTML': ViewComponentHtml,
        'BINARYGRAPHIC': ViewComponentBinaryGraphic,
        'MULTISTATEGRAPHIC': ViewComponentMultistateGraphic,
        'ANALOGGRAPHIC': ViewComponentAnalogGraphic,
        'ALARMLIST': ViewComponentAlarmList,
        'BUTTON': ViewComponentButton,
        'LINK': ViewComponentLink,
        'SCRIPT': ViewComponentScript,
        'IMAGECHART': ViewComponentImageChart,
    },

    data() {
        return {
            classicComponentList: [{
                title: 'Simple Point',
                datapointTypes: 'Any type',
                description: 'Display the current value of any datapoint',
                definition: ViewComponentSimpleJs,
            }, {
                title: 'Binary Graphic',
                datapointTypes: 'Binary',
                description: 'Render graphic depengind on the zero and one state of binary data point.',
                definition: ViewComponentBinaryGraphicJs,
            }, {
                title: 'Multistate Graphic',
                datapointTypes: 'Multistate',
                description: 'Assign specific graphic to corresponging DataPoint state value',
                definition: ViewComponentMultistateGraphicJs,
            }, {
                title: 'Analog Graphic',
                datapointTypes: 'Numeric',
                description: 'Render Image Set based on the data point value range',
                definition: ViewComponentAnalogGraphicJs,
            },
            {
                title: 'Html Component',
                datapointTypes: 'No type',
                description: 'Create a custom HTML element',
                definition: ViewComponentHtmlJs,
            }, {
                title: 'Alarm List',
                datapointTypes: 'No type',
                description: 'Display a list of alarms',
                definition: ViewComponentAlarmListJs
            }, {
                title: 'ON/OFF Button',
                datapointTypes: 'Binary',
                description: 'Create a button',
                definition: ViewComponentButtonJs
            }, {
                title: 'Link',
                datapointTypes: 'No type',
                description: 'Create a link',
                definition: ViewComponentLinkJs
            }, {
                title: 'Server side Script',
                datapointTypes: 'No type',
                description: 'Create a script',
                definition: ViewComponentScriptJs
            },
            {
                title: 'Image Chart',
                datapointTypes: 'Any type',
                description: 'Display a chart',
                definition: ViewComponentImageChartJs
            }]
        }
    }
}

export default viewComponentDefinitions;
