/*
 * (c) 2018 grzegorz.bylica@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.service;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.ViewGraphic;
import com.serotonin.mango.view.ViewGraphicLoader;
import com.serotonin.mango.web.ContextWrapper;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
@Service
public class ResourcesService {

    public void refreshImages() {
        ViewGraphicLoader loader = new ViewGraphicLoader();
        List<ImageSet> imageSets = new ArrayList<ImageSet>();
        List<DynamicImage> dynamicImages = new ArrayList<DynamicImage>();

        ServletContext ctx = Common.ctx.getCtx();

        for (ViewGraphic g : loader.loadViewGraphics(ctx.getRealPath(""))) {
            if (g.isImageSet())
                imageSets.add((ImageSet) g);
            else if (g.isDynamicImage())
                dynamicImages.add((DynamicImage) g);
            else
                throw new ShouldNeverHappenException(
                        "Unknown view graphic type");
        }

        ctx.setAttribute(Common.ContextKeys.IMAGE_SETS, imageSets);
        ctx.setAttribute(Common.ContextKeys.DYNAMIC_IMAGES, dynamicImages);

        Common.ctx = new ContextWrapper(ctx);

    }

}
