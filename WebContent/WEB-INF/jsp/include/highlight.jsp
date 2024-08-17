<link rel="stylesheet" href="resources/highlightjs/default.min.css">
<script src="resources/highlightjs/highlight.min.js"></script>
<script src="resources/highlightjs/css.min.js"></script>
<script src="resources/highlightjs/javascript.min.js"></script>

<script>
function updateCodeText(text, destination) {
    let result_element = document.querySelector(destination);
    if(text[text.length-1] == "\n") {
      text += " ";
    }
    result_element.innerHTML = text.replace(new RegExp("&", "g"), "&amp;").replace(new RegExp("<", "g"), "&lt;");
    hljs.highlightElement(result_element);
}

function updateCodeTextEscaped(text, destination) {
    let result_element = document.querySelector(destination);
    if(text[text.length-1] == "\n") {
      text += " ";
    }
    result_element.innerHTML = text;
    hljs.highlightElement(result_element);
}

function syncCodeScroll(element, destination) {
  let result_element = document.querySelector(destination);
  result_element.scrollTop = element.scrollTop;
  result_element.scrollLeft = element.scrollLeft;
}
</script>

<style>
.hgl-editor, .hgl-highlighting {
    margin: 10px;
    padding: 10px;
    border: 0;
    height: 500px;
    width: 600px;
    position: absolute;
    top: 0;
    left: 0;
    overflow: auto;
    white-space: nowrap;
}
.hgl-editor, .hgl-highlighting, .hgl-highlighting * {
    font-size: 10pt;
    font-family: Consolas, monospace;
    line-height: 15pt;
}
.hgl-editor {
    z-index: 1;
    padding: 1.7em;
    color: transparent;
    background-color: transparent !important;
    caret-color: black;
    resize: none;
}
.hgl-highlighting {
    z-index: 0;
}
</style>