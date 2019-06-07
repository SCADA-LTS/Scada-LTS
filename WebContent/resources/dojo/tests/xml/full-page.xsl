<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:s="http://livestoryboard.com/schemas/lsb" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="s">
  
  <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" encoding="UTF-8" indent="yes"/>
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Test Window</title>
      </head>
      <body>
        
        <h1>Test Window</h1>
        
        <ol>
          <xsl:apply-templates/>
        </ol>
        
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="s:site | s:folder | s:topic">
    
    <li>
      <p>
        <xsl:value-of select="@label"/>
      </p>
      <ol>
        <xsl:apply-templates/>
      </ol>
    </li>
    
  </xsl:template>
  
  <xsl:template match="s:page | s:content">
    
    <li>
      <p>
        <xsl:value-of select="@label"/>
      </p>
    </li>
    
  </xsl:template>
  
</xsl:stylesheet>
