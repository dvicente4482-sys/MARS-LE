.data
    title: .asciiz "Spell Testing \n"
    
    test1: .asciiz "1. Testing Shield Spell:\n"
    test2: .asciiz "\n2. Testing Mana Steal:\n"
    test3: .asciiz "\n3. Testing Balance HP:\n"
    test4: .asciiz "\n4. Testing Melee Attack:\n"
    test5: .asciiz "\n5. Testing Revive:\n"
    
    before: .asciiz "Before: "
    after: .asciiz "After: "
    hp: .asciiz " HP, "
    mana: .asciiz " Mana\n"
    
    done: .asciiz "\n Done. \n"

.text
.globl main

main:
    blink title
    
    
    blink test1
    
    loadMaxHP $t0, 50        # Start with 50 HP
    blink before
    manaTrackCrystal $t0
    
    manaShield $t0           # Add 25 shield
    blink after
    manaTrackCrystal $t0     # Should be 75
    

    blink test2
    
    loadMaxHP $t1, 20        # Thief has 20 mana
    loadMaxHP $t2, 30        # Victim has 30 mana
    
    blink before
    manaTrackCrystal $t1
    manaTrackCrystal $t2
    
    manaSteal $t1, $t2       # Steal all mana
    
    blink after
    manaTrackCrystal $t1     # Should be 50
    manaTrackCrystal $t2     # Should be 0
    

    blink test3
    
    loadMaxHP $t3, 80        # Person 1: 80 HP
    loadMaxHP $t4, 40        # Person 2: 40 HP
    
    blink before
    manaTrackCrystal $t3
    manaTrackCrystal $t4
    
    balanceHP $t3, $t4       # Average them
    
    blink after
    manaTrackCrystal $t3     # Should be 60
    manaTrackCrystal $t4     # Should be 60
    
   
    blink test4
    
    loadMaxHP $t5, 25        # Enemy with 25 HP
    
    blink before
    manaTrackCrystal $t5
    
    meleeAttack $t5          # Hit for 5 damage
    meleeAttack $t5          # Hit for 5 damage
    
    blink after
    manaTrackCrystal $t5     # Should be 15
    

    blink test5
    
    loadMaxHP $t6, 100       # Healer with 100 mana
    loadMaxHP $t7, 0         # Dead person (0 HP)
    
    blink before
    manaTrackCrystal $t6
    manaTrackCrystal $t7
    
    revive $t6, $t7          # Revive costs 50 mana, gives 50 HP
    
    blink after
    manaTrackCrystal $t6     # Should be 50 mana
    manaTrackCrystal $t7     # Should be 50 HP
    
    # Done
    blink done
