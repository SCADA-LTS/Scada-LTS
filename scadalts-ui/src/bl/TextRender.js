import DecimalFormat from 'decimal-format';

export default class TextRenderer {

    constructor(def) {
        this.def = def
    }

    render(value) {
        let ret = `${value}`;
        if (this.def.typeName === "textRendererPlain") {
            ret = `${value}${this.def.suffix}`
        } else if (this.def.typeName === "textRendererBinary") {
            if (ret == "true") {
                ret = this.def.oneLabel  
            } else 
            if (ret == "false") {
                ret = this.def.zeroLabel
            }
        }  else if (this.def.typeName === "textRendererAnalog") {
            const df = new DecimalFormat(this.def.format)
            ret = df.format(value)
        } else if (this.def.typeName == "textRendererMultistate") {
            ret = this.def.multistateValues.filter(function(o){ return o.key==value})[0].text
        }
        return ret;
    }

}