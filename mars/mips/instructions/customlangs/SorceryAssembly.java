package mars.mips.instructions.customlangs;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;


public class SorceryAssembly extends CustomAssembly {
    @Override
    public String getName(){
        return "Sorcery Assembly";
    }

    @Override
    public String getDescription(){
        return "Assembly language to let your computer control Mana and Magic";
    }
    protected void populate() {
        // loadMaxHP: $t0 = imm (load immediate - MUST BE FIRST TO TEST)
        instructionList.add(
                new BasicInstruction("loadMaxHP $t0,-100",
                        "Load Max HP: Load a signed 16-bit immediate directly into $t0.",
                        BasicInstructionFormat.I_FORMAT,
                        "001001 fffff 00000 ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt  = operands[0];
                                int imm = operands[1] << 16 >> 16;
                                RegisterFile.updateRegister(rt, imm);
                            }
                        }
                )
        );

        // charge: rt = rs + imm
        instructionList.add(
                new BasicInstruction("charge $t0,$t1,-100",
                        "Charge: rt = rs + imm.",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt  = operands[0];              // dest
                                int rs  = operands[1];              // source
                                int imm = operands[2] << 16 >> 16;  // sign-extended
                                int sum = RegisterFile.getValue(rs) + imm;
                                RegisterFile.updateRegister(rt, sum);
                                SystemIO.printString("Charge: $" + rt + " = $" + rs + " + " + imm + " (Result: " + sum + ")\n");
                            }
                        })
        );

        // conjure: Add two registers
        instructionList.add(
                new BasicInstruction("conjure $t0,$t1,$t2",
                        "Conjure: Add two registers and store result in the first.",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 100000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int dst = operands[0]; // $t0
                                int src1 = operands[1]; // $t1
                                int src2 = operands[2]; // $t2
                                int result = RegisterFile.getValue(src1) + RegisterFile.getValue(src2);
                                RegisterFile.updateRegister(dst, result);
                                SystemIO.printString("Conjure: $" + dst + " = $" + src1 + " + $" + src2 + " (Result: " + result + ")\n");
                            }
                        }
                )
        );

        // drain: $d = $s - $t
        instructionList.add(
                new BasicInstruction("drain $t0,$t1,$t2",
                        "Drain: Subtract second register from first source and store in destination.",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 100010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int dst = operands[0];
                                int src1 = operands[1];
                                int src2 = operands[2];
                                int result = RegisterFile.getValue(src1) - RegisterFile.getValue(src2);
                                RegisterFile.updateRegister(dst, result);
                                SystemIO.printString("Drain: $" + dst + " = $" + src1 + " - $" + src2 + " (Result: " + result + ")\n");
                            }
                        }
                )
        );

        // amplify: $d = $s * $t
        instructionList.add(
                new BasicInstruction("amplify $t0,$t1,$t2",
                        "Amplify: Multiply two registers and store result in the first.",
                        BasicInstructionFormat.R_FORMAT,
                        "011100 fffff sssss ttttt 00000 000010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int dst = operands[0];
                                int src1 = operands[1];
                                int src2 = operands[2];
                                int result = RegisterFile.getValue(src1) * RegisterFile.getValue(src2);
                                RegisterFile.updateRegister(dst, result);
                                SystemIO.printString("Amplify: $" + dst + " = $" + src1 + " * $" + src2 + " (Result: " + result + ")\n");
                            }
                        }
                )
        );

        // disperse: $d = $s / $t (integer division)
        instructionList.add(
                new BasicInstruction("disperse $t0,$t1,$t2",
                        "Disperse: Integer divide first source by second source and store in destination.",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 011010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int dst = operands[0];
                                int src1 = operands[1];
                                int src2 = operands[2];
                                int divisor = RegisterFile.getValue(src2);

                                if (divisor == 0) {
                                    throw new ProcessingException(statement, "Division by zero");
                                }

                                int result = RegisterFile.getValue(src1) / divisor;
                                RegisterFile.updateRegister(dst, result);
                                SystemIO.printString("Disperse: $" + dst + " = $" + src1 + " / $" + src2 + " (Result: " + result + ")\n");
                            }
                        }
                )
        );

        // drawCrystal: lw-style   drawCrystal $t0,offset($t1)
        instructionList.add(
                new BasicInstruction("drawCrystal $t0,-100($t1)",
                        "Draw Crystal: Load a word from memory into $t0 (like lw).",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt  = operands[0];                 // destination register
                                int base= operands[1];                 // base register
                                int imm = operands[2] << 16 >> 16;     // signed offset
                                int addr = RegisterFile.getValue(base) + imm;
                                try {
                                    int value = Globals.memory.getWord(addr);
                                    RegisterFile.updateRegister(rt, value);
                                    SystemIO.printString("Draw Crystal: Loaded " + value + " from memory[" + addr + "] into $" + rt + "\n");
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }
                )
        );

        // sealCrystal: sw-style   sealCrystal $t0,offset($t1)
        instructionList.add(
                new BasicInstruction("sealCrystal $t0,-100($t1)",
                        "Seal Crystal: Store a word from $t0 into memory (like sw).",
                        BasicInstructionFormat.I_FORMAT,
                        "101011 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt   = operands[0];                 // source register
                                int base = operands[1];                 // base register
                                int imm  = operands[2] << 16 >> 16;     // signed offset
                                int addr = RegisterFile.getValue(base) + imm;
                                try {
                                    int value = RegisterFile.getValue(rt);
                                    Globals.memory.setWord(addr, value);
                                    SystemIO.printString("Seal Crystal: Stored " + value + " from $" + rt + " into memory[" + addr + "]\n");
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }
                )
        );
        // ifEqualCast: beq-style   ifEqualCast $t0,$t1,label
        instructionList.add(
                new BasicInstruction("ifEqualCast $t0,$t1,label",
                        "If Equal Cast: Branch to label if the two registers are equal (like beq).",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int val1 = RegisterFile.getValue(operands[0]);
                                int val2 = RegisterFile.getValue(operands[1]);
                                if (val1 == val2) {
                                    SystemIO.printString("If Equal Cast: $" + operands[0] + " == $" + operands[1] + " (" + val1 + " == " + val2 + ") - Branching!\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                } else {
                                    SystemIO.printString("If Equal Cast: $" + operands[0] + " != $" + operands[1] + " (" + val1 + " != " + val2 + ") - Not branching\n");
                                }
                            }
                        }
                )
        );

        // ifShiftedPath: bne-style   ifShiftedPath $t0,$t1,label
        instructionList.add(
                new BasicInstruction("ifShiftedPath $t0,$t1,label",
                        "If Shifted Path: Branch to label if the two registers are not equal (like bne).",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000101 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int val1 = RegisterFile.getValue(operands[0]);
                                int val2 = RegisterFile.getValue(operands[1]);
                                if (val1 != val2) {
                                    SystemIO.printString("If Shifted Path: $" + operands[0] + " != $" + operands[1] + " (" + val1 + " != " + val2 + ") - Branching!\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                } else {
                                    SystemIO.printString("If Shifted Path: $" + operands[0] + " == $" + operands[1] + " (" + val1 + " == " + val2 + ") - Not branching\n");
                                }
                            }
                        }
                )
        );

        // fireball
        instructionList.add(
                new BasicInstruction("fireball $t0,$t1",
                        "Fireball: consumes 15 mana from caster ($t1) and deals 15 damage to target ($t0).",
                        BasicInstructionFormat.R_FORMAT,
                        "110000 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands   = statement.getOperands();
                                int targetReg    = operands[0];
                                int casterReg    = operands[1];

                                int manaCost = 15;
                                int damage   = 15;

                                int casterMana = RegisterFile.getValue(casterReg);
                                int targetHP   = RegisterFile.getValue(targetReg);

                                if (casterMana < manaCost) {
                                    SystemIO.printString("Fireball FIZZLED! Caster $" + casterReg + " has insufficient mana (" + casterMana + " < " + manaCost + ")\n");
                                    return;
                                }

                                casterMana -= manaCost;
                                targetHP   -= damage;
                                if (targetHP < 0) targetHP = 0;

                                RegisterFile.updateRegister(casterReg, casterMana);
                                RegisterFile.updateRegister(targetReg, targetHP);
                                SystemIO.printString("Fireball casted by $" + casterReg + " on $" + targetReg + "! Dealt " + damage + " damage. Target HP: " + targetHP + "\n");
                            }
                        }
                )
        );

        // manaTrackCrystal
        instructionList.add(
                new BasicInstruction("manaTrackCrystal $t0",
                        "Mana Track Crystal: copy mana value from $t0 into $v0 and print it.",
                        BasicInstructionFormat.R_FORMAT,
                        "110001 00000 00000 fffff 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int manaReg    = operands[0];

                                int mana = RegisterFile.getValue(manaReg);
                                RegisterFile.updateRegister(2, mana);
                                SystemIO.printString("Mana value: " + mana + "\n");
                            }
                        }
                )
        );

        // loadCrystalAddr: load absolute address of a label into a register
        instructionList.add(
                new BasicInstruction("loadCrystalAddr $t0,label",
                        "Load Crystal Address: Load the absolute memory address of a label into $t0.",
                        BasicInstructionFormat.I_FORMAT,
                        "001111 00000 fffff ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt = operands[0];  // destination register

                                // Get the label name from the token list
                                String label = statement.getOriginalTokenList().get(2).getValue();

                                // Look up the label's address in the symbol table
                                int addr = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                                RegisterFile.updateRegister(rt, addr);
                                SystemIO.printString("Load Crystal Address: $" + rt + " = " + addr + " (label: " + label + ")\n");
                            }
                        }
                )
        );

        // potion
        instructionList.add(
                new BasicInstruction("potion $t0,$t1",
                        "Potion: add 25 HP to $t0 and 25 mana to $t1.",
                        BasicInstructionFormat.R_FORMAT,
                        "110010 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int hpReg      = operands[0];
                                int manaReg    = operands[1];

                                int healHP   = 25;
                                int healMana = 25;

                                int hp   = RegisterFile.getValue(hpReg)   + healHP;
                                int mana = RegisterFile.getValue(manaReg) + healMana;

                                RegisterFile.updateRegister(hpReg,   hp);
                                RegisterFile.updateRegister(manaReg, mana);
                                SystemIO.printString("Potion consumed! $" + hpReg + " HP +" + healHP + " (now " + hp + "), $" + manaReg + " Mana +" + healMana + " (now " + mana + ")\n");
                            }
                        }
                )
        );

        // lightning
        instructionList.add(
                new BasicInstruction("lightning $t0,$t1",
                        "Lightning: consumes 20 mana from caster ($t1) and deals 30 damage to target ($t0).",
                        BasicInstructionFormat.R_FORMAT,
                        "110011 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands   = statement.getOperands();
                                int targetReg    = operands[0];
                                int casterReg    = operands[1];

                                int manaCost = 20;
                                int damage   = 30;

                                int casterMana = RegisterFile.getValue(casterReg);
                                int targetHP   = RegisterFile.getValue(targetReg);

                                if (casterMana < manaCost) {
                                    SystemIO.printString("Lightning FIZZLED! Caster $" + casterReg + " has insufficient mana (" + casterMana + " < " + manaCost + ")\n");
                                    return;
                                }

                                casterMana -= manaCost;
                                targetHP   -= damage;
                                if (targetHP < 0) targetHP = 0;

                                RegisterFile.updateRegister(casterReg, casterMana);
                                RegisterFile.updateRegister(targetReg, targetHP);
                                SystemIO.printString("Lightning casted by $" + casterReg + " on $" + targetReg + "! Dealt " + damage + " damage. Target HP: " + targetHP + "\n");
                            }
                        }
                )
        );

        // balanceHP
        instructionList.add(
                new BasicInstruction("balanceHP $t0,$t1",
                        "Balance HP: average HP between user ($t0) and target ($t1) and set both to that value.",
                        BasicInstructionFormat.R_FORMAT,
                        "110100 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int hpRegUser  = operands[0];
                                int hpRegTarget= operands[1];

                                int hpUser   = RegisterFile.getValue(hpRegUser);
                                int hpTarget = RegisterFile.getValue(hpRegTarget);

                                int total = hpUser + hpTarget;
                                int avg   = total / 2;

                                RegisterFile.updateRegister(hpRegUser,   avg);
                                RegisterFile.updateRegister(hpRegTarget, avg);
                                SystemIO.printString("Balance HP: $" + hpRegUser + " and $" + hpRegTarget + " HP balanced to " + avg + " each\n");
                            }
                        }
                )
        );
        // manaSteal
        instructionList.add(
                new BasicInstruction("manaSteal $t0,$t1",
                        "Mana Steal: move all mana from target ($t1) to user ($t0).",
                        BasicInstructionFormat.R_FORMAT,
                        "110101 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int userReg    = operands[0];
                                int targetReg  = operands[1];

                                int userMana   = RegisterFile.getValue(userReg);
                                int targetMana = RegisterFile.getValue(targetReg);

                                userMana   += targetMana;
                                targetMana  = 0;

                                RegisterFile.updateRegister(userReg,   userMana);
                                RegisterFile.updateRegister(targetReg, targetMana);
                                SystemIO.printString("Mana Steal: $" + userReg + " stole all mana from $" + targetReg + "! Gained " + targetMana + " mana (now " + userMana + ")\n");
                            }
                        }
                )
        );

        // obstacleSpell
        instructionList.add(
                new BasicInstruction("obstacleSpell $t0",
                        "Obstacle Spell: mark $t0 as 'skip next turn' by setting it to 1.",
                        BasicInstructionFormat.R_FORMAT,
                        "110110 00000 00000 fffff 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int flagReg    = operands[0];
                                RegisterFile.updateRegister(flagReg, 1);
                                SystemIO.printString("Obstacle Spell: $" + flagReg + " marked to skip turn (set to 1)\n");
                            }
                        }
                )
        );

        // meleeAttack
        instructionList.add(
                new BasicInstruction("meleeAttack $t0",
                        "Melee Attack: reduce target HP in $t0 by 5.",
                        BasicInstructionFormat.R_FORMAT,
                        "110111 00000 00000 fffff 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int hpReg      = operands[0];

                                int hp = RegisterFile.getValue(hpReg) - 5;
                                if (hp < 0) hp = 0;

                                RegisterFile.updateRegister(hpReg, hp);
                                SystemIO.printString("Melee Attack on $" + hpReg + "! Dealt 5 damage. Target HP: " + hp + "\n");
                            }
                        }
                )
        );

        // manaShield
        instructionList.add(
                new BasicInstruction("manaShield $t0",
                        "Mana Shield: add 25 shield HP to $t0 (can exceed normal max HP).",
                        BasicInstructionFormat.R_FORMAT,
                        "111000 00000 00000 fffff 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int shieldReg  = operands[0];

                                int shield = RegisterFile.getValue(shieldReg) + 25;
                                RegisterFile.updateRegister(shieldReg, shield);
                                SystemIO.printString("Mana Shield: $" + shieldReg + " gained 25 shield HP (now " + shield + ")\n");
                            }
                        }
                )
        );

        // revive
        instructionList.add(
                new BasicInstruction("revive $t0,$t1",
                        "Revive: if user ($t0) has >= 50 mana and target HP ($t1) == 0, revive target and spend 50 mana.",
                        BasicInstructionFormat.R_FORMAT,
                        "111001 fffff sssss 00000 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int userReg    = operands[0];
                                int targetReg  = operands[1];

                                int mana     = RegisterFile.getValue(userReg);
                                int hp       = RegisterFile.getValue(targetReg);
                                int cost     = 50;
                                int reviveHP = 50;

                                if (mana >= cost && hp == 0) {
                                    mana -= cost;
                                    hp    = reviveHP;

                                    RegisterFile.updateRegister(userReg,   mana);
                                    RegisterFile.updateRegister(targetReg, hp);
                                    SystemIO.printString("Revive: $" + userReg + " revived $" + targetReg + " for 50 mana! Target HP restored to " + reviveHP + "\n");
                                } else if (hp != 0) {
                                    SystemIO.printString("Revive FAILED: Target $" + targetReg + " is not defeated (HP = " + hp + ")\n");
                                } else {
                                    SystemIO.printString("Revive FAILED: Caster $" + userReg + " has insufficient mana (" + mana + " < " + cost + ")\n");
                                }
                            }
                        }
                )
        );

        // blink: print string at label address (like telepathy in DragonBall)
        instructionList.add(
                new BasicInstruction("blink target",
                        "Blink: Print a message string stored at the label address.",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "111011 00000 00000 ffffffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                try {
                                    // Get the label name from the token list
                                    String label = statement.getOriginalTokenList().get(1).getValue();

                                    // Look up the label in the program symbol table to get its address
                                    int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                                    // Read and print characters until we hit null terminator
                                    StringBuilder output = new StringBuilder();
                                    char ch = (char) Globals.memory.getByte(byteAddress);

                                    while (ch != 0) {
                                        output.append(ch);
                                        byteAddress++;
                                        ch = (char) Globals.memory.getByte(byteAddress);
                                    }

                                    SystemIO.printString(output.toString());

                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, "Address error in blink: " + e.getMessage());
                                } catch (Exception e) {
                                    throw new ProcessingException(statement, "Error in blink instruction: " + e.getMessage());
                                }
                            }
                        }
                )
        );

        // imbue
        instructionList.add(
                new BasicInstruction("imbue $t0",
                        "Imbue: Multiply the given register by 5 and print the new value.",
                        BasicInstructionFormat.R_FORMAT,
                        "111010 00000 00000 fffff 00000 000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int reg = operands[0];

                                int value = RegisterFile.getValue(reg);
                                value *= 5;

                                RegisterFile.updateRegister(reg, value);
                                SystemIO.printString("Imbued value: " + value + "\n");
                            }
                        }
                        //updating
                )
        );
    }
}