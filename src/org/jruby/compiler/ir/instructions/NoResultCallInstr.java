package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.MethAddr;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.CallType;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 */
public class NoResultCallInstr extends CallBase {
    public NoResultCallInstr(Operation op, CallType callType, MethAddr methAddr, Operand receiver, Operand[] args, Operand closure) {
        super(op, callType, methAddr, receiver, args, closure);
    }
    
    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return new NoResultCallInstr(getOperation(), getCallType(), (MethAddr) getMethodAddr().cloneForInlining(ii), 
                receiver.cloneForInlining(ii), cloneCallArgs(ii), closure == null ? null : closure.cloneForInlining(ii));
    }    

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        callAdapter.call(context, self, (IRubyObject) getReceiver().retrieve(context, self, temp), temp);
        return null;
    }  
}
