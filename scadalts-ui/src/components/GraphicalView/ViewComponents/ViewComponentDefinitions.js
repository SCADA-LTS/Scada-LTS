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
import ViewComponentLink from './VCLink';
import ViewComponentLinkJs from './VCLink/model';
import ViewComponentScript from './VCScript';
import ViewComponentScriptJs from './VCScript/model';
import ViewComponentAnalogGraphic from './VCAnalogGraphic'
import ViewComponentAnalogGraphicJs from './VCAnalogGraphic/model'


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