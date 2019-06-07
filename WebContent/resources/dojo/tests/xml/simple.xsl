<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:param name="focus-id" select="/*/@index_page"/>
  
  <xsl:key name="site-ids" match="*" use="@id"/>
  
  <xsl:template match="/">
    <div>
      <xsl:copy-of select="document('hello-world.xml')"/>
      <p>
        <xsl:value-of select="key('site-ids', $focus-id)/@label"/>
      </p>
    </div>
    
  </xsl:template>
  
</xsl:stylesheet>
