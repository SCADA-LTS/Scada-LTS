<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Dojo Tree Widget Test (dynamic node creation)</title>

<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: true };
</script>
<script type="text/javascript" src="../../../dojo.js"></script>
<script type="text/javascript">
	delete dojo["profile"];

	dojo.require("dojo.widget.*");
	dojo.require("dojo.widget.TreeV3");
	dojo.require("dojo.widget.TreeNodeV3");
	dojo.require("dojo.widget.TreeBasicControllerV3");
	dojo.require("dojo.profile");
	dojo.hostenv.writeIncludes();
</script>
<script type="text/javascript">

	start = new Date();

	dojo.addOnLoad(function() {
		dojo.debug(new Date() - start);
	});
	
</script>

</head>
<body>
	
<div dojoType="TreeV3">
<?php
$cnt = 0;
$depth = 0;
		function addChildren($title) {
			global $depth, $cnt;
			$depth++;
			
			for ($i=1; $i<=5; $i++) {
				$t = $title.'.'.$i;
				$cnt++;
				echo "<div dojoType='TreeNodeV3' title='$t' widgetId='$t'>\n";
								
				if ($depth<3 && $cnt<=100) {
					addChildren($t);
				}
				
				echo "</div>\n";
			}
			
			$depth--;			
		}
		
		addChildren("node");

?>		
</div>

Total nodes: <?php echo $cnt;?>
</body>
</html>



	
