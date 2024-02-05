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
package com.serotonin.mango.view;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.scada_lts.utils.UploadFileUtils.*;

public class ViewGraphicLoader {
    private static final Log LOG = LogFactory.getLog(ViewGraphicLoader.class);

    private Path path;

    public List<ViewGraphic> loadViewGraphics(Path path) {
        this.path = getGraphicsBaseSystemFilePath(path);
        List<ViewGraphic> viewGraphics = new ArrayList<>();

        File graphicsPath = path.toFile();
        File[] dirs = graphicsPath.listFiles();
        if(dirs != null) {
            for (File dir : dirs) {
                try {
                    if (dir.isDirectory())
                        viewGraphics.addAll(loadDirectory(dir, ""));
                } catch (Exception e) {
                    LOG.warn("Failed to load image set at " + dir, e);
                }
            }
        } else {
            LOG.warn("Not exists: " + path);
        }
        viewGraphics.sort(Comparator.comparing(ViewGraphic::getName));
        return viewGraphics;
    }

    private List<ViewGraphic> loadDirectory(File dir, String baseId) throws Exception {
        List<ViewGraphic> result = new ArrayList<>();
        String id = baseId + dir.getName();
        String name = id;
        String typeStr = "imageSet";
        int width = -1;
        int height = -1;
        int textX = 5;
        int textY = 5;

        File[] files = dir.listFiles();
        Arrays.sort(files);
        List<String> imageUrls = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory())
                loadDirectory(file, id + ".");
            else if (isThumbsFile(file)) {
                // no op
            }
            else if (isInfoFile(file)) {
                // Info file
                Properties props = new Properties();
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    props.load(fileInputStream);
                    name = getProperty(props, "name", name);
                    typeStr = getProperty(props, "type", "imageSet");
                    width = getIntProperty(props, "width", width);
                    height = getIntProperty(props, "height", height);
                    textX = getIntProperty(props, "text.x", textX);
                    textY = getIntProperty(props, "text.y", textY);
                }
            }
            else if(isImageBitmap(file)) {
                // Image file. Subtract the load path from the image path
                String imageUrl = file.getPath().substring(path.toString().length());
                if(imageUrl.startsWith("/") || imageUrl.startsWith("\\")) {
                    imageUrl=imageUrl.substring(1);
                }
                // Replace Windows-style '\' path separators with '/'
                imageUrl = imageUrl.replaceAll("\\\\", "/");
                imageUrls.add(imageUrl);
            }
            else {
                LOG.warn("File is not supported type: " + file);
            }
        }

        if (!imageUrls.isEmpty()) {
            if (width == -1 || height == -1) {
                String imageSystemFilePath = path + File.separator + normalizeSeparator(imageUrls.get(0));
                Image image = Toolkit.getDefaultToolkit().getImage(imageSystemFilePath);
                MediaTracker tracker = new MediaTracker(new Container());
                tracker.addImage(image, 0);
                tracker.waitForID(0);

                if (width == -1)
                    width = image.getWidth(null);
                if (height == -1)
                    height = image.getHeight(null);
            }

            if (width == -1 || height == -1)
                throw new Exception("Unable to derive image dimensions");

            String[] imageUrlsArray = imageUrls.toArray(new String[imageUrls.size()]);
            ViewGraphic g;
            if ("imageSet".equals(typeStr))
                g = new ImageSet(id, name, imageUrlsArray, width, height, textX, textY);
            else if ("dynamic".equals(typeStr))
                g = new DynamicImage(id, name, imageUrlsArray[0], width, height, textX, textY);
            else
                throw new Exception("Invalid type: " + typeStr);

            result.add(g);
        }
        return result;
    }

    private String getProperty(Properties props, String key, String defaultValue) {
        String prop = (String) props.get(key);
        if (prop == null)
            return defaultValue;
        return prop;
    }

    private int getIntProperty(Properties props, String key, int defaultValue) {
        String prop = (String) props.get(key);
        if (prop == null)
            return defaultValue;
        try {
            return Integer.parseInt(prop);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
