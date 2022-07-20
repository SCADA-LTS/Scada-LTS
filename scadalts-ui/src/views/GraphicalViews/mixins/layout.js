/**
 * @mixin
 * Layout mixin for GraphicalViews.
 * 
 * Mixin that is strictly used for layouting the Graphical View elements.
 * This is used by GraphicalViewPage.vue file and this mixin use its propterties.
 * 
 * Provide a library of layouting functions such as align or distribute.
 * Each element in the view can be layouted based on the specific context.
 * All selected components are stored inside the selectedComponents array.
 * 
 * There are two types of contexts:
 *  - Canvas context: the context is the canvas element.
 *  - Selection context: the context are all selected graphical view elements.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export const layoutMixin = {
    data() {
        return {
            layoutContext: 0, //0: GraphicalView, 1: Active selection
            selectedComponents: [],
        }
    },

    computed: {
        activeComponent() {
            if (this.selectedComponents.length === 1) {
                return this.selectedComponents[0];
            } else {
                return null;
            }
        },
        activeComponentsSize() {
            return this.selectedComponents.length;
        }
    },

    methods: {
        /**
         * Select component.
         * 
         * Add that component to the selectedComponents array.
         * That will be the refferrence for the layouting functions.
         * 
         * @param {BaseViewComponent} component
         */
        componentSelected(component) {
            this.selectedComponents.push(component);
        },

        /**
         * Deselect all components
         * 
         * Use right mouse button click to deselect all components.
         * Prevent from default context menu event and reset the selection.
         * 
         * @param {MouseEvent} event 
         */
        deselectComponents(event) {
            event.preventDefault();
            this.layoutContext = 0;
            console.log(this.layoutContext);
            this.selectedComponents.forEach(c => {
                c.selected = false;
            });
            this.selectedComponents = [];
        },

        /**
         * Move component to the left
         * 
         * Basing on the active context move the selected components 
         * to the left side of the canvas or to the left side of the selection.
         */
        layoutMoveLeft() {
            if (this.layoutContext === 0) {
                this._moveLeftCanvas();
            } else if (this.layoutContext === 1) {
                this._moveLeftSelection();
            }
        },

        /**
         * Align components to the center horizontally
         * 
         * Basing on the active context move the selected components 
         * to the center of the canvas or to the center of the selection
         * base on the horizontal axis. Component will be aligned to the center
         * of its body.
         */
        layoutHorizontalCenter() {
            if (this.layoutContext === 0) {
                this._alignHorizontalCenterCanvas();
            } else if (this.layoutContext === 1) {
                this._alignHorizontalCenterSelection();
            }
        },

        /**
         * Move component to the right
         * 
         * Basing on the active context move the selected components 
         * to the right side of the canvas or to the right side of the selection.
         */
        layoutMoveRight() {
            if (this.layoutContext === 0) {
                this._moveRightCanvas();
            } else if (this.layoutContext === 1) {
                this._moveRightSelection();
            }
        },

        /**
         * Move component to the top
         * 
         * Basing on the active context move the selected components 
         * to the top border of the canvas or to the top side of the selection.
         */
        layoutMoveTop() {
            if (this.layoutContext === 0) {
                this._moveTopCanvas();
            } else if (this.layoutContext === 1) {
                this._moveTopSelection();
            }
        },

        /**
         * Align components to the center verticaly
         * 
         * Basing on the active context move the selected components 
         * to the center of the canvas or to the center of the selection
         * base on the vertical axis. Component will be aligned to the center
         * of its body.
         */
        layoutVerticalCenter() {
            if (this.layoutContext === 0) {
                this._alignVerticalCenterCanvas();
            } else if (this.layoutContext === 1) {
                this._alignVerticalCenterSelection();
            }
        },

        /**
         * Move component to the bottom
         * 
         * Basing on the active context move the selected components 
         * to the bottom border of the canvas or to the bottom side of the selection.
         */
        layoutMoveBottom() {
            if (this.layoutContext === 0) {
                this._moveBottomCanvas();
            } else if (this.layoutContext === 1) {
                this._moveBottomSelection();
            }
        },

        /**
         * Distribute components vertically
         * 
         * Organize the components and distribute them vertically
         * based on the active selection. To invoke that function
         * the user must select at least three components.
         */
        layoutVerticalDistribute() {
            this.selectedComponents = this.selectedComponents.sort(
                (a, b) => a.component.y - b.component.y,
            );
            let count = this.selectedComponents.length - 1;
            let min = this.selectedComponents[0].component.y;
            let max = this.selectedComponents[count].component.y;
            let step = (max - min) / count;
            this.selectedComponents.forEach((c, i) => {
                c.component.y = min + (step * i);
            });
        },

        /**
         * Distribute components horizontally
         * 
         * Organize the components and distribute them horizontally
         * based on the active selection. To invoke that function
         * the user must select at least three components.
         */
        layoutHorizontalDistribute() {
            this.selectedComponents = this.selectedComponents.sort(
                (a, b) => a.component.x - b.component.x,
            );
            let count = this.selectedComponents.length - 1;
            let min = this.selectedComponents[0].component.x;
            let max = this.selectedComponents[count].component.x;
            let step = (max - min) / count;

            this.selectedComponents.forEach((c, i) => {
                c.component.x = min + (step * i);
            });
        },


        // --- Private methods --- //

        _moveLeftCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.x = 0;
            });
        },

        _moveLeftSelection() {
            let position = Infinity;
            this.selectedComponents.forEach(c => {
                if (c.component.x < position) {
                    position = c.component.x;
                }
            });

            this.selectedComponents.forEach(c => {
                c.component.x = position;
            });
        },

        _alignHorizontalCenterCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.x = this.viewSize.width / 2 - c.$refs.draggableContainer.clientWidth / 2;
            });
        },

        _alignHorizontalCenterSelection() {
            let widthArray = [];
            this.selectedComponents.forEach(c => {
                widthArray.push(c.component.x);
            });

            let position = widthArray.reduce((a, b) => a + b, 0) / widthArray.length;

            this.selectedComponents.forEach(c => {
                c.component.x = position - c.$refs.draggableContainer.clientWidth / 2;
            });
        },

        _moveRightCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.x = this.viewSize.width - c.$refs.draggableContainer.clientWidth;
            });
        },

        _moveRightSelection() {
            let position = -Infinity;
            let width = -Infinity;
            this.selectedComponents.forEach(c => {
                if (c.component.x > position) {
                    position = c.component.x;

                }
                if (c.$refs.draggableContainer.clientWidth > width) {
                    width = c.$refs.draggableContainer.clientWidth;
                }
            });

            this.selectedComponents.forEach(c => {
                c.component.x = position + width - c.$refs.draggableContainer.clientWidth;
            });
        },

        _moveTopCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.y = 0;
            });
        },

        _moveTopSelection() {
            let position = Infinity;
            this.selectedComponents.forEach(c => {
                if (c.component.y < position) {
                    position = c.component.y;
                }
            });
            this.selectedComponents.forEach(c => {
                c.component.y = position;
            });
        },

        _alignVerticalCenterCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.y = this.viewSize.height / 2 - c.$refs.draggableContainer.clientHeight / 2;
            });
        },

        _alignVerticalCenterSelection() {
            let heightArray = [];
            this.selectedComponents.forEach(c => {
                widthArray.push(c.component.y);
            });

            let position = widthArray.reduce((a, b) => a + b, 0) / heightArray.length;

            this.selectedComponents.forEach(c => {
                c.component.y = position - c.$refs.draggableContainer.clientHeight / 2;
            });
        },

        _moveBottomCanvas() {
            this.selectedComponents.forEach(c => {
                c.component.y = this.viewSize.height - c.$refs.draggableContainer.clientHeight;
            });
        },

        _moveBottomSelection() {
            let position = -Infinity;
            let height = -Infinity;
            this.selectedComponents.forEach(c => {
                if (c.component.y > position) {
                    position = c.component.y;
                }
                if (c.$refs.draggableContainer.clientHeight > height) {
                    height = c.$refs.draggableContainer.clientHeight;
                }
            });
            this.selectedComponents.forEach(c => {
                c.component.y = position + height - c.$refs.draggableContainer.clientHeight;
            });
        },

    }
}

export default layoutMixin;