package src.main;

import java.util.Random;
import src.entity.Entity;
import src.entity.Player;

public class BattleManager {

    GamePanel gp;
    Random randomGen;
    public int monsterIndex = -1;
    int battleState;
    boolean rolling;
    int diceCounter;
    int pauseCounter;

    private String lastPlayerActionInfo = "";
    private String lastMonsterAttackInfo = "";
    private int messageTimer = 0;
    private final int MESSAGE_DURATION = 90;

    public static final int HEAL_MANA_COST = 10;
    private final int HEAL_BASE_AMOUNT = 30;
    public static final int STRONG_ATTACK_MANA_COST = 15;
    private final double STRONG_ATTACK_MULTIPLIER = 1.8;

    public BattleManager(GamePanel gp) {
        this.gp = gp;
        randomGen = new Random();
    }

    void setBattle() {
        if (gp == null || gp.player == null) return;
        gp.player.changeFacing();
        gp.player.forcedMove = true;
        gp.player.gameState = gp.player.BATTLE;
        battleState = 0;
        rolling = false;
        diceCounter = 0;
        pauseCounter = 0;
        clearMessages();
    }

    void endBattleNormalMonster() {
        if (gp == null || gp.player == null || gp.monstersM == null || gp.monstersM.monsters == null) return;

        if (monsterIndex >= 0 && monsterIndex < gp.monstersM.monsters.length && gp.monstersM.monsters[monsterIndex] != null) {
            gp.player.gainExperience(gp.monstersM.monsters[monsterIndex].experienceDropped);
        }
        gp.player.gameState = gp.player.MOVE;
        gp.player.movingState = 0;
        if (monsterIndex >= 0 && monsterIndex < gp.monstersM.monsters.length && gp.monstersM.monsters[monsterIndex] != null) {
            gp.monstersM.monster_remaning--;
            gp.monstersM.monsters[monsterIndex] = null;
        }
        rolling = false;
        diceCounter = 0;
        battleState = 0;
        gp.player.changeFacing();
        gp.player.forcedMove = true;
        clearMessages();
        monsterIndex = -1;
    }

    private int calculateDamage(Entity attacker, Entity defender, int diceSumForDamage, double extraMultiplier) {
        if (attacker == null || defender == null) return 0;

        int baseDamage = diceSumForDamage;
        if (attacker instanceof Player) {
            baseDamage += ((Player) attacker).strength;
        }
        baseDamage = (int)(baseDamage * extraMultiplier);

        String critMessage = "";
        if (randomGen.nextDouble() < attacker.critChance) {
            baseDamage = (int) (baseDamage * attacker.critDamageMultiplier);
            critMessage = "CRITICAL HIT! ";
        }

        String attackerName = (attacker instanceof Player) ? "Player" : "Monster(" + attacker.symbol + ")";
        if (attacker == gp.player) {
            lastPlayerActionInfo = critMessage + attackerName + " dealt " + baseDamage + " to " + defender.symbol + "!";
        } else {
            lastMonsterAttackInfo = critMessage + attackerName + " dealt " + baseDamage + " to Player!";
        }
        messageTimer = MESSAGE_DURATION;
        return baseDamage;
    }

