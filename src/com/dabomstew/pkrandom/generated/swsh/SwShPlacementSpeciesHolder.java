// automatically generated by the FlatBuffers compiler, do not modify

package com.dabomstew.pkrandom.generated.swsh;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@javax.annotation.Generated(value="flatc")
@SuppressWarnings("unused")
public final class SwShPlacementSpeciesHolder extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static SwShPlacementSpeciesHolder getRootAsSwShPlacementSpeciesHolder(ByteBuffer _bb) { return getRootAsSwShPlacementSpeciesHolder(_bb, new SwShPlacementSpeciesHolder()); }
  public static SwShPlacementSpeciesHolder getRootAsSwShPlacementSpeciesHolder(ByteBuffer _bb, SwShPlacementSpeciesHolder obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SwShPlacementSpeciesHolder __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public long field00() { int o = __offset(4); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateField00(long field_00) { int o = __offset(4); if (o != 0) { bb.putInt(o + bb_pos, (int)field_00); return true; } else { return false; } }
  public long field01() { int o = __offset(6); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateField01(long field_01) { int o = __offset(6); if (o != 0) { bb.putInt(o + bb_pos, (int)field_01); return true; } else { return false; } }
  public long species() { int o = __offset(8); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateSpecies(long species) { int o = __offset(8); if (o != 0) { bb.putInt(o + bb_pos, (int)species); return true; } else { return false; } }
  public long form() { int o = __offset(10); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateForm(long form) { int o = __offset(10); if (o != 0) { bb.putInt(o + bb_pos, (int)form); return true; } else { return false; } }
  public long gender() { int o = __offset(12); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateGender(long gender) { int o = __offset(12); if (o != 0) { bb.putInt(o + bb_pos, (int)gender); return true; } else { return false; } }
  public long shiny() { int o = __offset(14); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateShiny(long shiny) { int o = __offset(14); if (o != 0) { bb.putInt(o + bb_pos, (int)shiny); return true; } else { return false; } }
  public long unused2() { int o = __offset(16); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateUnused2(long unused2) { int o = __offset(16); if (o != 0) { bb.putInt(o + bb_pos, (int)unused2); return true; } else { return false; } }
  public long hash07() { int o = __offset(18); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateHash07(long hash_07) { int o = __offset(18); if (o != 0) { bb.putLong(o + bb_pos, hash_07); return true; } else { return false; } }
  public long hash08() { int o = __offset(20); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateHash08(long hash_08) { int o = __offset(20); if (o != 0) { bb.putLong(o + bb_pos, hash_08); return true; } else { return false; } }
  public long hash09() { int o = __offset(22); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateHash09(long hash_09) { int o = __offset(22); if (o != 0) { bb.putLong(o + bb_pos, hash_09); return true; } else { return false; } }
  public long field10(int j) { int o = __offset(24); return o != 0 ? (long)bb.getInt(__vector(o) + j * 4) & 0xFFFFFFFFL : 0; }
  public int field10Length() { int o = __offset(24); return o != 0 ? __vector_len(o) : 0; }
  public IntVector field10Vector() { return field10Vector(new IntVector()); }
  public IntVector field10Vector(IntVector obj) { int o = __offset(24); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer field10AsByteBuffer() { return __vector_as_bytebuffer(24, 4); }
  public ByteBuffer field10InByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 24, 4); }
  public boolean mutateField10(int j, long field_10) { int o = __offset(24); if (o != 0) { bb.putInt(__vector(o) + j * 4, (int)field_10); return true; } else { return false; } }
  public float field11() { int o = __offset(26); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
  public boolean mutateField11(float field_11) { int o = __offset(26); if (o != 0) { bb.putFloat(o + bb_pos, field_11); return true; } else { return false; } }
  public long field12() { int o = __offset(28); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateField12(long field_12) { int o = __offset(28); if (o != 0) { bb.putInt(o + bb_pos, (int)field_12); return true; } else { return false; } }
  public int field13() { int o = __offset(30); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateField13(int field_13) { int o = __offset(30); if (o != 0) { bb.putInt(o + bb_pos, field_13); return true; } else { return false; } }
  public int field14() { int o = __offset(32); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateField14(int field_14) { int o = __offset(32); if (o != 0) { bb.putInt(o + bb_pos, field_14); return true; } else { return false; } }
  public int num15() { int o = __offset(34); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateNum15(int num_15) { int o = __offset(34); if (o != 0) { bb.put(o + bb_pos, (byte)num_15); return true; } else { return false; } }

  public static int createSwShPlacementSpeciesHolder(FlatBufferBuilder builder,
      long field_00,
      long field_01,
      long species,
      long form,
      long gender,
      long shiny,
      long unused2,
      long hash_07,
      long hash_08,
      long hash_09,
      int field_10Offset,
      float field_11,
      long field_12,
      int field_13,
      int field_14,
      int num_15) {
    builder.startTable(16);
    SwShPlacementSpeciesHolder.addHash09(builder, hash_09);
    SwShPlacementSpeciesHolder.addHash08(builder, hash_08);
    SwShPlacementSpeciesHolder.addHash07(builder, hash_07);
    SwShPlacementSpeciesHolder.addField14(builder, field_14);
    SwShPlacementSpeciesHolder.addField13(builder, field_13);
    SwShPlacementSpeciesHolder.addField12(builder, field_12);
    SwShPlacementSpeciesHolder.addField11(builder, field_11);
    SwShPlacementSpeciesHolder.addField10(builder, field_10Offset);
    SwShPlacementSpeciesHolder.addUnused2(builder, unused2);
    SwShPlacementSpeciesHolder.addShiny(builder, shiny);
    SwShPlacementSpeciesHolder.addGender(builder, gender);
    SwShPlacementSpeciesHolder.addForm(builder, form);
    SwShPlacementSpeciesHolder.addSpecies(builder, species);
    SwShPlacementSpeciesHolder.addField01(builder, field_01);
    SwShPlacementSpeciesHolder.addField00(builder, field_00);
    SwShPlacementSpeciesHolder.addNum15(builder, num_15);
    return SwShPlacementSpeciesHolder.endSwShPlacementSpeciesHolder(builder);
  }

  public static void startSwShPlacementSpeciesHolder(FlatBufferBuilder builder) { builder.startTable(16); }
  public static void addField00(FlatBufferBuilder builder, long field00) { builder.addInt(0, (int)field00, (int)0L); }
  public static void addField01(FlatBufferBuilder builder, long field01) { builder.addInt(1, (int)field01, (int)0L); }
  public static void addSpecies(FlatBufferBuilder builder, long species) { builder.addInt(2, (int)species, (int)0L); }
  public static void addForm(FlatBufferBuilder builder, long form) { builder.addInt(3, (int)form, (int)0L); }
  public static void addGender(FlatBufferBuilder builder, long gender) { builder.addInt(4, (int)gender, (int)0L); }
  public static void addShiny(FlatBufferBuilder builder, long shiny) { builder.addInt(5, (int)shiny, (int)0L); }
  public static void addUnused2(FlatBufferBuilder builder, long unused2) { builder.addInt(6, (int)unused2, (int)0L); }
  public static void addHash07(FlatBufferBuilder builder, long hash07) { builder.addLong(7, hash07, 0L); }
  public static void addHash08(FlatBufferBuilder builder, long hash08) { builder.addLong(8, hash08, 0L); }
  public static void addHash09(FlatBufferBuilder builder, long hash09) { builder.addLong(9, hash09, 0L); }
  public static void addField10(FlatBufferBuilder builder, int field10Offset) { builder.addOffset(10, field10Offset, 0); }
  public static int createField10Vector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startField10Vector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addField11(FlatBufferBuilder builder, float field11) { builder.addFloat(11, field11, 0.0f); }
  public static void addField12(FlatBufferBuilder builder, long field12) { builder.addInt(12, (int)field12, (int)0L); }
  public static void addField13(FlatBufferBuilder builder, int field13) { builder.addInt(13, field13, 0); }
  public static void addField14(FlatBufferBuilder builder, int field14) { builder.addInt(14, field14, 0); }
  public static void addNum15(FlatBufferBuilder builder, int num15) { builder.addByte(15, (byte)num15, (byte)0); }
  public static int endSwShPlacementSpeciesHolder(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SwShPlacementSpeciesHolder get(int j) { return get(new SwShPlacementSpeciesHolder(), j); }
    public SwShPlacementSpeciesHolder get(SwShPlacementSpeciesHolder obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

