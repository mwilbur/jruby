/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jruby.compiler.ir.instructions.jruby;

import java.util.Map;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.instructions.Instr;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author enebo
 */
public class RestoreErrorInfoInstr extends Instr {
    private Operand arg;
    
    public RestoreErrorInfoInstr(Operand arg) {
        super(Operation.RESTORE_ERROR_INFO);
        
        this.arg = arg;
    }

    @Override
    public Operand[] getOperands() {
        return new Operand[] { arg };
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
        arg = arg.getSimplifiedOperand(valueMap, force);
    }

    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return new RestoreErrorInfoInstr(arg.cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        context.setErrorInfo((IRubyObject) arg.retrieve(context, self, temp));
        
        return null;
    }
}
