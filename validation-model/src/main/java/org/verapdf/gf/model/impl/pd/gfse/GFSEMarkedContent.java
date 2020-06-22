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

import org.verapdf.as.ASAtom;
import org.verapdf.cos.COSString;
import org.verapdf.gf.model.impl.operator.inlineimage.GFOp_EI;
import org.verapdf.gf.model.impl.operator.markedcontent.GFOpMarkedContent;
import org.verapdf.gf.model.impl.operator.markedcontent.GFOp_BDC;
import org.verapdf.gf.model.impl.operator.markedcontent.GFOp_BMC;
import org.verapdf.gf.model.impl.operator.markedcontent.GFOp_EMC;
import org.verapdf.gf.model.impl.operator.pathpaint.GFOpPathPaint;
import org.verapdf.gf.model.impl.operator.pathpaint.GFOp_n;
import org.verapdf.gf.model.impl.operator.shading.GFOp_sh;
import org.verapdf.gf.model.impl.operator.textshow.GFOpTextShow;
import org.verapdf.gf.model.impl.operator.textshow.GFOp_TJ_Big;
import org.verapdf.gf.model.impl.operator.textshow.GFOp_Tj;
import org.verapdf.gf.model.impl.operator.xobject.GFOp_Do;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.coslayer.CosLang;
import org.verapdf.model.coslayer.CosName;
import org.verapdf.model.operator.Operator;
import org.verapdf.model.pdlayer.PDXObject;
import org.verapdf.model.selayer.SEContentItem;
import org.verapdf.model.selayer.SEMarkedContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GFSEMarkedContent extends GFSEContentItem implements SEMarkedContent {

    public static final String MARKED_CONTENT_TYPE = "SEMarkedContent";

    public static final String LANG = "Lang";

    private GFOpMarkedContent operator;

    public GFSEMarkedContent(List<Operator> operators) {
        this(operators, null);
    }

    public GFSEMarkedContent(List<Operator> operators, GFOpMarkedContent parentMarkedContentOperator) {
        super(MARKED_CONTENT_TYPE, parentMarkedContentOperator);
        this.operators = operators.subList(1, operators.size() - 1);
        this.operator = (GFOpMarkedContent)operators.get(0);
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case CONTENT_ITEM:
                return this.getContentItem();
            case LANG:
                return this.getLang();
            default:
                return super.getLinkedObjects(link);
        }
    }

    private List<SEContentItem> getContentItem() {
        if (operators == null) {
            return Collections.emptyList();
        }
        int markedContentIndex;
        Stack<Integer> markedContentStack = new Stack<>();
        List<SEContentItem> list = new ArrayList<>();
        for (int i = 0; i < operators.size(); i++) {
            Operator op = operators.get(i);
            String type = op.getObjectType();
            if (type.equals(GFOp_BDC.OP_BDC_TYPE) || type.equals(GFOp_BMC.OP_BMC_TYPE)) {
                markedContentStack.push(i);
            } else if (type.equals(GFOp_EMC.OP_EMC_TYPE)) {
                if (!markedContentStack.empty()) {
                    markedContentIndex = markedContentStack.pop();
                    if (markedContentStack.empty()) {
                        list.add(new GFSEMarkedContent(operators.subList(markedContentIndex, i + 1), this.operator));
                    }
                }
            }
            if (markedContentStack.empty()) {
                if (type.equals(GFOp_Tj.OP_TJ_TYPE) || type.equals(GFOp_TJ_Big.OP_TJ_BIG_TYPE)) {
                    list.add(new GFSETextItem((GFOpTextShow)op, this.operator));
                } else if (op instanceof GFOp_sh) {
                    list.add(new GFSEShadingItem((GFOp_sh)op, this.operator));
                } else if (op instanceof GFOpPathPaint && !(op instanceof GFOp_n)) {
                    list.add(new GFSELineArtItem((GFOpPathPaint)op, this.operator));
                } else if (op instanceof GFOp_EI) {
                    list.add(new GFSEImageItem((GFOp_EI)op, this.operator));
                } else if (op instanceof GFOp_Do) {
                    List<PDXObject> xObjects = ((GFOp_Do)op).getXObject();
                    if (xObjects != null && xObjects.size() != 0 && ASAtom.IMAGE.getValue().equals(xObjects.get(0).getSubtype())) {
                        list.add(new GFSEImageItem((GFOp_Do)op, this.operator));
                    }
                }
            }
        }
        return Collections.unmodifiableList(list);
    }

    public List<CosLang> getLang() {
        List<CosLang> lang = operator.getLang();
        if (lang.size() != 0) {
            return lang;
        }
        return operator.getParentLang();
    }

    @Override
    public String getExtraContext() {
        Long mcid = operator.getMCID();
        return mcid != null ?  "mcid:" + mcid : parentMCID != null ? "mcid:" + parentMCID : null;
    }

    @Override
    public String gettag() {
        List<CosName> tag = operator.getTag();
        if (tag != null && tag.size() != 0) {
            return tag.get(0).getinternalRepresentation();
        }
        return null;
    }

    @Override
    public String getstructureTag() {
        if (operator != null) {
            if (operator.getObjectType().equals(GFOp_BDC.OP_BDC_TYPE)) {
               return ((GFOp_BDC)operator).getstructureTag();
            }
        }
        return null;
    }

    @Override
    public String getE() {
        COSString E = operator.getE();
        return E != null ? E.toString() : null;
    }

    @Override
    public String getAlt() {
        COSString Alt = operator.getAlt();
        return Alt != null ? Alt.toString() : null;
    }

    @Override
    public String getActualText() {
        COSString ActualText = operator.getActualText();
        return ActualText != null ? ActualText.toString() : null;
    }

}
