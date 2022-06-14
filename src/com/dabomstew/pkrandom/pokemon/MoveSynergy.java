package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  MoveSynergy.java - synergies between moves, or between                --*/
/*--                     abilities/stats and moves                          --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Moves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveSynergy {

    public static List<Move> getSoftAbilityMoveSynergy(int ability, List<Move> moveList, Type pkType1, Type pkType2) {
        List<Integer> synergisticMoves = new ArrayList<>();

        switch(ability) {
            case Abilities.drizzle:
            case Abilities.primordialSea:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.WATER && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.drought:
            case Abilities.desolateLand:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.FIRE && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.refrigerate:
                if (pkType1 == Type.ICE || pkType2 == Type.ICE) {
                    synergisticMoves.addAll(moveList
                            .stream()
                            .filter(mv -> mv.type == Type.NORMAL && mv.category != MoveCategory.STATUS)
                            .map(mv -> mv.number)
                            .collect(Collectors.toList()));
                }
                break;
            case Abilities.galeWings:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.FLYING)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.pixilate:
                if (pkType1 == Type.FAIRY || pkType2 == Type.FAIRY) {
                    synergisticMoves.addAll(moveList
                            .stream()
                            .filter(mv -> mv.type == Type.NORMAL && mv.category != MoveCategory.STATUS)
                            .map(mv -> mv.number)
                            .collect(Collectors.toList()));
                }
                break;
            case Abilities.aerilate:
                if (pkType1 == Type.FLYING || pkType2 == Type.FLYING) {
                    synergisticMoves.addAll(moveList
                            .stream()
                            .filter(mv -> mv.type == Type.NORMAL && mv.category != MoveCategory.STATUS)
                            .map(mv -> mv.number)
                            .collect(Collectors.toList()));
                }
                break;
            case Abilities.darkAura:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.DARK && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.fairyAura:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.FAIRY && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.steelworker:
            case Abilities.steelySpirit:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.STEEL && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.galvanize:
                if (pkType1 == Type.ELECTRIC || pkType2 == Type.ELECTRIC) {
                    synergisticMoves.addAll(moveList
                            .stream()
                            .filter(mv -> mv.type == Type.NORMAL && mv.category != MoveCategory.STATUS)
                            .map(mv -> mv.number)
                            .collect(Collectors.toList()));
                }
                break;
            case Abilities.electricSurge:
            case Abilities.transistor:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.ELECTRIC && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.psychicSurge:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.PSYCHIC && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.grassySurge:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.GRASS && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.dragonsMaw:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.DRAGON && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }

        return moveList
                .stream()
                .filter(mv -> synergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getSoftAbilityMoveAntiSynergy(int ability, List<Move> moveList) {
        List<Integer> antiSynergisticMoves = new ArrayList<>();

        switch(ability) {
            case Abilities.drizzle:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.FIRE && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.drought:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.WATER && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.mistySurge:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.DRAGON && mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }

        return moveList
                .stream()
                .filter(mv -> antiSynergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getHardAbilityMoveSynergy(int ability, Type pkType1, Type pkType2, List<Move> moveList,
                                                       int generation, int perfectAccuracy) {
        List<Integer> synergisticMoves = new ArrayList<>();

        switch(ability) {
            case Abilities.drizzle:
            case Abilities.primordialSea:
                synergisticMoves.add(Moves.thunder);
                synergisticMoves.add(Moves.hurricane);
                if (pkType1 == Type.WATER || pkType2 == Type.WATER) {
                    synergisticMoves.add(Moves.weatherBall);
                }
                break;
            case Abilities.speedBoost:
                synergisticMoves.add(Moves.batonPass);
                synergisticMoves.add(Moves.storedPower);
                synergisticMoves.add(Moves.powerTrip);
                break;
            case Abilities.sturdy:
                if (generation >= 5) {
                    synergisticMoves.add(Moves.endeavor);
                    synergisticMoves.add(Moves.counter);
                    synergisticMoves.add(Moves.mirrorCoat);
                    synergisticMoves.add(Moves.flail);
                    synergisticMoves.add(Moves.reversal);
                }
                break;
            case Abilities.sandVeil:
            case Abilities.sandRush:
                synergisticMoves.add(Moves.sandstorm);
                break;
            case Abilities.staticTheAbilityNotTheKeyword:
                synergisticMoves.add(Moves.smellingSalts);
                synergisticMoves.add(Moves.hex);
                break;
            case Abilities.compoundEyes:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.hitratio > 0 && mv.hitratio <= 80)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.ownTempo:
            case Abilities.tangledFeet:
                synergisticMoves.add(Moves.petalDance);
                synergisticMoves.add(Moves.thrash);
                synergisticMoves.add(Moves.outrage);
                break;
            case Abilities.shadowTag:
            case Abilities.arenaTrap:
                synergisticMoves.add(Moves.perishSong);
                break;
            case Abilities.poisonPoint:
                synergisticMoves.add(Moves.venoshock);
                // fallthrough
            case Abilities.effectSpore:
            case Abilities.flameBody:
                synergisticMoves.add(Moves.hex);
                break;
            case Abilities.sereneGrace:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> ((mv.statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET ||
                                mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER) &&
                                mv.statChanges[0].percentChance < 100) ||
                                (mv.statusMoveType == StatusMoveType.DAMAGE && mv.statusPercentChance < 100) ||
                        mv.flinchPercentChance > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.swiftSwim:
            case Abilities.rainDish:
            case Abilities.drySkin:
            case Abilities.hydration:
                synergisticMoves.add(Moves.rainDance);
                break;
            case Abilities.chlorophyll:
            case Abilities.harvest:
            case Abilities.leafGuard:
                synergisticMoves.add(Moves.sunnyDay);
                break;
            case Abilities.soundproof:
                synergisticMoves.add(Moves.perishSong);
                break;
            case Abilities.sandStream:
                if (pkType1 == Type.ROCK || pkType2 == Type.ROCK) {
                    synergisticMoves.add(Moves.weatherBall);
                }
                break;
            case Abilities.earlyBird:
            case Abilities.shedSkin:
                synergisticMoves.add(Moves.rest);
                break;
            case Abilities.truant:
                synergisticMoves.add(Moves.transform);
                break;
            case Abilities.hustle:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                                mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                mv.hasSpecificStatChange(StatChangeType.ACCURACY, true))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.PHYSICAL && mv.hitratio == perfectAccuracy)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.guts:
                synergisticMoves.add(Moves.facade);
                break;
            case Abilities.rockHead:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.recoilPercent > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.drought:
            case Abilities.desolateLand:
                synergisticMoves.add(Moves.solarBeam);
                synergisticMoves.add(Moves.solarBlade);
                synergisticMoves.add(Moves.morningSun);
                synergisticMoves.add(Moves.synthesis);
                synergisticMoves.add(Moves.moonlight);
                if (generation >= 5) {
                    synergisticMoves.add(Moves.growth);
                }
                if (pkType1 == Type.FIRE || pkType2 == Type.FIRE) {
                    synergisticMoves.add(Moves.weatherBall);
                }
                break;
            case Abilities.ironFist:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.isPunchMove)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.snowCloak:
            case Abilities.iceBody:
            case Abilities.slushRush:
                synergisticMoves.add(Moves.hail);
                break;
            case Abilities.unburden:
                synergisticMoves.add(Moves.fling);
                synergisticMoves.add(Moves.acrobatics);
                break;
            case Abilities.simple:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                                mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                mv.statChanges[0].stages > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.acupressure);
                break;
            case Abilities.adaptability:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category != MoveCategory.STATUS && (mv.type == pkType1 || mv.type == pkType2))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.skillLink:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.hitCount >= 3)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.sniper:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.criticalChance == CriticalChance.INCREASED ||
                                mv.criticalChance == CriticalChance.GUARANTEED)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.magicGuard:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.recoilPercent > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.mindBlown);
                break;
            case Abilities.stall:
                synergisticMoves.add(Moves.metalBurst);
                synergisticMoves.add(Moves.payback);
                break;
            case Abilities.superLuck:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.criticalChance == CriticalChance.INCREASED)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.analytic:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.power > 0 && mv.priority < 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.noGuard:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.hitratio > 0 && mv.hitratio <= 70)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.technician:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.power >= 40 && mv.power <= 60) || mv.hitCount > 1)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.slowStart:
                synergisticMoves.add(Moves.transform);
                synergisticMoves.add(Moves.protect);
                synergisticMoves.add(Moves.detect);
                synergisticMoves.add(Moves.kingsShield);
                synergisticMoves.add(Moves.banefulBunker);
                synergisticMoves.add(Moves.fly);
                synergisticMoves.add(Moves.dig);
                synergisticMoves.add(Moves.bounce);
                synergisticMoves.add(Moves.dive);
                break;
            case Abilities.snowWarning:
                synergisticMoves.add(Moves.auroraVeil);
                synergisticMoves.add(Moves.blizzard);
                if (pkType1 == Type.ICE || pkType2 == Type.ICE) {
                    synergisticMoves.add(Moves.weatherBall);
                }
                break;
            case Abilities.reckless:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.recoilPercent > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.jumpKick);
                synergisticMoves.add(Moves.highJumpKick);
                break;
            case Abilities.badDreams:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusType == StatusType.SLEEP)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.sheerForce:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET ||
                                (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER &&
                                        mv.statChanges[0].stages > 0) ||
                                mv.statusMoveType == StatusMoveType.DAMAGE ||
                                mv.flinchPercentChance > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.contrary:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER &&
                                mv.statChanges[0].stages < 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.heavyMetal:
                synergisticMoves.add(Moves.heatCrash);
                synergisticMoves.add(Moves.heavySlam);
                break;
            case Abilities.moody:
                synergisticMoves.add(Moves.storedPower);
                synergisticMoves.add(Moves.powerTrip);
                break;
            case Abilities.poisonTouch:
            case Abilities.toughClaws:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.makesContact)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.regenerator:
                synergisticMoves.add(Moves.uTurn);
                synergisticMoves.add(Moves.voltSwitch);
                synergisticMoves.add(Moves.partingShot);
                break;
            case Abilities.prankster:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusMoveType == StatusMoveType.NO_DAMAGE)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.destinyBond);
                synergisticMoves.add(Moves.encore);
                synergisticMoves.add(Moves.reflect);
                synergisticMoves.add(Moves.lightScreen);
                synergisticMoves.add(Moves.grudge);
                synergisticMoves.add(Moves.painSplit);
                synergisticMoves.add(Moves.substitute);
                break;
            case Abilities.strongJaw:
                synergisticMoves.add(Moves.bite);
                synergisticMoves.add(Moves.crunch);
                synergisticMoves.add(Moves.fireFang);
                synergisticMoves.add(Moves.fishiousRend);
                synergisticMoves.add(Moves.hyperFang);
                synergisticMoves.add(Moves.iceFang);
                synergisticMoves.add(Moves.jawLock);
                synergisticMoves.add(Moves.poisonFang);
                synergisticMoves.add(Moves.psychicFangs);
                synergisticMoves.add(Moves.thunderFang);
                break;
            case Abilities.megaLauncher:
                synergisticMoves.add(Moves.auraSphere);
                synergisticMoves.add(Moves.darkPulse);
                synergisticMoves.add(Moves.dragonPulse);
                synergisticMoves.add(Moves.originPulse);
                synergisticMoves.add(Moves.terrainPulse);
                synergisticMoves.add(Moves.waterPulse);
                break;
            case Abilities.wimpOut:
            case Abilities.emergencyExit:
                synergisticMoves.add(Moves.fakeOut);
                synergisticMoves.add(Moves.firstImpression);
                break;
            case Abilities.merciless:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusType == StatusType.POISON || mv.statusType == StatusType.TOXIC_POISON)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.banefulBunker);
                break;
            case Abilities.liquidVoice:
                if (pkType1 == Type.WATER || pkType2 == Type.WATER) {
                    synergisticMoves.addAll(moveList
                            .stream()
                            .filter(mv -> mv.isSoundMove && mv.power > 0)
                            .map(mv -> mv.number)
                            .collect(Collectors.toList()));
                }
                break;
            case Abilities.triage:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.absorbPercent > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.surgeSurfer:
                synergisticMoves.add(Moves.electricTerrain);
                break;
            case Abilities.corrosion:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.STATUS &&
                                (mv.statusType == StatusType.POISON || mv.statusType == StatusType.TOXIC_POISON))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }

        return moveList
                .stream()
                .filter(mv -> synergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getHardAbilityMoveAntiSynergy(int ability, List<Move> moveList) {
        List<Integer> antiSynergisticMoves = new ArrayList<>();

        switch(ability) {
            case Abilities.primordialSea:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.FIRE &&
                                mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                // fallthrough
            case Abilities.drizzle:
            case Abilities.sandStream:
            case Abilities.snowWarning:
                antiSynergisticMoves.add(Moves.solarBeam);
                antiSynergisticMoves.add(Moves.solarBlade);
                antiSynergisticMoves.add(Moves.morningSun);
                antiSynergisticMoves.add(Moves.synthesis);
                antiSynergisticMoves.add(Moves.moonlight);
                antiSynergisticMoves.add(Moves.rainDance);
                antiSynergisticMoves.add(Moves.sunnyDay);
                antiSynergisticMoves.add(Moves.hail);
                antiSynergisticMoves.add(Moves.sandstorm);
                break;
            case Abilities.speedBoost:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                mv.statChanges[0].type == StatChangeType.SPEED &&
                                mv.statChanges[0].stages > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                antiSynergisticMoves.add(Moves.psychUp);
                antiSynergisticMoves.add(Moves.haze);
                break;
            case Abilities.desolateLand:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.type == Type.WATER &&
                                mv.category != MoveCategory.STATUS)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                // fallthrough
            case Abilities.drought:
                antiSynergisticMoves.add(Moves.thunder);
                antiSynergisticMoves.add(Moves.hurricane);
                antiSynergisticMoves.add(Moves.rainDance);
                antiSynergisticMoves.add(Moves.sunnyDay);
                antiSynergisticMoves.add(Moves.hail);
                antiSynergisticMoves.add(Moves.sandstorm);
                break;
            case Abilities.noGuard:
                antiSynergisticMoves.add(Moves.lockOn);
                antiSynergisticMoves.add(Moves.mindReader);
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.hasSpecificStatChange(StatChangeType.ACCURACY, true) ||
                                mv.hasSpecificStatChange(StatChangeType.EVASION, true) ||
                                mv.hasSpecificStatChange(StatChangeType.EVASION, false))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.damp:
                antiSynergisticMoves.add(Moves.selfDestruct);
                antiSynergisticMoves.add(Moves.explosion);
                antiSynergisticMoves.add(Moves.mindBlown);
                antiSynergisticMoves.add(Moves.mistyExplosion);
                break;
            case Abilities.insomnia:
            case Abilities.vitalSpirit:
            case Abilities.comatose:
            case Abilities.sweetVeil:
                antiSynergisticMoves.add(Moves.rest);
                break;
            case Abilities.airLock:
            case Abilities.cloudNine:
            case Abilities.deltaStream:
                antiSynergisticMoves.add(Moves.rainDance);
                antiSynergisticMoves.add(Moves.sunnyDay);
                antiSynergisticMoves.add(Moves.sandstorm);
                antiSynergisticMoves.add(Moves.hail);
                break;
            case Abilities.simple:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                                mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                mv.statChanges[0].stages < 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.contrary:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                                mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                mv.statChanges[0].stages > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                antiSynergisticMoves.add(Moves.shellSmash);
                break;
            case Abilities.lightMetal:
                antiSynergisticMoves.add(Moves.heatCrash);
                antiSynergisticMoves.add(Moves.heavySlam);
                break;
            case Abilities.electricSurge:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.category == MoveCategory.STATUS && mv.statusType == StatusType.SLEEP))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                antiSynergisticMoves.add(Moves.rest);
                break;
            case Abilities.psychicSurge:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.priority > 0))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Abilities.mistySurge:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.category == MoveCategory.STATUS &&
                                (mv.statusType == StatusType.BURN ||
                                mv.statusType == StatusType.FREEZE ||
                                mv.statusType == StatusType.PARALYZE ||
                                mv.statusType == StatusType.SLEEP ||
                                mv.statusType == StatusType.POISON ||
                                mv.statusType == StatusType.TOXIC_POISON)))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                antiSynergisticMoves.add(Moves.rest);
                break;
            case Abilities.grassySurge:
                antiSynergisticMoves.add(Moves.earthquake);
                antiSynergisticMoves.add(Moves.magnitude);
                antiSynergisticMoves.add(Moves.bulldoze);
                break;


        }

        return moveList
                .stream()
                .filter(mv -> antiSynergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getStatMoveSynergy(Pokemon pk, List<Move> moveList) {
        List<Integer> synergisticMoves = new ArrayList<>();

        if ((double)pk.hp / (double)pk.bst() < 1.0/8) {
            synergisticMoves.add(Moves.painSplit);
            synergisticMoves.add(Moves.endeavor);
        }

        if ((double)pk.hp / (double)pk.bst() >= 1.0/4) {
            synergisticMoves.add(Moves.waterSpout);
            synergisticMoves.add(Moves.eruption);
            synergisticMoves.add(Moves.counter);
            synergisticMoves.add(Moves.mirrorCoat);
        }

        if (pk.attack * 2 < pk.defense) {
            synergisticMoves.add(Moves.powerTrick);
        }

        if ((double)(pk.attack + pk.spatk) / (double)pk.bst() < 1.0/4) {
            synergisticMoves.add(Moves.powerSplit);
        }

        if ((double)(pk.defense + pk.spdef) / (double)pk.bst() < 1.0/4) {
            synergisticMoves.add(Moves.guardSplit);
        }

        if ((double)pk.speed / (double)pk.bst() < 1.0/8) {
            synergisticMoves.add(Moves.gyroBall);
        }

        if ((double)pk.speed / (double)pk.bst() >= 1.0/4) {
            synergisticMoves.add(Moves.electroBall);
        }

        return moveList
                .stream()
                .filter(mv -> synergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getStatMoveAntiSynergy(Pokemon pk, List<Move> moveList) {
        List<Integer> antiSynergisticMoves = new ArrayList<>();

        if ((double)pk.hp / (double)pk.bst() >= 1.0/4) {
            antiSynergisticMoves.add(Moves.painSplit);
            antiSynergisticMoves.add(Moves.endeavor);
        }

        if (pk.defense * 2 < pk.attack) {
            antiSynergisticMoves.add(Moves.powerTrick);
        }

        if ((double)(pk.attack + pk.spatk) / (double)pk.bst() >= 1.0/3) {
            antiSynergisticMoves.add(Moves.powerSplit);
        }

        if ((double)(pk.defense + pk.spdef) / (double)pk.bst() >= 1.0/3) {
            antiSynergisticMoves.add(Moves.guardSplit);
        }

        if ((double)pk.speed / (double)pk.bst() >= 1.0/4) {
            antiSynergisticMoves.add(Moves.gyroBall);
        }

        if ((double)pk.speed / (double)pk.bst() < 1.0/8) {
            antiSynergisticMoves.add(Moves.electroBall);
        }

        return moveList
                .stream()
                .filter(mv -> antiSynergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getMoveSynergy(Move mv1, List<Move> moveList, int generation) {
        List<Integer> synergisticMoves = new ArrayList<>();

        if ((mv1.statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET &&
                mv1.hasSpecificStatChange(StatChangeType.SPEED, false)) ||
                ((mv1.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                        mv1.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                        mv1.hasSpecificStatChange(StatChangeType.SPEED, true))) {
            synergisticMoves.addAll(moveList
                    .stream()
                    .filter(mv -> mv.flinchPercentChance > 0 && mv.priority == 0)
                    .map(mv -> mv.number)
                    .collect(Collectors.toList()));
        }

        if (mv1.flinchPercentChance > 0 && mv1.priority == 0) {
            synergisticMoves.addAll(moveList
                    .stream()
                    .filter(mv -> (mv.statChangeMoveType == StatChangeMoveType.DAMAGE_TARGET &&
                            mv.hasSpecificStatChange(StatChangeType.SPEED, false)) ||
                            ((mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER ||
                                    mv.statChangeMoveType == StatChangeMoveType.NO_DAMAGE_USER) &&
                                    mv.hasSpecificStatChange(StatChangeType.SPEED, true)))
                    .map(mv -> mv.number)
                    .collect(Collectors.toList()));
        }

        if (mv1.statChanges[0].stages >= 2 || mv1.statChanges[1].type != StatChangeType.NONE) {
            synergisticMoves.add(Moves.batonPass);
            synergisticMoves.add(Moves.storedPower);
            synergisticMoves.add(Moves.powerTrip);
        }

        if (mv1.statusType == StatusType.SLEEP) {
            synergisticMoves.add(Moves.dreamEater);
            synergisticMoves.add(Moves.nightmare);
            synergisticMoves.add(Moves.hex);
        }

        switch(mv1.number) {
            case Moves.toxic:
                synergisticMoves.add(Moves.protect);
                synergisticMoves.add(Moves.detect);
                synergisticMoves.add(Moves.kingsShield);
                synergisticMoves.add(Moves.dig);
                synergisticMoves.add(Moves.fly);
                synergisticMoves.add(Moves.bounce);
                synergisticMoves.add(Moves.dive);
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.isTrapMove))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                // fallthrough
            case Moves.poisonPowder:
            case Moves.poisonGas:
            case Moves.banefulBunker:
            case Moves.toxicThread:
                synergisticMoves.add(Moves.venoshock);
                synergisticMoves.add(Moves.hex);
                break;
            case Moves.venoshock:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.STATUS &&
                                (mv.statusType == StatusType.POISON || mv.statusType == StatusType.TOXIC_POISON))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.protect:
            case Moves.detect:
            case Moves.kingsShield:
                synergisticMoves.add(Moves.toxic);
                synergisticMoves.add(Moves.leechSeed);
                synergisticMoves.add(Moves.willOWisp);
                break;
            case Moves.batonPass:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statChanges[0].stages >= 2 || mv.statChanges[1].type != StatChangeType.NONE)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.shellSmash);
                break;
            case Moves.willOWisp:
                synergisticMoves.add(Moves.hex);
                break;
            case Moves.lockOn:
            case Moves.mindReader:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.hitratio <= 50))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.sunnyDay:
                synergisticMoves.add(Moves.solarBlade);
                synergisticMoves.add(Moves.solarBeam);
                break;
            case Moves.rainDance:
                synergisticMoves.add(Moves.thunder);
                synergisticMoves.add(Moves.hurricane);
                break;
            case Moves.powerSwap:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statChangeMoveType == StatChangeMoveType.DAMAGE_USER &&
                                (mv.hasSpecificStatChange(StatChangeType.ATTACK, false) ||
                                        mv.hasSpecificStatChange(StatChangeType.SPECIAL_ATTACK, false)))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.endure:
                synergisticMoves.add(Moves.reversal);
                synergisticMoves.add(Moves.flail);
                synergisticMoves.add(Moves.endeavor);
                break;
            case Moves.endeavor:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category != MoveCategory.STATUS && mv.priority > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.thunderWave:
            case Moves.glare:
            case Moves.stunSpore:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.flinchPercentChance > 0)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.STATUS && mv.statusType == StatusType.CONFUSION)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.hex);
                break;
            case Moves.hail:
                synergisticMoves.add(Moves.blizzard);
                synergisticMoves.add(Moves.auroraVeil);
                break;
            case Moves.stockpile:
                synergisticMoves.add(Moves.spitUp);
                synergisticMoves.add(Moves.swallow);
                break;
            case Moves.spitUp:
                synergisticMoves.add(Moves.stockpile);
                break;
            case Moves.swallow:
                synergisticMoves.add(Moves.stockpile);
                break;
            case Moves.leechSeed:
            case Moves.perishSong:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.isTrapMove))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.spikes:
            case Moves.stealthRock:
            case Moves.toxicSpikes:
                synergisticMoves.add(Moves.roar);
                synergisticMoves.add(Moves.whirlwind);
                synergisticMoves.add(Moves.dragonTail);
                synergisticMoves.add(Moves.circleThrow);
                break;
            case Moves.rest:
                synergisticMoves.add(Moves.sleepTalk);
                break;
            case Moves.focusEnergy:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.criticalChance == CriticalChance.INCREASED)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.focusPunch:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusMoveType == StatusMoveType.NO_DAMAGE &&
                                mv.statusType == StatusType.SLEEP)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.torment:
                synergisticMoves.add(Moves.encore);
                break;
            case Moves.encore:
                synergisticMoves.add(Moves.torment);
                break;
            case Moves.hex:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusMoveType == StatusMoveType.NO_DAMAGE &&
                                mv.statusType != StatusType.CONFUSION)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.banefulBunker);
                break;
            case Moves.dreamEater:
            case Moves.nightmare:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statusMoveType == StatusMoveType.NO_DAMAGE &&
                                mv.statusType == StatusType.SLEEP)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.storedPower:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.statChanges[0].stages > 1 || mv.statChanges[1].type != StatChangeType.NONE)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                synergisticMoves.add(Moves.acupressure);
                synergisticMoves.add(Moves.shellSmash);
                break;
            case Moves.swagger:
                synergisticMoves.add(Moves.punishment);
                break;
            case Moves.punishment:
                synergisticMoves.add(Moves.swagger);
                break;
            case Moves.shellSmash:
                synergisticMoves.add(Moves.storedPower);
                break;
        }

        return moveList
                .stream()
                .filter(mv -> synergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getSoftMoveSynergy(Move mv1, List<Move> moveList, int generation,
                                                boolean effectivenessUpdated) {
        List<Integer> synergisticMoves = new ArrayList<>();

        if (mv1.category != MoveCategory.STATUS) {
            List<Type> notVeryEffective = Effectiveness.notVeryEffective(mv1.type, generation, effectivenessUpdated);
            for (Type nveType: notVeryEffective) {
                List<Type> superEffectiveAgainstNVE =
                        Effectiveness.against(nveType, null, generation, effectivenessUpdated)
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue() == Effectiveness.DOUBLE)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category != MoveCategory.STATUS &&
                                superEffectiveAgainstNVE.contains(mv.type))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
            }
        }

        switch(mv1.number) {
            case Moves.swordsDance:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.PHYSICAL)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.nastyPlot:
            case Moves.tailGlow:
                synergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.SPECIAL)
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }

        return moveList
                .stream()
                .filter(mv -> synergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getHardMoveAntiSynergy(Move mv1, List<Move> moveList) {
        List<Integer> antiSynergisticMoves = new ArrayList<>();


        if (mv1.category == MoveCategory.STATUS && mv1.statusType != StatusType.NONE) {
            antiSynergisticMoves.addAll(moveList
                    .stream()
                    .filter(mv -> (mv.category == MoveCategory.STATUS && mv.statusType != StatusType.NONE &&
                            (mv.statusType == mv1.statusType ||
                                    (mv1.statusType != StatusType.CONFUSION && mv.statusType != StatusType.CONFUSION))))
                    .map(mv -> mv.number)
                    .collect(Collectors.toList()));
        }

        switch(mv1.number) {
            case Moves.protect:
                antiSynergisticMoves.add(Moves.detect);
                antiSynergisticMoves.add(Moves.banefulBunker);
                antiSynergisticMoves.add(Moves.kingsShield);
                break;
            case Moves.detect:
                antiSynergisticMoves.add(Moves.protect);
                antiSynergisticMoves.add(Moves.banefulBunker);
                antiSynergisticMoves.add(Moves.kingsShield);
                break;
            case Moves.kingsShield:
                antiSynergisticMoves.add(Moves.protect);
                antiSynergisticMoves.add(Moves.detect);
                antiSynergisticMoves.add(Moves.banefulBunker);
                break;
            case Moves.banefulBunker:
                antiSynergisticMoves.add(Moves.protect);
                antiSynergisticMoves.add(Moves.detect);
                antiSynergisticMoves.add(Moves.kingsShield);
                break;
            case Moves.returnTheMoveNotTheKeyword:
                antiSynergisticMoves.add(Moves.frustration);
                break;
            case Moves.frustration:
                antiSynergisticMoves.add(Moves.returnTheMoveNotTheKeyword);
                break;
        }

        if (mv1.type != null) {
            switch (mv1.type) {
                case FIRE:
                    if (mv1.category != MoveCategory.STATUS) {
                        antiSynergisticMoves.add(Moves.waterSport);
                    }
                    break;
                case ELECTRIC:
                    if (mv1.category != MoveCategory.STATUS) {
                        antiSynergisticMoves.add(Moves.mudSport);
                    }
                    break;
            }
        }

        return moveList
                .stream()
                .filter(mv -> antiSynergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> getSoftMoveAntiSynergy(Move mv1, List<Move> moveList) {
        List<Integer> antiSynergisticMoves = new ArrayList<>();


        if (mv1.category != MoveCategory.STATUS) {
            antiSynergisticMoves.addAll(moveList
                    .stream()
                    .filter(mv -> (mv.category != MoveCategory.STATUS && mv.type == mv1.type))
                    .map(mv -> mv.number)
                    .collect(Collectors.toList()));
        }

        switch (mv1.number) {
            case Moves.waterSport:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.category != MoveCategory.STATUS && mv.type == Type.FIRE))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
            case Moves.mudSport:
                antiSynergisticMoves.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.category != MoveCategory.STATUS && mv.type == Type.ELECTRIC))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }

        return moveList
                .stream()
                .filter(mv -> antiSynergisticMoves.contains(mv.number))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Move> requiresOtherMove(Move mv1, List<Move> moveList) {
        List<Integer> requiresMove = new ArrayList<>();
        switch(mv1.number) {
            case Moves.spitUp:
            case Moves.swallow:
                requiresMove.add(Moves.stockpile);
                break;
            case Moves.dreamEater:
            case Moves.nightmare:
                requiresMove.addAll(moveList
                        .stream()
                        .filter(mv -> (mv.category == MoveCategory.STATUS && mv.statusType == StatusType.SLEEP))
                        .map(mv -> mv.number)
                        .collect(Collectors.toList()));
                break;
        }
        return moveList.stream().filter(mv -> requiresMove.contains(mv.number)).distinct().collect(Collectors.toList());
    }
}
