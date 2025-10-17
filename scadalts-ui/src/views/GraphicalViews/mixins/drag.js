export const draggableMixin = {
    data() {
        return {
            dragProps: {
                dragging: false,
                ref: null,
                clientX: null,
                clientY: null,
                movementX: 0,
                movementY: 0,
                width: 0,
                height: 0,
            },
            axisLock: {
                active: false,
                key: 16, //Shift key
                x: false,
                y: false,
            }

        }
    },

    methods: {
        draggingStart({ event, ref }) {
            if (this.editMode) {
                event.preventDefault();
                this.dragProps.dragging = true;
                this.dragProps.clientX = event.clientX;
                this.dragProps.clientY = event.clientY;
                this.dragProps.width = ref.$refs.draggableContainer.clientWidth;
                this.dragProps.height = ref.$refs.draggableContainer.clientHeight;
                this.dragProps.ref = ref;
                document.onmousemove = this._draggingMove;
                document.onmouseup = this._draggingEnd;
                this.addAxisLockListener();
            }
        },

        addAxisLockListener() {
            window.addEventListener('keydown', this._enableAxisLock);
            window.addEventListener('keyup', this._disableAxisLock);
        },

        removeAxisLockListener() {
            window.removeEventListener('keydown', this._enableAxisLock);
            window.removeEventListener('keyup', this._disableAxisLock);
        },

        axisLockAction(props) {
            if (this.axisLock.active) {
                if (props.movementX !== 0 && !this.axisLock.x) {
                    this.axisLock.y = true;
                }
                if (props.movementY !== 0 && !this.axisLock.y) {
                    this.axisLock.x = true;
                }
            } else {
                this.axisLock.x = false;
                this.axisLock.y = false;
            }

            if (this.axisLock.y) {
                props.movementY = 0;
            }
            if (this.axisLock.x) {
                props.movementX = 0;
            }
        },

        // --- PRIVATE METHODS --- //
        // --- PRIVATE AXIS LOCK --- //

        _enableAxisLock(event) {
            if (event.keyCode === this.axisLock.key) {
                this.axisLock.active = true;
            }
        },

        _disableAxisLock(event) {
            if (event.keyCode === this.axisLock.key) {
                this.axisLock.active = false;
                this.axisLock.x = false;
                this.axisLock.y = false;
            }
        },

        // --- PRIVATE DRAGGABLE --- //

        _draggingMove(event) {
            const cmp = this.dragProps.ref.$refs.draggableContainer;
            const props = this.dragProps;
            event.preventDefault();

            props.movementX = props.clientX - event.clientX;
            props.movementY = props.clientY - event.clientY;
            props.clientX = event.clientX;
            props.clientY = event.clientY;

            this.axisLockAction(props);

            if (cmp.offsetTop >= 0) {
                cmp.style.top = cmp.offsetTop - props.movementY + 'px';
            } else {
                cmp.style.top = '0px';
            }

            if (cmp.offsetTop + cmp.clientHeight >= this.viewSize.height) {
                cmp.style.top = this.viewSize.height - cmp.clientHeight + 'px';
            }

            if (cmp.offsetLeft >= 0) {
                cmp.style.left = cmp.offsetLeft - props.movementX + 'px';
            } else {
                cmp.style.left = '0px';
            }

            if (cmp.offsetLeft + cmp.clientWidth >= this.viewSize.width) {
                cmp.style.left = this.viewSize.width - cmp.clientWidth + 'px';
            }
        },

        _draggingEnd() {
            this.dragProps.dragging = false;
            let c = this.dragProps.ref;
            c.component.x =
                c.$refs.draggableContainer.offsetLeft >= 0
                    ? c.$refs.draggableContainer.offsetLeft
                    : 0;
            c.component.y =
                c.$refs.draggableContainer.offsetTop >= 0
                    ? c.$refs.draggableContainer.offsetTop
                    : 0;
            this.dragProps.ref = null;
            document.onmouseup = null;
            document.onmousemove = null;
            this.removeAxisLockListener();
            this._disableAxisLock({ keyCode: this.axisLock.key });
        }

    }
}

export default draggableMixin;