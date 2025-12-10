.data
    title: .asciiz "Potion Crafting\n\n"
    step1: .asciiz "Step 1: Gathering ingredients.\n"
    step2: .asciiz "\nStep 2: Mixing ingredients...\n"
    step3: .asciiz "\nStep 3: Calculating potion power.\n"
    step4: .asciiz "\nStep 4: Storing in inventory.\n"
    step5: .asciiz "\nStep 5: Reading from inventory.\n"
    
    herbs: .asciiz "Herbs: "
    crystals: .asciiz "Magic crystals: "
    total: .asciiz "Total: "
    divided: .asciiz "Split: "
    multiplied: .asciiz "Power x3: "
    enhanced: .asciiz "Imbued x5: "
    
    potion1_label: .asciiz "Potion slot 1: "
    potion2_label: .asciiz "Potion slot 2: "
    potion3_label: .asciiz "Potion slot 3: "
    potion4_label: .asciiz "Potion slot 4: "
    
    obstacle_msg: .asciiz "\nCrafting blocked! Status: "
    done: .asciiz "\n Done. \n"
    
    inventory: .word 0, 0, 0, 0
.text
.globl main
main:
    blink title
    
    # STEP 1: Gather
    blink step1
    loadMaxHP $t0, 15
    loadMaxHP $t1, 25
    blink herbs
    manaTrackCrystal $t0
    blink crystals
    manaTrackCrystal $t1
    
    # STEP 2: Mix
    blink step2
    conjure $t2, $t0, $t1
    blink total
    manaTrackCrystal $t2
    
    # STEP 3: Calculate
    blink step3
    loadMaxHP $t3, 4
    disperse $t4, $t2, $t3
    blink divided
    manaTrackCrystal $t4
    
    loadMaxHP $t5, 3
    amplify $t6, $t4, $t5
    blink multiplied
    manaTrackCrystal $t6
    
    loadMaxHP $t7, 4
    imbue $t7
    blink enhanced
    manaTrackCrystal $t7
    
    drain $s0, $t6, $t7
    
    # STEP 4: Store
    blink step4
    loadCrystalAddr $s1, inventory
    loadMaxHP $t0, 25
    