    public void update() {
        if (gp == null || gp.player == null || gp.monstersM == null || gp.monstersM.monsters == null) return;

        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) {
                clearMessages();
            }
        }

        if(gp.player.gameState == gp.player.MOVE) {
            for(int i = 0; i < gp.monstersM.monsters.length; i++) {
                Entity monster = gp.monstersM.monsters[i];
                if(monster != null &&
                        gp.player.getX() == monster.getX() &&
                        gp.player.getY() == monster.getY()) {
                    monsterIndex = i;
                    monster.visable = true;
                    if(gp.player.direction != null){
                        switch (gp.player.direction){
                            case Entity.UP:    monster.direction = Entity.DOWN;  break;
                            case Entity.DOWN:  monster.direction = Entity.UP;    break;
                            case Entity.LEFT:  monster.direction = Entity.RIGHT; break;
                            case Entity.RIGHT: monster.direction = Entity.LEFT;  break;
                        }
                    } else {
                        monster.direction = Entity.DOWN;
                    }
                    setBattle();
                    break;
                }
            }
        }
        else if(gp.player.gameState == gp.player.BATTLE && monsterIndex >= 0 && monsterIndex < gp.monstersM.monsters.length && gp.monstersM.monsters[monsterIndex] != null) {
            Entity currentMonster = gp.monstersM.monsters[monsterIndex];

            if(rolling) {
                if(gp != null) ;
                if(diceCounter < 50) {
                    if(battleState == 0) {
                        gp.player.dice[0] = randomGen.nextInt(6) + 1;
                        if (currentMonster.dice != null) currentMonster.dice[0] = randomGen.nextInt(6) + 1;
                    } else if(battleState == 11 || battleState == 13 || battleState == 4 || battleState == 3) {
                        gp.player.dice[0] = randomGen.nextInt(6) + 1;
                        gp.player.dice[1] = randomGen.nextInt(6) + 1;
                        if (battleState == 3 && currentMonster.dice != null) {
                            currentMonster.dice[0] = randomGen.nextInt(6) + 1;
                            currentMonster.dice[1] = randomGen.nextInt(6) + 1;
                        }
                    } else if(battleState == 2) {
                        if (currentMonster.dice != null) {
                            currentMonster.dice[0] = randomGen.nextInt(6) + 1;
                            currentMonster.dice[1] = randomGen.nextInt(6) + 1;
                        }
                    }
                    diceCounter++;
                }
                else {
                    rolling = false;
                    diceCounter = 0;
                    gp.player.dice[0] = (gp.player.dice != null && gp.player.dice[0] == 0) ? 1 : gp.player.dice[0];
                    gp.player.dice[1] = (gp.player.dice != null && gp.player.dice[1] == 0) ? 1 : gp.player.dice[1];
                    if(currentMonster != null && currentMonster.dice != null) {
                        currentMonster.dice[0] = (currentMonster.dice[0] == 0) ? 1 : currentMonster.dice[0];
                        currentMonster.dice[1] = (currentMonster.dice[1] == 0) ? 1 : currentMonster.dice[1];
                    }

                    if(battleState == 0) {
                        if(gp.player.dice[0] > currentMonster.dice[0]) battleState = 1;
                        else if(gp.player.dice[0] < currentMonster.dice[0]) battleState = 2;
                        else battleState = 3;
                    }
                    else if(battleState == 11) {
                        if(gp != null) ;
                        int diceSum = gp.player.dice[0]*10 + gp.player.dice[1];
                        if(gp.player.dice[0] == gp.player.dice[1]) diceSum += 100;
                        currentMonster.hit_point -= calculateDamage(gp.player, currentMonster, diceSum, 1.0);
                        if(currentMonster.hit_point <= 0) { currentMonster.hit_point = 0; if (currentMonster.symbol == Entity.KING) {gp.handleZorkDefeat(monsterIndex); return;} else battleState = 4;} else battleState = 2;
                    }
                    else if (battleState == 13) {
                        if(gp != null) gp.playSE(1);
                        int diceSum = gp.player.dice[0]*10 + gp.player.dice[1];
                        if(gp.player.dice[0] == gp.player.dice[1]) diceSum += 100;
                        currentMonster.hit_point -= calculateDamage(gp.player, currentMonster, diceSum, STRONG_ATTACK_MULTIPLIER);
                        if(currentMonster.hit_point <= 0) { currentMonster.hit_point = 0; if (currentMonster.symbol == Entity.KING) {gp.handleZorkDefeat(monsterIndex); return;} else battleState = 4;} else battleState = 2;
                    }
                    else if(battleState == 2) {
                        if(gp != null) ;
                        int diceSum = currentMonster.dice[0]*10 + currentMonster.dice[1];
                        if(currentMonster.symbol == Entity.KING && currentMonster.dice[0] == currentMonster.dice[1]) diceSum += 100;
                        gp.player.hit_point -= calculateDamage(currentMonster, gp.player, diceSum, 1.0);
                        if(gp.player.hit_point <= 0) { gp.player.hit_point = 0; gp.panelState = gp.END; gp.es.setTitleTexts("You Died :(\nSteps: " + gp.player.steps + " Lvl: " + gp.player.level); return;} else battleState = 1;
                    }
                    else if(battleState == 3) {
                        if(gp != null) gp.playSE(1);
                        int pDice = gp.player.dice[0]*10 + gp.player.dice[1]; if(gp.player.dice[0]==gp.player.dice[1]) pDice+=100;
                        int mDice = currentMonster.dice[0]*10 + currentMonster.dice[1]; if(currentMonster.symbol==Entity.KING && currentMonster.dice[0]==currentMonster.dice[1]) mDice+=100;
                        currentMonster.hit_point -= calculateDamage(gp.player, currentMonster, pDice, 1.0);
                        gp.player.hit_point -= calculateDamage(currentMonster, gp.player, mDice, 1.0);
                        if(gp.player.hit_point <= 0) { gp.player.hit_point = 0; gp.panelState = gp.END; gp.es.setTitleTexts("You Died :(\nSteps: " + gp.player.steps + " Lvl: " + gp.player.level); return;}
                        if(currentMonster.hit_point <= 0) { currentMonster.hit_point = 0; if (currentMonster.symbol == Entity.KING) {gp.handleZorkDefeat(monsterIndex); return;} else battleState = 4;} else battleState = 0;
                    }
                    else if(battleState == 4) {
                        int heal = gp.player.dice[0]*10 + gp.player.dice[1];
                        gp.player.hit_point += heal;
                        if (gp.player.hit_point > gp.player.max_hit_point) gp.player.hit_point = gp.player.max_hit_point;
                        lastPlayerActionInfo = "Player healed for " + heal + " HP.";
                        messageTimer = MESSAGE_DURATION;
                    }
                    if (battleState != 4) {
                        if(gp.player.dice != null) { gp.player.dice[0] = 0; gp.player.dice[1] = 0; }
                        if (currentMonster != null && currentMonster.dice != null) {currentMonster.dice[0] = 0; currentMonster.dice[1] = 0;}
                    }
                }
            }
            else {
                if(battleState == 0) {
                    if(gp.keyH.rPressed) { gp.keyH.rPressed = false; rolling = true; clearMessages(); }
                }
                else if (battleState == 1) {
                    if (gp.keyH.rPressed) {
                        gp.keyH.rPressed = false; battleState = 11; rolling = true; clearMessages();
                    } else if (gp.keyH.spell1Pressed) {
                        gp.keyH.spell1Pressed = false;
                        if (gp.player.canCastHeal(HEAL_MANA_COST)) {
                            gp.player.castHeal(HEAL_MANA_COST, HEAL_BASE_AMOUNT);
                            lastPlayerActionInfo = "Player casts Heal! (+ " + (HEAL_BASE_AMOUNT + gp.player.intelligence * 2) + " HP)";
                            messageTimer = MESSAGE_DURATION;
                            battleState = 2;
                        } else {
                            lastPlayerActionInfo = "Not enough mana for Heal!"; messageTimer = MESSAGE_DURATION;
                        }
                        clearMessages(false);
                    } else if (gp.keyH.spell2Pressed) {
                        gp.keyH.spell2Pressed = false;
                        if (gp.player.canCastStrongAttack(STRONG_ATTACK_MANA_COST)) {
                            gp.player.consumeManaForStrongAttack(STRONG_ATTACK_MANA_COST);
                            battleState = 13; rolling = true;
                        } else {
                            lastPlayerActionInfo = "Not enough mana for Strong Attack!"; messageTimer = MESSAGE_DURATION;
                        }
                        clearMessages();
                    }
                }
                else if(battleState == 2) {
                    pauseCounter++;
                    if(pauseCounter >= 60) { pauseCounter = 0; rolling = true; clearMessages(); }
                }
                else if(battleState == 3) {
                    if(gp.keyH.rPressed) { gp.keyH.rPressed = false; rolling = true; clearMessages(); }
                }
                else if(battleState == 4) {
                    boolean diceAlreadyRolledForHeal = (gp.player.dice != null && (gp.player.dice[0] != 0 || gp.player.dice[1] != 0));
                    if (!diceAlreadyRolledForHeal && !rolling) {
                        if(gp.keyH.rPressed) {
                            gp.keyH.rPressed = false; rolling = true; clearMessages(false);
                        }
                    } else if (diceAlreadyRolledForHeal && !rolling) {
                        pauseCounter++;
                        if (pauseCounter >= MESSAGE_DURATION + 20) {
                            pauseCounter = 0;
                            endBattleNormalMonster();
                            if(gp.player.dice != null) { gp.player.dice[0] = 0; gp.player.dice[1] = 0; }
                        }
                    }
                }
            }
        }
    }

    private void clearMessages(boolean clearPlayerToo) {
        if (clearPlayerToo && lastPlayerActionInfo != null) lastPlayerActionInfo = "";
        if (lastMonsterAttackInfo != null) lastMonsterAttackInfo = "";
    }
    private void clearMessages() {
        clearMessages(true);
    }

    public String getLastPlayerActionInfo() { return (messageTimer > 0 && lastPlayerActionInfo != null && !lastPlayerActionInfo.isEmpty()) ? lastPlayerActionInfo : ""; }
    public String getLastMonsterAttackInfo() { return (messageTimer > 0 && lastMonsterAttackInfo != null && !lastMonsterAttackInfo.isEmpty()) ? lastMonsterAttackInfo : ""; }
}