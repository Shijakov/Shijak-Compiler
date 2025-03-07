package com.company.code_generator;

import com.company.model.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Heap {

    Slot rootFreeSlot = null;
    int heapSize = 0;
    Map<String, Pair<Integer, Integer>> heapPointers = new HashMap<>();

    private static class Slot {
        int start;
        int end;
        Slot nextSlot;
        Slot prevSlot;

        public Slot(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int memSize() {
            return end - start + 1;
        }
    }

    /**
     * This function reads the beginning of heap memory and stores it in variables
     * heapStart and heapEnd (at the beginning the variables point at the same spot in memory
     * because no heap memory has been used yet). After that in the heapStart memory location
     * it stores a value of -1 so that it is known where the memory start is.
     * @param sb The StringBuilder that contains all the generated mips code
     * @param shared The shared runtime
     */
    public void initialize(StringBuilder sb, SharedRuntime shared) {
        CommandRunner.runCommand(sb, "li", List.of("$a0", "0"));
        CommandRunner.runCommand(sb, "li", List.of("$v0", "9"));
        CommandRunner.runCommand(sb, "syscall", List.of());
        CommandRunner.runCommand(sb, "sw", List.of("$v0", shared.heapStartLabel));
        CommandRunner.runCommand(sb, "sw", List.of("$v0", shared.heapEndLabel));
        CommandRunner.runCommand(sb, "li", List.of(shared.heapArithmeticTmp, "-1"));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, "0($v0)"));
    }

    private void initializeFindFreeSlotProcedure(StringBuilder sb, SharedRuntime shared) {
        CommandRunner.runCommand(sb, "$findFreeSlot:", List.of());
        // Start from end of heap
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMoveRegister, shared.heapEndLabel));
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapStartRegister, shared.heapStartLabel));
        CommandRunner.runCommand(sb, "$proceedFindFreeSlot:", List.of());
        // If heapMovePointer == start of heap THEN break loop and allocate new heap memory
        CommandRunner.runCommand(sb, "beq", List.of(shared.heapMoveRegister, shared.heapStartRegister, "$allocateNewHeapMemSlot"));

        // Check if memory slot is free
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "bne", List.of(shared.heapArithmeticTmp, "$zero", "$didntFindFreeSlot"));

        // Part where memory slot is free
        // Check if there is enough free space in slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "subi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "8"));

        // If not enough free space CONTINUE
        CommandRunner.runCommand(sb, "blt", List.of(shared.heapArithmeticTmp, shared.heapMemSizeRequestRegister, "$didntFindFreeSlot"));

        // addressOfHeapMove - valueOfHeapMove - offset20 - requestedMemSize
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "subi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "20"));
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, shared.heapMemSizeRequestRegister));

        // If value of previous expression is >= 20 THEN partition memory, else allocate whole memory for request
        CommandRunner.runCommand(sb, "bge", List.of(shared.heapArithmeticTmp, "20", "$partitionMemorySlot"));

        // Assign whole memory slot
        // Set flag of slot to IN USE (t)
        CommandRunner.runCommand(sb, "li", List.of(shared.heapArithmeticTmp, "1"));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapMoveRegister)));

        // Store to heapTmp the value of the start address of the memory slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMemReturnRegister, shared.heapTmpPointer, "8"));

        // Finish memory allocation and escape loop
        CommandRunner.runCommand(sb, "j", List.of("$finishAllocation"));

        // Partition memory slot
        CommandRunner.runCommand(sb, "$partitionMemorySlot:", List.of());

        // Move heapTmp to start of memory slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapTmpPointer, shared.heapTmpPointer, "4"));

        // Store the size of the requested memory to the beginning of the memory slot
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMemSizeRequestRegister, String.format("0(%s)", shared.heapTmpPointer)));

        // Move heapTmp to last used memory slot of the newly assigned slot
        CommandRunner.runCommand(sb, "add", List.of(shared.heapTmpPointer, shared.heapTmpPointer, shared.heapMemSizeRequestRegister));

        // Move heapTmp to jump flag of newly assigned slot
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapTmpPointer, shared.heapTmpPointer, "8"));

        // Store TAKEN flag
        CommandRunner.runCommand(sb, "li", List.of(shared.heapArithmeticTmp, "1"));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapTmpPointer)));

        // Store value of heapMove in heapTmp (updating the jump slots)
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapTmpPointer)));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));

        // Store new size of partitioned free memory slot
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapMoveRegister, shared.heapTmpPointer));
        CommandRunner.runCommand(sb, "subi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "12"));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("4(%s)", shared.heapTmpPointer)));

        // Move tmp to start and store in memoryReturn the start address of the mem slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapTmpPointer)));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMemReturnRegister, shared.heapTmpPointer, "8"));

        // Finish with allocation and return
        CommandRunner.runCommand(sb, "$finishAllocation:", List.of());

        // Return
        CommandRunner.runCommand(sb, "jr", List.of("$ra"));

        // Here if the memory slot in this cycle was not free or was too big
        CommandRunner.runCommand(sb, "$didntFindFreeSlot:", List.of());

        // CONTINUE loop
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMoveRegister, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "j", List.of("$proceedFindFreeSlot"));

        // SYSCALL to allocate new heap memory
        CommandRunner.runCommand(sb, "$allocateNewHeapMemSlot:", List.of());

        // Move heapTmp and heapMove to end of heap
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMoveRegister, shared.heapEndLabel));

        // Allocate heap mem slots for request + 12 offset and store it into register for return
        CommandRunner.runCommand(sb, "li", List.of("$v0", shared.allocateHeapMemoryCode));
        CommandRunner.runCommand(sb, "move", List.of(shared.heapArithmeticTmp, shared.heapMemSizeRequestRegister));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "12"));
        CommandRunner.runCommand(sb, "move", List.of("$a0", shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "syscall", List.of());
        CommandRunner.runCommand(sb, "move", List.of(shared.heapMemReturnRegister, "$v0"));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMemReturnRegister, shared.heapMemReturnRegister, "4"));

        // Store the size of the requested memory to the start of the mem slot
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMemSizeRequestRegister, String.format("0(%s)", shared.heapMemReturnRegister)));

        // Update the heapEndLabel for the new heapEnd
        CommandRunner.runCommand(sb, "li", List.of("$v0", shared.allocateHeapMemoryCode));
        CommandRunner.runCommand(sb, "li", List.of("$a0", "0"));
        CommandRunner.runCommand(sb, "syscall", List.of());
        CommandRunner.runCommand(sb, "sw", List.of("$v0", shared.heapEndLabel));

        // Store heapEndLabel to heapMove
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMoveRegister, "0($v0)"));

        // Move heapMove to end of heap
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMoveRegister, shared.heapEndLabel));

        // Set end flags
        CommandRunner.runCommand(sb, "li", List.of(shared.heapArithmeticTmp, "1"));
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapMoveRegister)));

        // Update return address to point to first memory slot
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMemReturnRegister, shared.heapMemReturnRegister, "4"));

        // Return
        CommandRunner.runCommand(sb, "jr", List.of("$ra"));
    }

    private void initializeFreeSlotProcedure(StringBuilder sb, SharedRuntime shared) {
        CommandRunner.runCommand(sb, "$freeSlot:", List.of());
        // Move movePointer to end of memSlot to be freed
        CommandRunner.runCommand(sb, "move", List.of(shared.heapMoveRegister, shared.heapMemSizeRequestRegister));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMoveRegister, shared.heapMoveRegister, "-4"));
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "add", List.of(shared.heapMoveRegister, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMoveRegister, shared.heapMoveRegister, "8"));

        // Set flag to false
        CommandRunner.runCommand(sb, "sw", List.of("$zero", String.format("-4(%s)", shared.heapMoveRegister)));

        // Move tmpPointer to previous memory slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));

        //Check if tmp is at beginning of stack
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapStartRegister, shared.heapStartLabel));
        CommandRunner.runCommand(sb, "beq", List.of(shared.heapTmpPointer, shared.heapStartRegister, "$skipPrevMemCombine"));

        //Load flag
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapTmpPointer)));

        // Skip if flag is set to true (if memory slot is in use)
        CommandRunner.runCommand(sb, "bne", List.of(shared.heapArithmeticTmp, "$zero", "$skipPrevMemCombine"));

        // Move tmpHeapPointer to previous memory slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapTmpPointer)));

        // Store tmp to move heap pointer
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));

        // Move tmp pointer 1 slot up
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapTmpPointer, shared.heapTmpPointer, "4"));

        // Calculate size of new memory slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "subi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "12"));

        // Store size of new mem slot in tmpPointer
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapTmpPointer)));

        CommandRunner.runCommand(sb, "$skipPrevMemCombine:", List.of());

        // Start combination with slot after
        // Skip if move points to end of heap
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapStartRegister, shared.heapEndLabel));
        CommandRunner.runCommand(sb, "beq", List.of(shared.heapMoveRegister, shared.heapStartRegister, "$skipFollowMemCombine"));

        // Move movePointer to slot after one to be freed
        CommandRunner.runCommand(sb, "move", List.of(shared.heapTmpPointer, shared.heapMoveRegister));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMoveRegister, shared.heapMoveRegister, "4"));
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "add", List.of(shared.heapMoveRegister, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapMoveRegister, shared.heapMoveRegister, "8"));

        // Skip if flag is set to t (if mem slot is in use)
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("-4(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "bne", List.of(shared.heapArithmeticTmp, "$zero", "$skipFollowMemCombine"));

        // Set tmpPointer to start of mem slot to be freed
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapTmpPointer)));

        // Set movePointer slot value to tmp
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapTmpPointer, String.format("0(%s)", shared.heapMoveRegister)));

        // Move tmp one slot up
        CommandRunner.runCommand(sb, "addi", List.of(shared.heapTmpPointer, shared.heapTmpPointer, "4"));

        // Calculate size of new mem slot
        CommandRunner.runCommand(sb, "lw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapMoveRegister)));
        CommandRunner.runCommand(sb, "sub", List.of(shared.heapArithmeticTmp, shared.heapMoveRegister, shared.heapArithmeticTmp));
        CommandRunner.runCommand(sb, "subi", List.of(shared.heapArithmeticTmp, shared.heapArithmeticTmp, "12"));

        // Set the slot pointed by tmp to the size of the slot
        CommandRunner.runCommand(sb, "sw", List.of(shared.heapArithmeticTmp, String.format("0(%s)", shared.heapTmpPointer)));


        CommandRunner.runCommand(sb, "$skipFollowMemCombine:", List.of());

        CommandRunner.runCommand(sb, "jr", List.of("$ra"));
    }

    public void initializeProcedures(StringBuilder sb, SharedRuntime shared) {
        initializeFindFreeSlotProcedure(sb, shared);
        initializeFreeSlotProcedure(sb, shared);
    }

    public void alloc(StringBuilder sb) {
        CommandRunner.runCommand(sb, "jal", List.of("$findFreeSlot"));
    }

    public void free(StringBuilder sb) {
        CommandRunner.runCommand(sb, "jal", List.of("$freeSlot"));
    }
}
