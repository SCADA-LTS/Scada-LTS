#!/usr/bin/env python

import cgi
import cgitb; cgitb.enable()
import os, sys

def ctype():
    print """Content-Type: text/html

"""

def head():
    print """
<html>
    <head><title>Upload test.</title></head>
    <style>
        .value  { background-color: #FFFFC0; }
	.hilite { background-color: #FFC0C0; }
    </style>
    <body>
"""

def tail():
    print """
    </body>
</html>
"""

form = cgi.FieldStorage()

def print_env(k, v, hilite=False):
    if hilite:
        print '<div><tt><strong class="hilite">%s</strong> = <span class="value">%s</span></tt></div>' % (k, v)
    else:
        print '<div><tt><strong>%s</strong> = <span class="value">%s</span></tt></div>' % (k, v)

def print_file(k, v):
    print '<div><tt><strong>%s (%s)</strong> (File: %s):</div><pre class="value">%s</pre>' % (k, v.filename, v.type, v.value)

def print_fs(k, v):
    print '<div><tt><strong>%s</strong> (FieldStorage: %s) = <span class="value">%s</span></tt></div>' % (k, v.type, v.value)

def print_unk(k, v):
    print '<div><tt><strong>%s</strong> (Unknown: %s) = <span class="value">%s</span></tt></div>' % (k, v.type, v.value)

def print_mfs(k, v):
    print '<div><tt><strong>%s</strong> (MiniFieldStorage: %s) = <span class="value">%s</span></tt></div>' % (k, v.type, v.value)

def env():
    print '<h1>Environment</h1>'
    for k, v in os.environ.items():
        print_env(k, v, hilite = (k == 'REQUEST_METHOD') or (k == 'CONTENT_TYPE'))

def act():
    print '<h1>Form Fields</h1>'
    for k in form:
        v = form[k]
	if v.file and v.filename:
	    print_file(k, v)
	elif isinstance(v, cgi.FieldStorage):
	    print_fs(k, v)
	elif isinstance(v, cgi.MiniFieldStorage):
	    print_mfs(k, v)
	else:
	    print_unk(k, v)

if (__name__ == "__main__"):
    ctype()
    if not 'inline' in form or form['inline'].value != 'yes':
        head()
    act()
    #env()
    if not 'inline' in form or form['inline'].value != 'yes':
        tail()




