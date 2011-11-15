package org.jruby.compiler.ir.instructions;

import org.jruby.RubyModule;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class PutConstInstr extends PutInstr {
    public PutConstInstr(Operand scopeOrObj, String constName, Operand val) {
        super(Operation.PUT_CONST, scopeOrObj, constName, val);
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return new PutConstInstr(operands[TARGET].cloneForInlining(ii), ref, operands[VALUE].cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        IRubyObject value = (IRubyObject) getValue().retrieve(context, self, temp);
        RubyModule module = (RubyModule) getTarget().retrieve(context, self, temp);

        assert module != null : "MODULE should always be something";

        module.setConstant(getRef(), value);
        return null;
    }
}
