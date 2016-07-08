<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
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
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<div id="graphicRendererEditorPopup" style="display:none;left:0px;top:0px;" class="windowDiv">
  <table cellpadding="0" cellspacing="0"><tr><td>
    <table width="100%">
      <tr>
        <td>
          <tag:img png="graphic" title="viewEdit.graphic.editor" style="display:inline;"/>
          <span class="copyTitle" id="graphicsComponentName"></span>
        </td>
        <td align="right">
          <tag:img png="save" onclick="graphicRendererEditor.save()" title="common.save" style="display:inline;"/>&nbsp;
          <tag:img png="cross" onclick="graphicRendererEditor.close()" title="common.close" style="display:inline;"/>
        </td>
      </tr>
    </table>
    
    <table>
      <tbody id="graphicRenderer_analogGraphic" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.min"/></td>
          <td class="formField"><input id="graphicRendererAnalogMin" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.max"/></td>
          <td class="formField"><input id="graphicRendererAnalogMax" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.displayText"/></td>
          <td class="formField"><input id="graphicRendererAnalogDisplayText" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.imageSet"/></td>
          <td>
            <select id="graphicRendererAnalogImageSet" onchange="graphicRendererEditor.updateSampleImageSet(this)">
              <option value=""></option>
              <c:forEach items="${imageSets}" var="imageSet">
                <option value="${imageSet.id}">${imageSet.name} (${imageSet.imageCount} <fmt:message key="viewEdit.graphic.images"/>)</option>
              </c:forEach>
            </select><br/>
            <img id="graphicRendererAnalogImageSetSample"/>
          </td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_binaryGraphic" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.displayText"/></td>
          <td class="formField"><input id="graphicRendererBinaryDisplayText" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.imageSet"/></td>
          <td>
            <select id="graphicRendererBinaryImageSet" onchange="graphicRendererEditor.displayBinaryImages($get(this));">
              <option value=""></option>
              <c:forEach items="${imageSets}" var="imageSet">
                <option value="${imageSet.id}">${imageSet.name} (${imageSet.imageCount} <fmt:message key="viewEdit.graphic.images"/>)</option>
              </c:forEach>
            </select>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.zero"/></td>
          <td class="formField">
            <div id="graphicRendererBinaryImageSetZero" style="width:200px; overflow:auto; white-space:nowrap;"></div>
            <input type="hidden" id="graphicRendererBinaryImageSetZeroMsg"/>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.one"/></td>
          <td class="formField">
            <div id="graphicRendererBinaryImageSetOne" style="width:200px; overflow:auto; white-space:nowrap;"></div>
            <input type="hidden" id="graphicRendererBinaryImageSetOneMsg"/>
          </td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_dynamicGraphic" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.min"/></td>
          <td class="formField"><input id="graphicRendererDynamicMin" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.max"/></td>
          <td class="formField"><input id="graphicRendererDynamicMax" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.displayText"/></td>
          <td class="formField"><input id="graphicRendererDynamicDisplayText" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.dynamicImage"/></td>
          <td>
            <select id="graphicRendererDynamicImage" onchange="graphicRendererEditor.updateSampleDynamicImage(this)">
              <option value=""></option>
              <c:forEach items="${dynamicImages}" var="dynamicImage">
                <option value="${dynamicImage.id}">${dynamicImage.name}</option>
              </c:forEach>
            </select><br/>
            <img id="graphicRendererDynamicImageSample"/>
          </td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_multistateGraphic" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.displayText"/></td>
          <td class="formField"><input id="graphicRendererMultistateDisplayText" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.imageSet"/></td>
          <td>
            <select id="graphicRendererMultistateImageSet" onchange="graphicRendererEditor.displayMultistateImages($get(this));">
              <option value=""></option>
              <c:forEach items="${imageSets}" var="imageSet">
                <option value="${imageSet.id}">${imageSet.name} (${imageSet.imageCount} <fmt:message key="viewEdit.graphic.images"/>)</option>
              </c:forEach>
            </select>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.state"/></td>
          <td class="formField" id="graphicRendererMultistateImageSetList">
          </td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_script" style="display:none;">
        <tr>
          <td colspan="2">
            <span class="formLabelRequired"><fmt:message key="viewEdit.graphic.script"/></span><br/>
            <span class="formField"><textarea id="graphicRendererScriptScript" rows="10" cols="50"></textarea></span>
          </td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_simpleImage" style="display:none;">
        <tr><td class="formLabelRequired"><fmt:message key="viewEdit.graphic.noConfig"/></td></tr>
      </tbody>
      
      <tbody id="graphicRenderer_simple" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.displayPointName"/></td>
          <td class="formField"><input id="graphicRendererSimpleDisplayPointName" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.styleAttribute"/></td>
          <td class="formField"><input id="graphicRendererSimpleStyleAttribute" type="text"/></td>
        </tr>
      </tbody>
      
      <tbody id="graphicRenderer_thumbnailImage" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.scale"/></td>
          <td class="formField"><input id="graphicRendererThumbnailScalePercent" type="text"/></td>
        </tr>
      </tbody>
      <tbody id="graphicRenderer_button" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.whenOnLabel"/></td>
          <td class="formField"><input id="graphicRendererButtonWhenOnLabel" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.whenOffLabel"/></td>
          <td class="formField"><input id="graphicRendererButtonWhenOffLabel" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.height"/></td>
          <td class="formField"><input id="graphicRendererButtonHeight" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.width"/></td>
          <td class="formField"><input id="graphicRendererButtonWidth" type="text"/></td>
        </tr>
        
      </tbody>
      
    </table>
  </td></tr></table>
  
  <script type="text/javascript">
    // Script requires
    //  - Drag and Drop library for locating objects and positioning the window.
    //  - DWR utils for using $() prototype.
    //  - common.js
    function GraphicRendererEditor() {
        this.currentImageSetId = null;
        this.zeroImage = -1;
        this.oneImage = -1;
        this.componentId = null;
        this.typeName = null;
        this.imageSets = <sst:convert obj="${imageSets}"/>;
        this.dynamicImages = <sst:convert obj="${dynamicImages}"/>;
        
        this.open = function(compId) {
            graphicRendererEditor.currentImageSetId = null;
            graphicRendererEditor.zeroImage = -1;
            graphicRendererEditor.oneImage = -1;
        
            graphicRendererEditor.componentId = compId;
            
            // Set the renderers for the data type of this point view.
            ViewDwr.getViewComponent(compId, graphicRendererEditor.setViewComponent);
            
            positionEditor(compId, "graphicRendererEditorPopup");
        };
        
        this.setViewComponent = function(comp) {
            graphicRendererEditor.typeName = comp.typeName;
            $set("graphicsComponentName", comp.displayName);
            
            // Clear the form data.
            $set("graphicRendererBinaryDisplayText");
            $set("graphicRendererBinaryImageSet");
            $set("graphicRendererMultistateDisplayText");
            $set("graphicRendererMultistateImageSet");
            $set("graphicRendererAnalogMin");
            $set("graphicRendererAnalogMax");
            $set("graphicRendererAnalogDisplayText");
            $set("graphicRendererAnalogImageSet");
            $set("graphicRendererThumbnailScalePercent");
            $set("graphicRendererDynamicMin");
            $set("graphicRendererDynamicMax");
            $set("graphicRendererDynamicDisplayText");
            $set("graphicRendererDynamicImage");
            $set("graphicRendererScriptScript");
            $set("graphicRendererSimpleDisplayPointName");
            $set("graphicRendererSimpleStyleAttribute");
            graphicRendererEditor.displayBinaryImages(null);
            graphicRendererEditor.displayMultistateImages(null);
            // Update the data in the form.
            if (comp.typeName == "analogGraphic") {
                $set("graphicRendererAnalogMin", comp.min);
                $set("graphicRendererAnalogMax", comp.max);
                $set("graphicRendererAnalogDisplayText", comp.displayText);
                $set("graphicRendererAnalogImageSet", comp.imageSetId);
                graphicRendererEditor.updateSampleImageSet($("graphicRendererAnalogImageSet"));
            }
            else if (comp.typeName == "binaryGraphic") {
                $set("graphicRendererBinaryDisplayText", comp.displayText);
                $set("graphicRendererBinaryImageSet", comp.imageSetId);
                graphicRendererEditor.displayBinaryImages(comp.imageSetId);
                graphicRendererEditor.setZeroImage(comp.zeroImage);
                graphicRendererEditor.setOneImage(comp.oneImage);
            }
            else if (comp.typeName == "dynamicGraphic") {
                $set("graphicRendererDynamicMin", comp.min);
                $set("graphicRendererDynamicMax", comp.max);
                $set("graphicRendererDynamicDisplayText", comp.displayText);
                $set("graphicRendererDynamicImage", comp.dynamicImageId);
            }
            else if (comp.typeName == "multistateGraphic") {
                $set("graphicRendererMultistateDisplayText", comp.displayText);
                $set("graphicRendererMultistateImageSet", comp.imageSetId);
                graphicRendererEditor.displayMultistateImages(comp.imageSetId);
                var imageStates = comp.imageStateList;
                
                var i=0;
                while ($("graphicRendererMultistateState"+ i))
                    $set("graphicRendererMultistateState"+ (i++), "");
                for (var i=0; i<imageStates.length; i++)
                    $set("graphicRendererMultistateState"+ imageStates[i].key, imageStates[i].value);
                $set("graphicRendererMultistateDefault", comp.defaultImage);
            }
            else if (comp.typeName == "script")
                $set("graphicRendererScriptScript", comp.script);
            else if (comp.typeName == "simple") {
                $set("graphicRendererSimpleDisplayPointName", comp.displayPointName);
                $set("graphicRendererSimpleStyleAttribute", comp.styleAttribute);
            } else if (comp.typeName == "thumbnailImage")
                $set("graphicRendererThumbnailScalePercent", comp.scalePercent);
            else if(comp.typeName == "button") {
            	$set("graphicRendererButtonWhenOnLabel", comp.whenOnLabel);
            	$set("graphicRendererButtonWhenOffLabel", comp.whenOffLabel);
            	$set("graphicRendererButtonWidth", comp.width);
            	$set("graphicRendererButtonHeight", comp.height);
            }
            show("graphicRenderer_"+ comp.typeName);
            show("graphicRendererEditorPopup");
        };
    
        this.close = function() {
            hide("graphicRendererEditorPopup");
            hideContextualMessages("graphicRendererEditorPopup");
            if (graphicRendererEditor.typeName)
                hide("graphicRenderer_"+ graphicRendererEditor.typeName);
        };
        
        this.save = function() {
            hideContextualMessages("graphicRendererEditorPopup");
            if (graphicRendererEditor.typeName == "analogGraphic")
                ViewDwr.saveAnalogGraphicComponent(graphicRendererEditor.componentId, $get("graphicRendererAnalogMin"),
                        $get("graphicRendererAnalogMax"), $get("graphicRendererAnalogDisplayText"),
                        $get("graphicRendererAnalogImageSet"), graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "binaryGraphic")
                ViewDwr.saveBinaryGraphicComponent(graphicRendererEditor.componentId, graphicRendererEditor.zeroImage,
                        graphicRendererEditor.oneImage, $get("graphicRendererBinaryDisplayText"),
                        $get("graphicRendererBinaryImageSet"), graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "dynamicGraphic")
                ViewDwr.saveDynamicGraphicComponent(graphicRendererEditor.componentId,
                        $get("graphicRendererDynamicMin"), $get("graphicRendererDynamicMax"),
                        $get("graphicRendererDynamicDisplayText"), $get("graphicRendererDynamicImage"),
                        graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "multistateGraphic") {
                var imageSet = $get("graphicRendererMultistateImageSet");
                var i = 0, j;
                var imageStates = new Array();
                if (imageSet) {
                    while ($("graphicRendererMultistateState"+ i)) {
                        text = $get("graphicRendererMultistateState"+ i);
                        if (text.trim() != "")
                            imageStates[imageStates.length] = {key: i, value: text};
                        i++;
                    }
                }
                
                ViewDwr.saveMultistateGraphicComponent(graphicRendererEditor.componentId, imageStates,
                        $get("graphicRendererMultistateDefault"), $get("graphicRendererMultistateDisplayText"),
                        imageSet, graphicRendererEditor.saveCB);
            }
            else if (graphicRendererEditor.typeName == "script")
                ViewDwr.saveScriptComponent(graphicRendererEditor.componentId, $get("graphicRendererScriptScript"),
                        graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "simple")
                ViewDwr.saveSimplePointComponent(graphicRendererEditor.componentId,
                        $get("graphicRendererSimpleDisplayPointName"), 
                        $get("graphicRendererSimpleStyleAttribute"), graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "thumbnailImage")
                ViewDwr.saveThumbnailComponent(graphicRendererEditor.componentId,
                        $get("graphicRendererThumbnailScalePercent"), graphicRendererEditor.saveCB);
            else if (graphicRendererEditor.typeName == "button") {
            	ViewDwr.saveButtonComponent(graphicRendererEditor.componentId,
                        $get("graphicRendererButtonWhenOnLabel"), $get("graphicRendererButtonWhenOffLabel"),$get("graphicRendererButtonWidth"),
                        $get("graphicRendererButtonHeight"), graphicRendererEditor.saveCB);
            }
            else
                graphicRendererEditor.close();
        };
        
        this.saveCB = function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                graphicRendererEditor.close();
                MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
            }
        };
        
        this.displayBinaryImages = function(imageSetId) {
            graphicRendererEditor.setZeroImage(-1);
            graphicRendererEditor.setOneImage(-1);
            graphicRendererEditor.currentImageSetId = imageSetId;
            
            var imageSet = graphicRendererEditor.findImageSet(imageSetId);
            if (imageSet) {
                $set("graphicRendererBinaryImageSetZero", graphicRendererEditor.createImageList(imageSet, "Zero"));
                $set("graphicRendererBinaryImageSetOne", graphicRendererEditor.createImageList(imageSet, "One"));
            }
            else {
                $set("graphicRendererBinaryImageSetZero");
                $set("graphicRendererBinaryImageSetOne");
            }
        };
        
        this.createImageList = function(imageSet, type) {
            var html = "";
            for (var i=0; i<imageSet.imageFilenames.length; i++) {
                html += "<a\
                  href='javascript:void(0)' onclick='graphicRendererEditor.set"+ type +"Image("+ i +")'><img\
                    id='graphicRendererBinaryImageSet"+ type + i +"' src='"+ imageSet.imageFilenames[i] +"'\
                    style='display:inline;' border='0'/></a>&nbsp;";
            }
            return html;
        };
        
        this.displayMultistateImages = function(imageSetId) {
            var imageSet = graphicRendererEditor.findImageSet(imageSetId);
            var graphicImg = '<tag:img png="graphic" title="viewEdit.graphic.imageSample" style="display:inline;"/>';
            if (imageSet) {
                var html = "\
                    <table>\
                      <tr>\
                        <th><fmt:message key="viewEdit.graphic.image"/></th>\
                        <th><fmt:message key="viewEdit.graphic.stateList"/></th>\
                        <th><fmt:message key="viewEdit.graphic.default"/></th>\
                      </tr>";

                for (var i=0; i<imageSet.imageFilenames.length; i++) {
                    html += "\
                        <tr>\
                          <td align='center'>\
                            <div onmouseover='show(\"graphicRendererMultistateImageSet"+ i +"\");'\
                                    onmouseout='hide(\"graphicRendererMultistateImageSet"+ i +"\");'\
                                    style='position:relative;display:inline;'>"+ graphicImg +"<div \
                                    id='graphicRendererMultistateImageSet"+ i +"' class='imageDiv'\
                                    style='display:none;top:18px;'><img src='"+ imageSet.imageFilenames[i] +"'/></div></div>\
                          </td>\
                          <td><input id='graphicRendererMultistateState"+ i +"' type='text' class='formShort'/></td>\
                          <td align='center'><input type='radio' name='graphicRendererMultistateDefault' value='"+ i +"'/></td>\
                        </tr>";
                }
                
                html += "</table>";
                
                $set("graphicRendererMultistateImageSetList", html);
            }
            else
                $set("graphicRendererMultistateImageSetList");
        };
        
        this.setZeroImage = function(imageId) {
            var image;
            if (graphicRendererEditor.zeroImage != -1 && graphicRendererEditor.currentImageSetId)
                $("graphicRendererBinaryImageSetZero"+ graphicRendererEditor.zeroImage).border = "0";
            graphicRendererEditor.zeroImage = imageId;
            if (graphicRendererEditor.zeroImage != -1 && graphicRendererEditor.currentImageSetId)
                $("graphicRendererBinaryImageSetZero"+ graphicRendererEditor.zeroImage).border = "2";
        };
        this.setOneImage = function(imageId) {
            var image;
            if (graphicRendererEditor.oneImage != -1 && graphicRendererEditor.currentImageSetId)
                $("graphicRendererBinaryImageSetOne"+ graphicRendererEditor.oneImage).border = "0";
            graphicRendererEditor.oneImage = imageId;
            if (graphicRendererEditor.oneImage != -1 && graphicRendererEditor.currentImageSetId)
                $("graphicRendererBinaryImageSetOne"+ graphicRendererEditor.oneImage).border = "2";
        };
        
        this.updateSampleImageSet = function(selectComp) {
            var img = $(selectComp.id +"Sample");
            var imageSet = graphicRendererEditor.findImageSet($get(selectComp));
            if (imageSet) {
                img.src = imageSet.imageFilenames[0];
                show(img);
            }
            else
                hide(img);
        };
        
        this.updateSampleDynamicImage = function(selectComp) {
            var img = $(selectComp.id +"Sample");
            var dynamicImage = graphicRendererEditor.findDynamicImage($get(selectComp));
            if (dynamicImage) {
                img.src = dynamicImage.imageFilename;
                show(img);
            }
            else
                hide(img);
        };
        
        this.findImageSet = function(id) {
            for (var i=0; i<graphicRendererEditor.imageSets.length; i++) {
                if (graphicRendererEditor.imageSets[i].id == id)
                    return graphicRendererEditor.imageSets[i];
            }
            return null;
        };
        
        this.findDynamicImage = function(id) {
            for (var i=0; i<graphicRendererEditor.dynamicImages.length; i++) {
                if (graphicRendererEditor.dynamicImages[i].id == id)
                    return graphicRendererEditor.dynamicImages[i];
            }
            return null;
        };
    }
    var graphicRendererEditor = new GraphicRendererEditor();
  </script>
</div>