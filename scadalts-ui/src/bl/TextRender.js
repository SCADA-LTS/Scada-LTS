import DecimalFormat from 'decimal-format';

export default class TextRenderer {

    #DEFAULT_COLOR = '#212121';

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

    color(value) {
        
        let ret = this.#DEFAULT_COLOR
        if (this.def.typeName === "textRendererPlain") {
            // defaulot color
        } else if (this.def.typeName === "textRendererBinary") {
            if (ret == "true") {
                ret = this.def.oneColour
            } else 
            if (ret == "false") {
                ret = this.def.zeroColour
            }
        }  else if (this.def.typeName === "textRendererAnalog") {
            // default color
        } else if (this.def.typeName == "textRendererMultistate") {
            ret = this.def.multistateValues.filter(function(o){ return o.key==value})[0].colour
            if (ret == null) {
                ret = this.#DEFAULT_COLOR
            } 
        }
        return ret;

    }

}