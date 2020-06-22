/**
 * This file is part of veraPDF Validation, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Validation is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Validation as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Validation as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gf.model.impl.pd.gfse;

import org.verapdf.gf.model.impl.operator.markedcontent.GFOpMarkedContent;
import org.verapdf.gf.model.impl.operator.shading.GFOp_sh;
import org.verapdf.model.selayer.SEShadingItem;

public class GFSEShadingItem extends GFSESimpleContentItem implements SEShadingItem {

    public static final String SHADING_ITEM_TYPE = "SEShadingItem";

    private GFOp_sh op_sh;

    public GFSEShadingItem(GFOp_sh op_sh) {
        super(SHADING_ITEM_TYPE);
        this.op_sh = op_sh;
    }

    public GFSEShadingItem(GFOp_sh op_sh, GFOpMarkedContent parentMarkedContentOperator) {
        super(SHADING_ITEM_TYPE, parentMarkedContentOperator);
        this.op_sh = op_sh;

    }

}
