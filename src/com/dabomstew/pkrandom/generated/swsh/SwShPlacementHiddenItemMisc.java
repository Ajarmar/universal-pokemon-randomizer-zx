// automatically generated by the FlatBuffers compiler, do not modify

package com.dabomstew.pkrandom.generated.swsh;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@javax.annotation.Generated(value="flatc")
@SuppressWarnings("unused")
public final class SwShPlacementHiddenItemMisc extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static SwShPlacementHiddenItemMisc getRootAsSwShPlacementHiddenItemMisc(ByteBuffer _bb) { return getRootAsSwShPlacementHiddenItemMisc(_bb, new SwShPlacementHiddenItemMisc()); }
  public static SwShPlacementHiddenItemMisc getRootAsSwShPlacementHiddenItemMisc(ByteBuffer _bb, SwShPlacementHiddenItemMisc obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SwShPlacementHiddenItemMisc __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int field00() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateField00(int field_00) { int o = __offset(4); if (o != 0) { bb.putInt(o + bb_pos, field_00); return true; } else { return false; } }
  public float field01() { int o = __offset(6); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
  public boolean mutateField01(float field_01) { int o = __offset(6); if (o != 0) { bb.putFloat(o + bb_pos, field_01); return true; } else { return false; } }
  public float field02() { int o = __offset(8); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
  public boolean mutateField02(float field_02) { int o = __offset(8); if (o != 0) { bb.putFloat(o + bb_pos, field_02); return true; } else { return false; } }
  public float field03() { int o = __offset(10); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
  public boolean mutateField03(float field_03) { int o = __offset(10); if (o != 0) { bb.putFloat(o + bb_pos, field_03); return true; } else { return false; } }
  public float field04() { int o = __offset(12); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
  public boolean mutateField04(float field_04) { int o = __offset(12); if (o != 0) { bb.putFloat(o + bb_pos, field_04); return true; } else { return false; } }

  public static int createSwShPlacementHiddenItemMisc(FlatBufferBuilder builder,
      int field_00,
      float field_01,
      float field_02,
      float field_03,
      float field_04) {
    builder.startTable(5);
    SwShPlacementHiddenItemMisc.addField04(builder, field_04);
    SwShPlacementHiddenItemMisc.addField03(builder, field_03);
    SwShPlacementHiddenItemMisc.addField02(builder, field_02);
    SwShPlacementHiddenItemMisc.addField01(builder, field_01);
    SwShPlacementHiddenItemMisc.addField00(builder, field_00);
    return SwShPlacementHiddenItemMisc.endSwShPlacementHiddenItemMisc(builder);
  }

  public static void startSwShPlacementHiddenItemMisc(FlatBufferBuilder builder) { builder.startTable(5); }
  public static void addField00(FlatBufferBuilder builder, int field00) { builder.addInt(0, field00, 0); }
  public static void addField01(FlatBufferBuilder builder, float field01) { builder.addFloat(1, field01, 0.0f); }
  public static void addField02(FlatBufferBuilder builder, float field02) { builder.addFloat(2, field02, 0.0f); }
  public static void addField03(FlatBufferBuilder builder, float field03) { builder.addFloat(3, field03, 0.0f); }
  public static void addField04(FlatBufferBuilder builder, float field04) { builder.addFloat(4, field04, 0.0f); }
  public static int endSwShPlacementHiddenItemMisc(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SwShPlacementHiddenItemMisc get(int j) { return get(new SwShPlacementHiddenItemMisc(), j); }
    public SwShPlacementHiddenItemMisc get(SwShPlacementHiddenItemMisc obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}
