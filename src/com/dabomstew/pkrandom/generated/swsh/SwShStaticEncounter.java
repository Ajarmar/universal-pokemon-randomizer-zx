// automatically generated by the FlatBuffers compiler, do not modify

package com.dabomstew.pkrandom.generated.swsh;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@javax.annotation.Generated(value="flatc")
@SuppressWarnings("unused")
public final class SwShStaticEncounter extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static SwShStaticEncounter getRootAsSwShStaticEncounter(ByteBuffer _bb) { return getRootAsSwShStaticEncounter(_bb, new SwShStaticEncounter()); }
  public static SwShStaticEncounter getRootAsSwShStaticEncounter(ByteBuffer _bb, SwShStaticEncounter obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SwShStaticEncounter __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public long backgroundFarTypeId() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateBackgroundFarTypeId(long background_far_type_id) { int o = __offset(4); if (o != 0) { bb.putLong(o + bb_pos, background_far_type_id); return true; } else { return false; } }
  public long backgroundNearTypeId() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateBackgroundNearTypeId(long background_near_type_id) { int o = __offset(6); if (o != 0) { bb.putLong(o + bb_pos, background_near_type_id); return true; } else { return false; } }
  public int evSpe() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvSpe(int ev_spe) { int o = __offset(8); if (o != 0) { bb.put(o + bb_pos, (byte)ev_spe); return true; } else { return false; } }
  public int evAtk() { int o = __offset(10); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvAtk(int ev_atk) { int o = __offset(10); if (o != 0) { bb.put(o + bb_pos, (byte)ev_atk); return true; } else { return false; } }
  public int evDef() { int o = __offset(12); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvDef(int ev_def) { int o = __offset(12); if (o != 0) { bb.put(o + bb_pos, (byte)ev_def); return true; } else { return false; } }
  public int evHp() { int o = __offset(14); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvHp(int ev_hp) { int o = __offset(14); if (o != 0) { bb.put(o + bb_pos, (byte)ev_hp); return true; } else { return false; } }
  public int evSpa() { int o = __offset(16); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvSpa(int ev_spa) { int o = __offset(16); if (o != 0) { bb.put(o + bb_pos, (byte)ev_spa); return true; } else { return false; } }
  public int evSpd() { int o = __offset(18); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateEvSpd(int ev_spd) { int o = __offset(18); if (o != 0) { bb.put(o + bb_pos, (byte)ev_spd); return true; } else { return false; } }
  public int form() { int o = __offset(20); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateForm(int form) { int o = __offset(20); if (o != 0) { bb.put(o + bb_pos, (byte)form); return true; } else { return false; } }
  public int dynamaxLevel() { int o = __offset(22); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateDynamaxLevel(int dynamax_level) { int o = __offset(22); if (o != 0) { bb.put(o + bb_pos, (byte)dynamax_level); return true; } else { return false; } }
  public int field0a() { int o = __offset(24); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateField0a(int field_0a) { int o = __offset(24); if (o != 0) { bb.putInt(o + bb_pos, field_0a); return true; } else { return false; } }
  public long encounterId() { int o = __offset(26); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean mutateEncounterId(long encounter_id) { int o = __offset(26); if (o != 0) { bb.putLong(o + bb_pos, encounter_id); return true; } else { return false; } }
  public int field0c() { int o = __offset(28); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateField0c(int field_0c) { int o = __offset(28); if (o != 0) { bb.put(o + bb_pos, (byte)field_0c); return true; } else { return false; } }
  public boolean gmaxFactor() { int o = __offset(30); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }
  public boolean mutateGmaxFactor(boolean gmax_factor) { int o = __offset(30); if (o != 0) { bb.put(o + bb_pos, (byte)(gmax_factor ? 1 : 0)); return true; } else { return false; } }
  public int heldItem() { int o = __offset(32); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateHeldItem(int held_item) { int o = __offset(32); if (o != 0) { bb.putInt(o + bb_pos, held_item); return true; } else { return false; } }
  public int level() { int o = __offset(34); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateLevel(int level) { int o = __offset(34); if (o != 0) { bb.put(o + bb_pos, (byte)level); return true; } else { return false; } }
  public int encounterScenario() { int o = __offset(36); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateEncounterScenario(int encounter_scenario) { int o = __offset(36); if (o != 0) { bb.putInt(o + bb_pos, encounter_scenario); return true; } else { return false; } }
  public int species() { int o = __offset(38); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateSpecies(int species) { int o = __offset(38); if (o != 0) { bb.putInt(o + bb_pos, species); return true; } else { return false; } }
  public long shinyLock() { int o = __offset(40); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateShinyLock(long shiny_lock) { int o = __offset(40); if (o != 0) { bb.putInt(o + bb_pos, (int)shiny_lock); return true; } else { return false; } }
  public long nature() { int o = __offset(42); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0L; }
  public boolean mutateNature(long nature) { int o = __offset(42); if (o != 0) { bb.putInt(o + bb_pos, (int)nature); return true; } else { return false; } }
  public int gender() { int o = __offset(44); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public boolean mutateGender(int gender) { int o = __offset(44); if (o != 0) { bb.put(o + bb_pos, (byte)gender); return true; } else { return false; } }
  public byte ivSpe() { int o = __offset(46); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvSpe(byte iv_spe) { int o = __offset(46); if (o != 0) { bb.put(o + bb_pos, iv_spe); return true; } else { return false; } }
  public byte ivAtk() { int o = __offset(48); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvAtk(byte iv_atk) { int o = __offset(48); if (o != 0) { bb.put(o + bb_pos, iv_atk); return true; } else { return false; } }
  public byte ivDef() { int o = __offset(50); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvDef(byte iv_def) { int o = __offset(50); if (o != 0) { bb.put(o + bb_pos, iv_def); return true; } else { return false; } }
  public byte ivHp() { int o = __offset(52); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvHp(byte iv_hp) { int o = __offset(52); if (o != 0) { bb.put(o + bb_pos, iv_hp); return true; } else { return false; } }
  public byte ivSpa() { int o = __offset(54); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvSpa(byte iv_spa) { int o = __offset(54); if (o != 0) { bb.put(o + bb_pos, iv_spa); return true; } else { return false; } }
  public byte ivSpd() { int o = __offset(56); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateIvSpd(byte iv_spd) { int o = __offset(56); if (o != 0) { bb.put(o + bb_pos, iv_spd); return true; } else { return false; } }
  public int ability() { int o = __offset(58); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateAbility(int ability) { int o = __offset(58); if (o != 0) { bb.putInt(o + bb_pos, ability); return true; } else { return false; } }
  public int move0() { int o = __offset(60); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateMove0(int move0) { int o = __offset(60); if (o != 0) { bb.putInt(o + bb_pos, move0); return true; } else { return false; } }
  public int move1() { int o = __offset(62); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateMove1(int move1) { int o = __offset(62); if (o != 0) { bb.putInt(o + bb_pos, move1); return true; } else { return false; } }
  public int move2() { int o = __offset(64); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateMove2(int move2) { int o = __offset(64); if (o != 0) { bb.putInt(o + bb_pos, move2); return true; } else { return false; } }
  public int move3() { int o = __offset(66); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean mutateMove3(int move3) { int o = __offset(66); if (o != 0) { bb.putInt(o + bb_pos, move3); return true; } else { return false; } }

  public static int createSwShStaticEncounter(FlatBufferBuilder builder,
      long background_far_type_id,
      long background_near_type_id,
      int ev_spe,
      int ev_atk,
      int ev_def,
      int ev_hp,
      int ev_spa,
      int ev_spd,
      int form,
      int dynamax_level,
      int field_0a,
      long encounter_id,
      int field_0c,
      boolean gmax_factor,
      int held_item,
      int level,
      int encounter_scenario,
      int species,
      long shiny_lock,
      long nature,
      int gender,
      byte iv_spe,
      byte iv_atk,
      byte iv_def,
      byte iv_hp,
      byte iv_spa,
      byte iv_spd,
      int ability,
      int move0,
      int move1,
      int move2,
      int move3) {
    builder.startTable(32);
    SwShStaticEncounter.addEncounterId(builder, encounter_id);
    SwShStaticEncounter.addBackgroundNearTypeId(builder, background_near_type_id);
    SwShStaticEncounter.addBackgroundFarTypeId(builder, background_far_type_id);
    SwShStaticEncounter.addMove3(builder, move3);
    SwShStaticEncounter.addMove2(builder, move2);
    SwShStaticEncounter.addMove1(builder, move1);
    SwShStaticEncounter.addMove0(builder, move0);
    SwShStaticEncounter.addAbility(builder, ability);
    SwShStaticEncounter.addNature(builder, nature);
    SwShStaticEncounter.addShinyLock(builder, shiny_lock);
    SwShStaticEncounter.addSpecies(builder, species);
    SwShStaticEncounter.addEncounterScenario(builder, encounter_scenario);
    SwShStaticEncounter.addHeldItem(builder, held_item);
    SwShStaticEncounter.addField0a(builder, field_0a);
    SwShStaticEncounter.addIvSpd(builder, iv_spd);
    SwShStaticEncounter.addIvSpa(builder, iv_spa);
    SwShStaticEncounter.addIvHp(builder, iv_hp);
    SwShStaticEncounter.addIvDef(builder, iv_def);
    SwShStaticEncounter.addIvAtk(builder, iv_atk);
    SwShStaticEncounter.addIvSpe(builder, iv_spe);
    SwShStaticEncounter.addGender(builder, gender);
    SwShStaticEncounter.addLevel(builder, level);
    SwShStaticEncounter.addGmaxFactor(builder, gmax_factor);
    SwShStaticEncounter.addField0c(builder, field_0c);
    SwShStaticEncounter.addDynamaxLevel(builder, dynamax_level);
    SwShStaticEncounter.addForm(builder, form);
    SwShStaticEncounter.addEvSpd(builder, ev_spd);
    SwShStaticEncounter.addEvSpa(builder, ev_spa);
    SwShStaticEncounter.addEvHp(builder, ev_hp);
    SwShStaticEncounter.addEvDef(builder, ev_def);
    SwShStaticEncounter.addEvAtk(builder, ev_atk);
    SwShStaticEncounter.addEvSpe(builder, ev_spe);
    return SwShStaticEncounter.endSwShStaticEncounter(builder);
  }

  public static void startSwShStaticEncounter(FlatBufferBuilder builder) { builder.startTable(32); }
  public static void addBackgroundFarTypeId(FlatBufferBuilder builder, long backgroundFarTypeId) { builder.addLong(0, backgroundFarTypeId, 0L); }
  public static void addBackgroundNearTypeId(FlatBufferBuilder builder, long backgroundNearTypeId) { builder.addLong(1, backgroundNearTypeId, 0L); }
  public static void addEvSpe(FlatBufferBuilder builder, int evSpe) { builder.addByte(2, (byte)evSpe, (byte)0); }
  public static void addEvAtk(FlatBufferBuilder builder, int evAtk) { builder.addByte(3, (byte)evAtk, (byte)0); }
  public static void addEvDef(FlatBufferBuilder builder, int evDef) { builder.addByte(4, (byte)evDef, (byte)0); }
  public static void addEvHp(FlatBufferBuilder builder, int evHp) { builder.addByte(5, (byte)evHp, (byte)0); }
  public static void addEvSpa(FlatBufferBuilder builder, int evSpa) { builder.addByte(6, (byte)evSpa, (byte)0); }
  public static void addEvSpd(FlatBufferBuilder builder, int evSpd) { builder.addByte(7, (byte)evSpd, (byte)0); }
  public static void addForm(FlatBufferBuilder builder, int form) { builder.addByte(8, (byte)form, (byte)0); }
  public static void addDynamaxLevel(FlatBufferBuilder builder, int dynamaxLevel) { builder.addByte(9, (byte)dynamaxLevel, (byte)0); }
  public static void addField0a(FlatBufferBuilder builder, int field0a) { builder.addInt(10, field0a, 0); }
  public static void addEncounterId(FlatBufferBuilder builder, long encounterId) { builder.addLong(11, encounterId, 0L); }
  public static void addField0c(FlatBufferBuilder builder, int field0c) { builder.addByte(12, (byte)field0c, (byte)0); }
  public static void addGmaxFactor(FlatBufferBuilder builder, boolean gmaxFactor) { builder.addBoolean(13, gmaxFactor, false); }
  public static void addHeldItem(FlatBufferBuilder builder, int heldItem) { builder.addInt(14, heldItem, 0); }
  public static void addLevel(FlatBufferBuilder builder, int level) { builder.addByte(15, (byte)level, (byte)0); }
  public static void addEncounterScenario(FlatBufferBuilder builder, int encounterScenario) { builder.addInt(16, encounterScenario, 0); }
  public static void addSpecies(FlatBufferBuilder builder, int species) { builder.addInt(17, species, 0); }
  public static void addShinyLock(FlatBufferBuilder builder, long shinyLock) { builder.addInt(18, (int)shinyLock, (int)0L); }
  public static void addNature(FlatBufferBuilder builder, long nature) { builder.addInt(19, (int)nature, (int)0L); }
  public static void addGender(FlatBufferBuilder builder, int gender) { builder.addByte(20, (byte)gender, (byte)0); }
  public static void addIvSpe(FlatBufferBuilder builder, byte ivSpe) { builder.addByte(21, ivSpe, 0); }
  public static void addIvAtk(FlatBufferBuilder builder, byte ivAtk) { builder.addByte(22, ivAtk, 0); }
  public static void addIvDef(FlatBufferBuilder builder, byte ivDef) { builder.addByte(23, ivDef, 0); }
  public static void addIvHp(FlatBufferBuilder builder, byte ivHp) { builder.addByte(24, ivHp, 0); }
  public static void addIvSpa(FlatBufferBuilder builder, byte ivSpa) { builder.addByte(25, ivSpa, 0); }
  public static void addIvSpd(FlatBufferBuilder builder, byte ivSpd) { builder.addByte(26, ivSpd, 0); }
  public static void addAbility(FlatBufferBuilder builder, int ability) { builder.addInt(27, ability, 0); }
  public static void addMove0(FlatBufferBuilder builder, int move0) { builder.addInt(28, move0, 0); }
  public static void addMove1(FlatBufferBuilder builder, int move1) { builder.addInt(29, move1, 0); }
  public static void addMove2(FlatBufferBuilder builder, int move2) { builder.addInt(30, move2, 0); }
  public static void addMove3(FlatBufferBuilder builder, int move3) { builder.addInt(31, move3, 0); }
  public static int endSwShStaticEncounter(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SwShStaticEncounter get(int j) { return get(new SwShStaticEncounter(), j); }
    public SwShStaticEncounter get(SwShStaticEncounter obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}
