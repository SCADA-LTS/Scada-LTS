import ViewComponentSimple from './VCSimple';
import ViewComponentSimpleJs from './VCSimple/model';
import ViewComponentBinaryGraphic from './VCBinaryGraphic';
import ViewComponentBinaryGraphicJs from './VCBinaryGraphic/model'
import ViewComponentHtml from './VCHtml';
import ViewComponentHtmlJs from './VCHtml/model'
import ViewComponentAlarmList from './VCAlarmList';
import ViewComponentAlarmListJs from './VCAlarmList/model';
import ViewComponentButton from './VCButton';
import ViewComponentButtonJs from './VCButton/model';
import ViewComponentImageChart from './VCImageChart';
import ViewComponentImageChartJs from './VCImageChart/model';
import ViewComponentMultistateGraphic from './VCMultistateGraphic';
import ViewComponentMultistateGraphicJs from './VCMultistateGraphic/model';


export const viewComponentDefinitions = {
    components: {
        'SIMPLE': ViewComponentSimple,
        'HTML': ViewComponentHtml,
        'BINARY_GRAPHIC': ViewComponentBinaryGraphic,
        'MULTISTATE_GRAPHIC': ViewComponentMultistateGraphic,
        'ALARMLIST': ViewComponentAlarmList,
        'BUTTON': ViewComponentButton,
        'IMAGE_CHART': ViewComponentImageChart,
    },

    data() {
        return {
            classicComponentList: [{
                title: 'Simple Point',
                datapointTypes: '',
                description: 'Display the current value of any datapoint',
                definition: ViewComponentSimpleJs,
            }, {
                title: 'Binary Graphic',
                datapointTypes: 'Binary',
                description: '',
                definition: ViewComponentBinaryGraphicJs,
            }, {
                title: 'Multistate Graphic',
                datapointTypes: 'Multistate',
                description: '',
                definition: ViewComponentMultistateGraphicJs,
            }, {
                title: 'Html Component',
                datapointTypes: '',
                description: 'Create a custom HTML element',
                definition: ViewComponentHtmlJs,
            }, {
                title: 'Alarm List',
                datapointTypes: '',
                description: 'Display a list of alarms',
                definition: ViewComponentAlarmListJs
            }, {
                title: 'Button',
                datapointTypes: '',
                description: 'Create a button',
                definition: ViewComponentButtonJs
            }, {
                title: 'Image Chart',
                datapointTypes: '',
                description: 'Display a chart',
                definition: ViewComponentImageChartJs
            }]
        }
    }
}

export default viewComponentDefinitions;