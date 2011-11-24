/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jruby.compiler.ir.instructions;

import java.util.Map;
import org.jruby.RubyRegexp;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author enebo
 */
public class Match2Instr extends Instr implements ResultInstr {
    private Variable result;
    private Operand receiver;
    private Operand arg;
    
    public Match2Instr(Variable result, Operand receiver, Operand arg) {
        super(Operation.MATCH2);
        
        assert result != null: "Match2Instr result is null";
        
        this.result = result;
        this.receiver = receiver;
        this.arg = arg;
    }

    @Override
    public Operand[] getOperands() {
        return new Operand[] { receiver, arg };
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
        receiver = receiver.getSimplifiedOperand(valueMap, force);
        arg = arg.getSimplifiedOperand(valueMap, force);
    }

    public Variable getResult() {
        return result;
    }
    
    public void updateResult(Variable v) {
        this.result = v;
    }

    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return new Match2Instr((Variable) result.cloneForInlining(ii),
                receiver.cloneForInlining(ii), arg.cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        RubyRegexp regexp = (RubyRegexp) receiver.retrieve(context, self, currDynScope, temp);
        IRubyObject argValue = (IRubyObject) arg.retrieve(context, self, currDynScope, temp);
        return regexp.op_match(context, argValue);
    }
}
