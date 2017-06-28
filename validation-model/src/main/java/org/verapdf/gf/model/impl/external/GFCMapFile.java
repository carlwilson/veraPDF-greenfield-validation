/**
 * This file is part of validation-model, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * validation-model is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with validation-model as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * validation-model as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gf.model.impl.external;

import org.verapdf.cos.COSStream;
import org.verapdf.model.external.CMapFile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents embedded CMap file.
 *
 * @author Sergey Shemyakov
 */
public class GFCMapFile extends GFExternal implements CMapFile {

    private static final Logger LOGGER = Logger.getLogger(GFCMapFile.class.getCanonicalName());

    private org.verapdf.pd.font.cmap.CMapFile cMapFile;

    /**
     * Type name for GFCMapFile
     */
    public static final String CMAP_FILE_TYPE = "CMapFile";

    /**
     * Constructor from COSStream containing CMap.
     *
     * @param stream is CMap stream.
     */
    public GFCMapFile(COSStream stream) {
        super(CMAP_FILE_TYPE);
        this.cMapFile = new org.verapdf.pd.font.cmap.CMapFile(stream);
    }

    /**
     * @return the value of the WMode entry in the embedded CMap file.
     */
    @Override
    public Long getWMode() {
        try {
            return new Long(cMapFile.getWMode());
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Error in parsing embedded CMap file");
            e.printStackTrace();
            return new Long(-1);
        }
    }

    /**
     * @return the value of the WMode entry in the parent CMap dictionary.
     */
    @Override
    public Long getdictWMode() {
        return new Long(cMapFile.getDictWMode());
    }

    @Override
    public Long getmaximalCID() {
        return new Long(this.cMapFile.getMaxCID());
    }
}
