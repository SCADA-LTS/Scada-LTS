<script>
    import axios from 'axios';

    export default {
        props: ['componentData', 'componentId', 'componentEditor'],
        data() {
            return {
                config: {timeout: 5000, useCredentails: true, credentials: 'same-origin'},
        }
        },
        methods: {
            saveData() {
                this.$emit('saved', this.componentId, this.componentData)
            },
            getDataPointValueXid(xid) {
                if(xid !== undefined) {
                    return new Promise((resolve, reject) => {
                        axios.get(`./api/point_value/getValue/${xid}`, this.config).then(resp => {
                            resolve(resp.data);
                        }).catch(error => {
                            reject(error)
                        })
                    })
                }

            },
            changeComponentColor(component, color) {
                let x = this.$svg.get(component).style().split(';');
                for (let i = 0; i < x.length; i++) {
                    if (x[i].includes('fill:')) {
                        x[i] = `fill:${color}`;
                    }
                }
                this.$svg.get(component).style(x.join(';'))
            },
            changeComponentText(component, text) {
                if (this.$svg.get(component).node.textContent) this.$svg.get(component).node.textContent = text;
            },
            rotateComponent(component, duration = 3000, angle = 360, loop = true) {
                let element = this.$svg.get(component);
                if (element.fx) {
                    if (element.fx.play()) {
                        element.finish()
                    }
                }
                if (loop) {
                    element.animate(duration).rotate(angle).loop()
                } else {
                    element.animate(duration).rotate(angle)
                }
            },
            pauseComponentAnimation(component) {
                if (this.$svg.get(component).fx) this.$svg.get(component).fx.pause();
            },
            finishComponentAnimation(component) {
                if (this.$svg.get(component).fx) this.$svg.get(component).fx.finish();
            },
        }
    }
</script>