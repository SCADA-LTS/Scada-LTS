<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:s="http://livestoryboard.com/schemas/lsb" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="s">
  <!--  xmlns="http://www.w3.org/1999/xhtml" -->

  <xsl:param name="debug" select="'false'"/>
  <xsl:param name="focus-id" select="/*/@index_page"/>

  <xsl:output encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="yes"/>

  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="error">
        <p class="error">
          <xsl:value-of select="error"/>
        </p>
      </xsl:when>
      <xsl:otherwise>
        <div 
          dojoType="TreeRPCController" 
          RPCUrl="local" 
          widgetId="treeController" 
          id="treeController" 
          DNDController="create">
          <xsl:text> </xsl:text>
        </div>
        <div 
          dojoType="TreeSelector" 
          widgetId="treeSelector" 
          id="treeSelector">
          <xsl:text> </xsl:text>
        </div>
        <div 
          controller="treeController" 
          dojoType="Tree" 
          selector="treeSelector" 
          widgetId="navTree"
          id="navTree">
          <xsl:apply-templates/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="s:site">
    <div id="{@id}" title="{@label}" actionsDisabled="remove" dojoType="TreeNode" isFolder="true" widgetId="{@id}">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="s:folder | s:topic">
    
    <div id="{@id}" title="{@label}" dojoType="TreeNode" isFolder="true" widgetId="{@id}">
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="s:page | s:content">
    <div id="{@id}" title="{@label}" dojoType="TreeNode" widgetId="{@id}">
      <xsl:text> </xsl:text>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
