// automatically generated by the FlatBuffers compiler, do not modify

package com.dabomstew.pkrandom.generated.swsh;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@javax.annotation.Generated(value="flatc")
@SuppressWarnings("unused")
public final class SwShPlacementFieldItemA extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static SwShPlacementFieldItemA getRootAsSwShPlacementFieldItemA(ByteBuffer _bb) { return getRootAsSwShPlacementFieldItemA(_bb, new SwShPlacementFieldItemA()); }
  public static SwShPlacementFieldItemA getRootAsSwShPlacementFieldItemA(ByteBuffer _bb, SwShPlacementFieldItemA obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SwShPlacementFieldItemA __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public boolean field00() { int o = __offset(4); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }
  public boolean mutateField00(boolean field_00) { int o = __offset(4); if (o != 0) { bb.put(o + bb_pos, (byte)(field_00 ? 1 : 0)); return true; } else { return false; } }
  public com.dabomstew.pkrandom.generated.swsh.SwShFlatDummyObject field01() { return field01(new com.dabomstew.pkrandom.generated.swsh.SwShFlatDummyObject()); }
  public com.dabomstew.pkrandom.generated.swsh.SwShFlatDummyObject field01(com.dabomstew.pkrandom.generated.swsh.SwShFlatDummyObject obj) { int o = __offset(6); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createSwShPlacementFieldItemA(FlatBufferBuilder builder,
      boolean field_00,
      int field_01Offset) {
    builder.startTable(2);
    SwShPlacementFieldItemA.addField01(builder, field_01Offset);
    SwShPlacementFieldItemA.addField00(builder, field_00);
    return SwShPlacementFieldItemA.endSwShPlacementFieldItemA(builder);
  }

  public static void startSwShPlacementFieldItemA(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addField00(FlatBufferBuilder builder, boolean field00) { builder.addBoolean(0, field00, false); }
  public static void addField01(FlatBufferBuilder builder, int field01Offset) { builder.addOffset(1, field01Offset, 0); }
  public static int endSwShPlacementFieldItemA(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SwShPlacementFieldItemA get(int j) { return get(new SwShPlacementFieldItemA(), j); }
    public SwShPlacementFieldItemA get(SwShPlacementFieldItemA obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}
