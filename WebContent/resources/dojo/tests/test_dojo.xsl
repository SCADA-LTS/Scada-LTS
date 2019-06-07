<?xml version="1.0"?>
	<xsl:stylesheet  
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html" omit-xml-declaration="yes" 
	doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" 
	doctype-system="http://www.w3.org/TR/html4/loose.dtd" encoding="ISO-8859-1"/>

	<xsl:template match="index">
		<html>
			<head>
				<!--
				pointing at the dojo.js directly only works if it's from a build
				<script src="../dojo.js" type="text/javascript"></script>
				-->
				<!--
				manually including the bootstraps works fine
				-->
				<script src="../src/bootstrap1.js" type="text/javascript"></script>
				<script src="../src/hostenv_browser.js" type="text/javascript"></script>
				<script src="../src/bootstrap2.js" type="text/javascript"></script>
			</head>
			<body>
				this is content
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
