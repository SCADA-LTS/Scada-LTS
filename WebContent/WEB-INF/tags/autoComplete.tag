<%--
    ScadaBR - Automação para todos - http://www.scadabr.com.br
    Copyright (C) 2012-2014 MCA Desenvolvimento de Sistemas Ltda.
    @author Diego R. Ferreira
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.
    --%>
    <%--
    AutoComplete Tag - An HTMLInputElement with 
    Usage: Define a tag in html as such:
    <tag:autoComplete id="settingsPointInput" onclickcallback="onClickTag"/>
    
    Initiate the autocomplete tag:
    autocomplete.init(ID,LIST, SELECTED, ONCLICKCALLBACK);
    where:
    ID is the input id
    LIST is the list of elements - name is the main column
    SELECTED is the current item
    ONCLICKCALLBACK is the onclick callback, taking selected.name as arg  
     --%>
    
    
<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@tag body-content="empty"%>
<%@attribute name="id" rtexprvalue="true" required="true"%>
<%@attribute name="onclickcallback" rtexprvalue="true" required="true"%>
<%@attribute name="selected" rtexprvalue="true"%>
<input type=text autocomplete="off"  
	<c:if test="${!empty id}"> id="${id}"</c:if>/>

<div id="${id}Popup" class="windowDiv selectDiv">
	<table id="${id}PopupTable"></table>
</div>

<script type="text/javascript">
function AutoComplete() {
  this.id = "";
  this.list = [];
  this.selected = "";
  this.onclickCallback = "";

  this.init = function (id, list, selected, onclickcallback) {
    this.id = id;
    this.list = list;
    this.selected = selected;
    this.onclickCallback = onclickcallback;
    $set(this.id, this.selected);
    $(this.id).onkeyup = createHandler();
    hide(this.id + 'Popup');
    console.log('autocomplete.init: ' + this.id);
    for (var i=0;i<list.length;i++)
        console.log('autocomplete.list[' + i + ']: ' + this.list[i].name);
    console.log('autocomplete.onclickcallback: ' + this.onclickCallback);
    console.log('$(this.id).onkeyup: ' + $(this.id).onkeyup);
  };

  this.inputChanged = function () {
    console.log('autocomplete.inputChanged: ' + this.id);
    var items = getElements(this.list, $get(this.id),"name");
    if (!items || !items.length) {
      console.log('No points found!');
    } else {
      var itemsTable = $(this.id+"PopupTable");
      console.log('autocomplete.inputChanged - Peguei tabela:' + this.id + "PopupTable");
    }
    
    // Clean table
    while(itemsTable.hasChildNodes()){
      itemsTable.removeChild(itemsTable.firstChild);
      console.log('autocomplete.inputChanged - limpei tabela:' + this.id + "PopupTable");
    }

    // Populate Table
    for(var i=0; i<items.length; i++){
      var currentRow = itemsTable.insertRow(i);
      currentRow.className="selectItem";
      var currentCell = currentRow.insertCell(0);
      currentCell.innerHTML="<div onclick='autocomplete.clickHandler(\"" + items[i].name + "\");" + this.onclickCallback + "(\"" + items[i].name + "\")'>" + items[i].name + "</div>";
      console.log('autocomplete.inputChanged - Peguei tabela:' + this.id + "PopupTable");
      
      if(i>15)
        break;
 	}

    var list = $(this.id + "Popup");
    list.style.position = "absolute";
    list.style.display = "block";
    console.log('autocomplete.inputChanged - Show(popup): ' + list.style.display);

  };
  
  this.clickHandler = function(name) {
	  $set(this.id, name)
	  hide(this.id + "Popup");
  }
}

function createHandler(){  
    return function() {  
        autocomplete.inputChanged(); 
    }  
}

var autocomplete = new AutoComplete();
</script>
