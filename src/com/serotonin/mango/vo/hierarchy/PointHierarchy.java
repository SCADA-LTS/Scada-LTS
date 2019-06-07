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
package com.serotonin.mango.vo.hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.IntValuePair;

/**
 * @author Matthew Lohbihler
 * 
 */
public class PointHierarchy {
    private final PointFolder root;

    public PointHierarchy() {
        root = new PointFolder(0, "Root");
    }

    public PointHierarchy(PointFolder root) {
        this.root = root;
    }

    public PointHierarchy copyFoldersOnly() {
        PointHierarchy copy = new PointHierarchy();
        copy.root.copyFoldersFrom(root);
        return copy;
    }

    public void addPointFolder(PointFolder f, int parentId) {
        boolean added = addPointFolder(f, parentId, root);
        if (!added)
            throw new ShouldNeverHappenException("Could not find point folder " + parentId + " in which to add folder "
                    + f.getId());
    }

    private static boolean addPointFolder(PointFolder f, int parentId, PointFolder parent) {
        if (parent.getId() == parentId) {
            parent.addSubfolder(f);
            return true;
        }

        for (PointFolder child : parent.getSubfolders()) {
            if (addPointFolder(f, parentId, child))
                return true;
        }

        return false;
    }

    public void addDataPoint(int id, int folderId, String name) {
        IntValuePair point = new IntValuePair(id, name);
        boolean added = addDataPoint(point, folderId, root);
        if (!added)
            root.addDataPoint(point);
    }

    private static boolean addDataPoint(IntValuePair p, int folderId, PointFolder parent) {
        if (parent.getId() == folderId) {
            parent.addDataPoint(p);
            return true;
        }

        for (PointFolder child : parent.getSubfolders()) {
            if (addDataPoint(p, folderId, child))
                return true;
        }

        return false;
    }

    public PointFolder getRoot() {
        return root;
    }

    public List<String> getPath(int pointId) {
        List<PointFolder> path = getFolderPath(pointId);

        List<String> result = new ArrayList<String>();
        // Skip the root.
        for (int i = 1; i < path.size(); i++)
            result.add(path.get(i).getName());

        return result;
    }

    public List<PointFolder> getFolderPath(int pointId) {
        List<PointFolder> path = new ArrayList<PointFolder>();
        root.findPoint(path, pointId);
        if (path.isEmpty())
            path.add(root);
        else
            // findPoint returns the path in reverse order.
            Collections.reverse(path);

        return path;
    }

    public void parseEmptyFolders() {
        parseEmptyFoldersRecursive(root);
    }

    private static void parseEmptyFoldersRecursive(PointFolder folder) {
        PointFolder sub;
        for (int i = folder.getSubfolders().size() - 1; i >= 0; i--) {
            sub = folder.getSubfolders().get(i);
            parseEmptyFoldersRecursive(sub);

            if (sub.getPoints().size() == 0 && sub.getSubfolders().size() == 0)
                folder.getSubfolders().remove(i);
        }
    }
}
