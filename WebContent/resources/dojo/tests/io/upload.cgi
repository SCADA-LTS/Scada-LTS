#!/usr/bin/python

# FROM: http://aspn.activestate.com/ASPN/Cookbook/Python/Recipe/273844

import cgi
import cgitb; cgitb.enable()
import os, sys
import string

UPLOAD_DIR = "/tmp/upload/"
form = cgi.FieldStorage()

dbg = []

def debug(dbgstr):
	dbg.append("DBG: "+str(dbgstr))

def save_uploaded_file(form_field, upload_dir):
	global form
	if not form.has_key(form_field): 
		debug("didn't find it! (1)")
		return
	fileitem = form[form_field]
	if not fileitem.file: 
		debug(form.getvalue(form_field, ""))
		debug(fileitem.__dict__)
		debug("didn't find it! (2)")
		return
	fout = file(os.path.join(upload_dir, fileitem.filename), 'wb')
	while 1:
		chunk = fileitem.file.read(100000)
		if not chunk: break
		fout.write (chunk)
	fout.close()

retval = "false;";
fileFields = ""
if form.has_key("fileFields"):
	fval = str(form.getvalue("fileFields", ""))
	fileFields = fval.split(",")
	debug(str(len(fileFields)))
	for field in fileFields:
		debug("field: "+field)
		save_uploaded_file(str(field).strip(), UPLOAD_DIR)
	retval = "true;";

print """Content-Type: text/html


<html>
	<head>
	</head>
	<body>
	    <textarea style="width: 100%%; height: 100px;">%s</textarea>
		<br>
	    <textarea style="width: 100%%; height: 100px;">%s</textarea>
	</body>
</html>
""" % (retval, string.join(dbg, "\n"))
