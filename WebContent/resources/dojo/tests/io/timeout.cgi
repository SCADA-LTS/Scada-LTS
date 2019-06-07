#!/usr/bin/python

import cgi
import cgitb; cgitb.enable()
import os, sys, time
import string

dbg = []

def debug(dbgstr):
	dbg.append("DBG: "+str(dbgstr))

time.sleep(5);

print """Content-Type: text/plain
Expires: 0


Done sleeping for 5 seconds.
"""
