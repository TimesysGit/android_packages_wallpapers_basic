
package com.android.wallpaper.grass;

import android.content.res.Resources;
import android.renderscript.*;
import android.util.Log;

public class ScriptField_Vertex
    extends android.renderscript.Script.FieldBase
{

    static public class Item {
        Item() {
        }

        // When a float2 is present LLVM alings to 8 bytes.
        public static final int sizeof = (5*4);
        int color;
        float x;
        float y;
        float s;
        float t;
    }
    private Item mItemArray[];


    public ScriptField_Vertex(RenderScript rs, int count) {
        // Allocate a pack/unpack buffer
        mIOBuffer = new FieldPacker(Item.sizeof * count);
        mItemArray = new Item[count];

        Element.Builder eb = new Element.Builder(rs);
        eb.add(Element.createAttrib(rs, Element.DataType.UNSIGNED_8, Element.DataKind.COLOR, 4), "color");
        eb.add(Element.createAttrib(rs, Element.DataType.FLOAT_32, Element.DataKind.POSITION, 2), "position");
        eb.add(Element.createAttrib(rs, Element.DataType.FLOAT_32, Element.DataKind.TEXTURE, 2), "texCoord");
        mElement = eb.create();

        init(rs, count);
    }

    private void copyToArray(Item i, int index) {
        mIOBuffer.reset(index * Item.sizeof);
        mIOBuffer.addI32(i.color);
        mIOBuffer.addF32(i.x);
        mIOBuffer.addF32(i.y);
        mIOBuffer.addF32(i.s);
        mIOBuffer.addF32(i.t);
    }

    public void set(Item i, int index, boolean copyNow) {
        mItemArray[index] = i;
        if (copyNow) {
            copyToArray(i, index);
            mAllocation.subData1D(index * Item.sizeof, Item.sizeof, mIOBuffer.getData());
        }
    }

    public void copyAll() {
        for (int ct=0; ct < mItemArray.length; ct++) {
            copyToArray(mItemArray[ct], ct);
        }
        mAllocation.data(mIOBuffer.getData());
    }


    private FieldPacker mIOBuffer;
}