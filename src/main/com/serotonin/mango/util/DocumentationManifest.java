/*
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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.serotonin.mango.Common;
import com.serotonin.util.XmlUtils;

/**
 * @author Matthew Lohbihler
 */
public class DocumentationManifest {
    private final List<DocumentationItem> items = new ArrayList<DocumentationItem>();

    public DocumentationManifest() throws Exception {
        // Read the documentation manifest file.
        XmlUtils utils = new XmlUtils();

        Document document = utils.parse(new File(Common.getDocPath() + "manifest.xml"));

        Element root = document.getDocumentElement();
        for (Element item : utils.getElementsByTagName(root, "item")) {
            DocumentationItem di = new DocumentationItem(item.getAttribute("id"));

            for (Element relation : utils.getElementsByTagName(item, "relation"))
                di.addRelated(relation.getAttribute("id"));

            items.add(di);
        }
    }

    public DocumentationItem getItem(String id) {
        for (DocumentationItem di : items) {
            if (id.equals(di.getId()))
                return di;
        }
        return null;
    }
}
