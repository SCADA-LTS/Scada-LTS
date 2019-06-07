<?php

// Request encoding/decoding skipped to make things simple

// user should see progress indicator
sleep(3);


extract($_REQUEST);

if ($action == 'getChildren') {
?>
([{title:"load1",isFolder:true,objectId:"myobj"},{title:"load2",isFolder:true,objectId:"myobj"},{title:"singleChild",children:[ {title:"leaf"}  ]}])
<?
	return;
}

if ($action == 'createChild') {
?>
dojo.debug("server gave new title");({title:"action successful: createChild",objectId:666})
<?
	return;
}

if ($action == 'editLabelSave') {
?>
dojo.debug("server gave new title");({title:"action successful: editLabelSave"})
<?
return;
}

?>
({})



