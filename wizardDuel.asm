.data
    title: .asciiz " WIZARD DUEL\n\n"
    round_start: .asciiz "\n- Round "
    round_end: .asciiz " -\n"
    wizard1_label: .asciiz "Wizard 1: "
    wizard2_label: .asciiz "Wizard 2: "
    hp_label: .asciiz " HP, "
    mana_label: .asciiz " Mana\n"
    wizard1_victory: .asciiz "\n Wizard 1 Wins!\n"
    wizard2_victory: .asciiz "\n Wizard 2 Wins!\n"
    draw_message: .asciiz "\n Draw!\n"
.text
.globl main
main:
    # Print title
    blink title
    
    # Initialize Wizard 1
    loadMaxHP $s0, 50      # Wizard 1 HP
    charge $s1, $zero, 60       # Wizard 1 Mana
    
    # Initialize Wizard 2
    loadMaxHP $s2, 100      # Wizard 2 HP
    charge $s3, $zero, 60      # Wizard 2 Mana
    
    # Round counter
    loadMaxHP $s4, 0
    
    # Max rounds (prevent infinite loop)
    loadMaxHP $s5, 5
    
    # Zero for comparisons
    loadMaxHP $t9, 0

battle_loop:
    # Increment round counter
    charge $s4, $s4, 1
    
    # Display round number
    blink round_start
    manaTrackCrystal $s4
    blink round_end
    
    # Display Wizard 1 stats
    blink wizard1_label
    manaTrackCrystal $s0
    blink hp_label
    manaTrackCrystal $s1
    blink mana_label
    
    # Display Wizard 2 stats
    blink wizard2_label
    manaTrackCrystal $s2
    blink hp_label
    manaTrackCrystal $s3
    blink mana_label
    
    # Check if Wizard 1 has enough mana for lightning (20 mana)
    loadMaxHP $t0, 20
    
    # Simple mana check: if mana >= 20, cast lightning, else fireball
    # We'll just try lightning and let it fizzle if not enough mana
    lightning $s2, $s1
    
    # Check if Wizard 2 is defeated
    ifEqualCast $s2, $t9, wizard1_wins
    
    # Wizard 2 does fireball
    fireball $s0, $s3
    
    # Check if Wizard 1 is defeated
    ifEqualCast $s0, $t9, wizard2_wins
    
    # Both wizards use potions if they're still alive
    potion $s0, $s1
    potion $s2, $s3
    
    # Continue loop if we haven't hit max rounds
    ifShiftedPath $s4, $s5, battle_loop
    
    # Max rounds reached - it's a draw
    blink draw_message
    ifEqualCast $t9, $t9, end_program


wizard1_wins:
    blink wizard1_victory
    ifEqualCast $t9, $t9, end_program

wizard2_wins:
    blink wizard2_victory
    # Fall through to end

end_program:
    # Program complete