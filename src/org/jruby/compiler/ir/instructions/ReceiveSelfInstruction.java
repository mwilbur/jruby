package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

// SSS FIXME: ReceiveSelf should inherit from ReceiveArg?
public class ReceiveSelfInstruction extends Instr implements ResultInstr {
    private Variable result;
    
	 // SSS FIXME: destination always has to be a local variable '%self'.  So, is this a redundant arg?
    public ReceiveSelfInstruction(Variable result) {
        super(Operation.RECV_SELF);
        
        assert result != null: "ReceiveSelfInstr result is null";
        
        this.result = result;
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }
    
    public Variable getResult() {
        return result;
    }
    
    public void updateResult(Variable v) {
        this.result = v;
    }

    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return new CopyInstr(ii.getRenamedVariable(result), ii.getCallReceiver());
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        // result is a confusing name

        // SSS FIXME: Anything else to do here?? 
        // getResult().store(interp, context, self, self);
        return null;
    }
}
