package org.jruby.compiler.ir.instructions;

import java.util.Map;
import org.jruby.compiler.ir.Interp;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.RubyArray;
import org.jruby.RubyProc;
import org.jruby.RubyNil;
import org.jruby.runtime.DynamicScope;

public class YieldInstr extends Instr implements ResultInstr {
    public final boolean unwrapArray;
    private Operand blockArg;
    private Operand yieldArg;
    private Variable result;

    public YieldInstr(Variable result, Variable block, Operand arg, boolean unwrapArray) {
        super(Operation.YIELD);
        
        assert result != null: "YieldInstr result is null";
        
        this.blockArg = block;
        this.yieldArg = arg;
        this.unwrapArray = unwrapArray;
        this.result = result;
    }
   
    public Instr cloneForInlining(InlinerInfo ii) {
        // FIXME: This needs to be cloned!
        return this;  // This is just a placeholder during inlining.
    }

    public Operand getBlockArg() {
        return blockArg;
    }

    public Operand getYieldArg() {
        return yieldArg;
    }

    @Interp
    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        Object resultValue;
        Object blk = (Object) blockArg.retrieve(context, self, currDynScope, temp);
        if (blk instanceof RubyProc) blk = ((RubyProc)blk).getBlock();
        if (blk instanceof RubyNil) blk = Block.NULL_BLOCK;
        // Blocks that get yielded are always normal
        Block b = (Block)blk;
        b.type = Block.Type.NORMAL;
        if (yieldArg == null) {
            return b.yieldSpecific(context);
        } else {
            IRubyObject yieldVal = (IRubyObject)yieldArg.retrieve(context, self, currDynScope, temp);
            return (unwrapArray && (yieldVal instanceof RubyArray)) ? b.yieldArray(context, yieldVal, null, null) : b.yield(context, yieldVal);
        }
    }

    @Override
    public String toString() { 
        return unwrapArray ? (super.toString() + "(" + blockArg + ", " + yieldArg + ")") : (super.toString() + "(" + blockArg + ", UNWRAP(" + yieldArg + "))");
    }

    // if unwrapArray, maybe convert yieldArg into a CompoundArray operand?
    public Operand[] getOperands() {
        return (yieldArg == null) ? new Operand[]{blockArg} : new Operand[] {blockArg, yieldArg};
    }
    
    public Variable getResult() {
        return result;
    }    

    public void updateResult(Variable v) {
        this.result = v;
    }

    public Operand[] getNonBlockOperands() {
        return (yieldArg == null) ? new Operand[]{} : new Operand[] {yieldArg};
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
        blockArg = blockArg.getSimplifiedOperand(valueMap, force);
        if (yieldArg != null) yieldArg = yieldArg.getSimplifiedOperand(valueMap, force);
    }
}
