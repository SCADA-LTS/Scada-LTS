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
<%@page	import="com.serotonin.mango.view.component.EnhancedPointLineType"%>
<%@page import="com.serotonin.web.i18n.I18NUtils"%>
<%@page	import="com.serotonin.mango.view.component.EnhancedImageChartType"%>
<%@page import="com.serotonin.web.i18n.LocalizableMessage"%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>
<%@page	import="com.serotonin.mango.view.component.SimpleCompoundComponent"%>

<div id="compoundEditorPopup" style="display: none; left: 0px; top: 0px;" class="windowDiv">
	<div>
	<table cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<table width="100%">
					<tr>
						<td>
							<tag:img png="plugin_edit"
								title="viewEdit.compound.editor" 
								style="display:inline;"
							/>
							<span class="copyTitle" id="compoundComponentName"></span>
						</td>
						<td align="right">
							<tag:img png="save" title="common.save"
								style="display:inline;"
								onclick="compoundEditor.save()" 
							/>&nbsp;
							<tag:img png="cross" title="common.close"
								style="display:inline;"
								onclick="compoundEditor.close()" 
							/>
						</td>
					</tr>
				</table>
				<table>
					<tr>
        				<td class="formLabel"><fmt:message key="viewEdit.position.x"/></td>
        				<td class="formField"><input id="compoundPositionX" type="number" default="0" min="0"/></td>
      				</tr>
      				<tr>
        				<td class="formLabel"><fmt:message key="viewEdit.position.y"/></td>
        				<td class="formField"><input id="compoundPositionY" type="number" default="0" min="0"/></td>
      				</tr>
				</table>
				<table>
					<tr>
						<td class="formLabelRequired">
							<fmt:message key="viewEdit.compound.name"/>
						</td>
						<td class="formField">
							<input id="compoundName" type="text"/>
						</td>
					</tr>
					<tbody id="simpleCompoundAttrs">
						<tr>
							<td class="formLabel">
								<fmt:message key="viewEdit.compound.backgroundColour"/>
							</td>
							<td class="formField">
								<input id="compoundBackgroundColour" type="text"/>
							</td>
						</tr>
					</tbody>
					<tbody id="imageChartAttrs">
						<tr>
							<td class="formLabelRequired">
								<fmt:message key="viewEdit.compound.width"/>
							</td>
							<td class="formField">
								<input id="imageChartWidth" type="text" />
							</td>
						</tr>
						<tr>
							<td class="formLabelRequired">
								<fmt:message key="viewEdit.compound.height" />
							</td>
							<td class="formField">
								<input id="imageChartHeight" type="text" />
							</td>
						</tr>
						<tr>
							<td class="formLabelRequired">
								<fmt:message key="viewEdit.compound.duration"/>
							</td>
							<td class="formField">
								<input type="text" id="imageChartDurationPeriods" class="formShort" />
								<select id="imageChartDurationType">
									<tag:timePeriodOptions sst="false" s="true" min="true" h="true"
										d="true" w="true" mon="true" y="true"/>
								</select>
							</td>
						</tr>
					</tbody>
					<tbody id="enhancedImageChartAttrs">
						<tr>
							<td class="formLabelRequired">
								<fmt:message key="viewEdit.compound.type"/>
							</td>
							<td class="formField">
								<input name="enhancedImageChartType" type="radio" 
									value="<%= EnhancedImageChartType.STATIC.name() %>">
								<fmt:message key="viewEdit.compound.type.static" /></input> 
								<input name="enhancedImageChartType" type="radio"
									value="<%= EnhancedImageChartType.DYNAMIC.name() %>">
								<fmt:message key="viewEdit.compound.type.dynamic" /></input>
							</td>
						</tr>
					</tbody>
					<tbody id="pointLists"></tbody>
				</table>
			</td>
		</tr>
	</table>

	<script type="text/javascript">
	var viewId = mango.longPoll.pollRequest.viewId;
	class CompoundEditor {
		constructor() {
			this.component = null;
        	this.pointList = [];
		}

		open(compId) {
			document.getElementById("compoundEditorPopup").firstElementChild.setAttribute("id", "compound" + compId);
			ViewDwr.getViewComponent(compId, viewId, (comp) => {
                this.component = comp;
                $set("compoundComponentName", comp.displayName);
				$set("compoundPositionX", comp.x);
                $set("compoundPositionY", comp.y);
                
                // Update the point lists
                this.updatePointLists();
                
                // Update the data in the form.
                $set("compoundName", comp.name);
                
                if (comp.defName == "simpleCompound") {
                    $set("compoundBackgroundColour", comp.backgroundColour);
                    show("simpleCompoundAttrs");
                } else {
					hide("simpleCompoundAttrs");
				}
                    
                if (comp.defName === "imageChart" || comp.defName === "enhancedImageChart") {
                    $set("imageChartWidth", comp.width);
                    $set("imageChartHeight", comp.height);
                    $set("imageChartDurationType", comp.durationType);
                    $set("imageChartDurationPeriods", comp.durationPeriods);
                    show("imageChartAttrs");
                } else {
					hide("imageChartAttrs");
				}
                    
                if(comp.defName === "enhancedImageChart"){
                	jQuery("input:radio[name='enhancedImageChartType'][value='" + comp.enhancedImageChartType + "']").prop("checked", true);
                	show("enhancedImageChartAttrs");
                } else {
                	hide("enhancedImageChartAttrs");
                }
                
                show("compoundEditorPopup");
            });
            positionEditor(compId, "compoundEditorPopup");
		}

		close() {
            hide("compoundEditorPopup");
            hideContextualMessages("compoundEditorPopup");
        }

		save() {
            hideContextualMessages("compoundEditorPopup");
			let posX = $get("compoundPositionX");
            let posY = $get("compoundPositionY");
            [posX, posY] = validateComponentPosition(posX, posY);
            updatePointPosition(this.component.id,
              posX, posY, "compoundPositionX", "compoundPositionY"
            );
            
            // Gather the point settings
            var pointChildren = this.getPointChildren();
            var childPointIds = new Array();
            var sel;
            for (var i=0; i<pointChildren.length; i++)
                childPointIds.push({
					key: pointChildren[i].id, 
					value: $get("compoundPointSelect"+ pointChildren[i].id)
				});
			switch(this.component.defName) {
				case 'simpleCompound':
					ViewDwr.saveSimpleCompoundComponent(this.component.id, 
						$get("compoundName"), $get("compoundBackgroundColour"), 
						childPointIds, posX, posY, viewId,
						this.saveCB
					);
					break;
				case 'imageChart':
					ViewDwr.saveImageChartComponent(this.component.id, 
						$get("compoundName"), $get("imageChartWidth"), 
						$get("imageChartHeight"), $get("imageChartDurationType"),
                        $get("imageChartDurationPeriods"), childPointIds, 
						posX, posY, viewId,
						this.saveCB
					);
					break;
				case 'enhancedImageChart':
					let pointsPropsList = this.getEnhancedPointsProperties();
            		ViewDwr.saveEnhancedImageChartComponent(this.component.id, 
						$get("compoundName"), $get("imageChartWidth"), 
						$get("imageChartHeight"), $get("imageChartDurationType"),
                        $get("imageChartDurationPeriods"), 
						jQuery("input:radio[name='enhancedImageChartType']:checked").val(),
                        childPointIds, pointsPropsList, 
						posX, posY, viewId,
						this.saveCB
					);
					break;
				default:
					ViewDwr.saveCompoundComponent(this.component.id, 
						$get("compoundName"), childPointIds,
						posX, posY, viewId,
                        this.saveCB);
			}
        }

		saveCB(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                if (compoundEditor.component.defName == "simpleCompound") {
					$("c"+ compoundEditor.component.id +"Info").style.background = $get("compoundBackgroundColour");
				}
                if (compoundEditor.component.defName == "enhancedImageChart") {
                	dygraphsCharts[compoundEditor.component.id].updateOptions(
						parseInt($get("imageChartWidth")), 
						parseInt($get("imageChartHeight")),
						parseInt($get("imageChartDurationType")), 
						parseInt($get("imageChartDurationPeriods")), 
						jQuery("input:radio[name='enhancedImageChartType']:checked").val(),
                		compoundEditor.getEnhancedPointsProperties(true)
					);
                	dygraphsCharts[compoundEditor.component.id].requestData();
                }
                
                compoundEditor.close();
                MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
            }
        }

		getEnhancedPointsProperties(addPointNameInfo) {
        	var pointsPropsList = new Array();
        	var pointChildren = this.getPointChildren();
        	for (var i=0; i<pointChildren.length; i++) {
        	  if(!isBlank(jQuery("#compoundPointSelect"+ pointChildren[i].id + " option:selected").text())) {	
        	    var color = $get("compoundPointColor" + pointChildren[i].id);
        	
        	    if(!color.startsWith("#")){
        			color = "#" + color;
        		}
        	
        	    var pointProps = {
        			key: pointChildren[i].id,
        			alias: $get("compoundPointName" + pointChildren[i].id),
        			color: color,
        			strokeWidth: $get("compoundPointStrokeWidth" + pointChildren[i].id),
        			lineType: jQuery("input:radio[name='compoundPointLineType" + pointChildren[i].id + "']:checked").val(),
        			showPoints: $get("compoundPointShowPoints" + pointChildren[i].id)
        		};

				if(addPointNameInfo) {
        			pointProps['pointName'] = jQuery("#compoundPointSelect" + pointChildren[i].id + " option:selected").text();
        		}
        		pointsPropsList.push(pointProps);
        	  }
        	}
          return pointsPropsList;
        }
        
        setPointList(pointList) {
            this.pointList = pointList;
        };

		updatePointLists() {
            var pointChildren = this.getPointChildren();
            
            var functions = [
                (data) => { return data.description; },
                (data) => { return '<select id="compoundPointSelect'+ data.id +'"></select>'; }
            ];
            if(this.component.defName === "enhancedImageChart") {
            	functions = [
                    (data) => { return data.description; },
                    (data) => { return '<select id="compoundPointSelect'+ data.id +'"></select></br></br>' + 
                    	'<label for="compoundPointColor'+ data.id +'"><span><fmt:message key="viewEdit.compound.point.color"/></span></label>' +
                    	'<input id="compoundPointColor'+ data.id +'" type="hidden" value="' + data.viewComponent.color + '"/>' +
                    	'<label for="compoundPointStrokeWidth'+ data.id +'"><span><fmt:message key="viewEdit.compound.point.strokeWidth"/></span></label>' + 
                    	'<input id="compoundPointStrokeWidth'+ data.id +'" value="' + data.viewComponent.strokeWidth + '" style="width: 20%;"/>'; },
                    (data) => { return '<label for="compoundPointName'+ data.id +'"><span><fmt:message key="viewEdit.compound.point.alias"/></span></label>' +
                    	'<input id="compoundPointName' + data.id + '" type="text" value="' + defaultIfBlank(data.viewComponent.alias, "") + '"/></br>' +
                    	'<input name="compoundPointLineType'+ data.id +'" type="radio" ' + ((data.viewComponent.lineType === "<%= EnhancedPointLineType.LINE.name() %>" ) ? 'checked="checked"' : '')  +
                   		' value="<%= EnhancedPointLineType.LINE.name() %>"><fmt:message key="viewEdit.compound.point.lineType.line"/></input>' +
          				'<input name="compoundPointLineType'+ data.id +'" type="radio" ' + ((data.viewComponent.lineType === "<%= EnhancedPointLineType.SPLINE.name() %>" ) ? 'checked="checked"' : '')  +
						' value="<%= EnhancedPointLineType.SPLINE.name() %>"><fmt:message key="viewEdit.compound.point.lineType.spline"/></input>' + 
						'<input id="compoundPointShowPoints' + data.id + '" type="checkbox" ' + (data.viewComponent.showPoints ? 'checked':'') + '><fmt:message key="viewEdit.compound.point.showPoints"/></input>'; }
                ];
            }
            
            // Create the select controls
            dwr.util.removeAllRows("pointLists");
            dwr.util.addRows("pointLists", pointChildren, functions, {
                cellCreator: (options) => {
                    var td = document.createElement("td");
                    if (options.cellNum % 2 == 0) {
                        if (
							this.component.defName == "simpleCompound" &&
                            options.rowData.id == "<%= SimpleCompoundComponent.LEAD_POINT %>"
						)
							td.className = "formLabelRequired";
						else {
							td.className = "formLabel";
						}						
					} else if (options.cellNum % 2 == 1)
						td.className = "formField";
						return td;
					}
			});

				// Add options to the controls.
			var sel, p;
			for (var i = 0; i < pointChildren.length; i++) {
				sel = $("compoundPointSelect" + pointChildren[i].id);
				var pointChildId = "compoundPointSelect"+ pointChildren[i].id;
				sel = $(pointChildId);
				sel.options[0] = new Option("", 0);
				for (p = 0; p < this.pointList.length; p++) {
					if (contains(pointChildren[i].dataTypes, this.pointList[p].dataType)) {
						sel.options[sel.options.length] = new Option(
							settingsEditor.pointList[p].name,
							settingsEditor.pointList[p].id
						);
					}
				}

				// Set the control default value.
				$set(sel, pointChildren[i].viewComponent.dataPointId);
				jQuery("#" + pointChildId).chosen({
					allow_single_deselect: true,
					placeholder_text_single: " ",
					search_contains: true,
					width: "400px"
				});
				if(this.component.defName === "enhancedImageChart") {	
					jQuery("#compoundPointColor" + pointChildren[i].id).jPicker({
						images: {
							clientPath: 'resources/jQuery/plugins/jpicker/images/',
						},
						window: {
							title: "",
							position: {
								x: 'right',
								y: 'center'
							}
						}
					});
					jQuery("#compoundPointStrokeWidth" + pointChildren[i].id).spinner({
						step: 1.0,
						numberFormat: "n",
						max: 10,
						min: 0
					}).width("100%").parent().width("20%");
				}
			}
		}

		getPointChildren() {
			var pointChildren = new Array();
			for (var i = 0; i < this.component.childComponents.length; i++) {
				if (this.component.childComponents[i].viewComponent.pointComponent) {
					pointChildren.push(this.component.childComponents[i]);
				}
			}
			return pointChildren;
		}
	}
	
	var compoundEditor = new CompoundEditor();
	</script>
	</div>
</div>