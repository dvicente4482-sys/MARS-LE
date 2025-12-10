.data
    title: .asciiz "Crystal Storage\n\n"
    
    step1: .asciiz "Step 1: Creating crystals\n"
    step2: .asciiz "\nStep 2: Drawing from storage\n"
    step3: .asciiz "\nStep 3: Sealing storage\n"
    step4: .asciiz "\nStep 4: Testing obstacle\n"
    
    crystal1: .asciiz "Crystal 1: "
    crystal2: .asciiz "Crystal 2: "
    drawn: .asciiz "Drawn: "
    sealed: .asciiz "Sealed: "
    obstacle: .asciiz "Obstacle status: "
    
    done: .asciiz "\nDone.\n"
    
    storage: .word 0, 0
    
.text
.globl main
main:

    blink title
    

    blink step1
    loadMaxHP $t0, 100
    loadMaxHP $t1, 200
    
    blink crystal1
    manaTrackCrystal $t0
    
    blink crystal2
    manaTrackCrystal $t1
    
    
    blink step2
    loadCrystalAddr $s0, storage
    
    drawCrystal $t2, 0($s0)
    blink drawn
    manaTrackCrystal $t2
    
   
    blink step3
    sealCrystal $t0, 0($s0)
    
    blink sealed
    manaTrackCrystal $t0

    blink step4
    loadMaxHP $t3, 5
    obstacleSpell $t3
    
    blink obstacle
    manaTrackCrystal $t3

    blink done