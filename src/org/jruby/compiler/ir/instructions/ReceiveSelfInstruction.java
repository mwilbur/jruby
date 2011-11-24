package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
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
}
