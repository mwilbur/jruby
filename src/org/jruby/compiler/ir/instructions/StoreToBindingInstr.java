package org.jruby.compiler.ir.instructions;

import java.util.Map;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.IRScope;
import org.jruby.compiler.ir.IRMethod;
import org.jruby.compiler.ir.operands.LocalVariable;
import org.jruby.compiler.ir.operands.UndefinedValue;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class StoreToBindingInstr extends Instr {
    private IRMethod targetMethod;
    private String slotName;
    private Operand value;
    private int bindingSlot;

    public StoreToBindingInstr(IRScope scope, String slotName, Operand value) {
        super(Operation.BINDING_STORE);

        this.slotName = slotName;
        this.targetMethod = (IRMethod)scope.getClosestMethodAncestor();
        this.value = value;
        bindingSlot = targetMethod.assignBindingSlot(slotName);
    }

    public String getSlotName() {
        return slotName;
    }

    public Operand[] getOperands() {
        return new Operand[]{value};
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
        value = value.getSimplifiedOperand(valueMap, force);
    }

    @Override
    public String toString() {
        return "BINDING(" + targetMethod + ")." + slotName + " = " + value;
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return new StoreToBindingInstr(targetMethod, slotName, value.cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        LocalVariable v = (LocalVariable) value;
        
        if (bindingSlot == -1) bindingSlot = targetMethod.getBindingSlot(v.getName());
        
        // FIXME: This is a pseudo-hack.  bindings set up for blocks in opt arg default values
        // can trip over this since we cannot store somethign which is not a real IRubyObject.
        Object rubyValue = currDynScope.getValue(v.getLocation(), v.getScopeDepth());
        if (rubyValue == null) rubyValue = context.getRuntime().getNil();
        if (!(rubyValue instanceof UndefinedValue)) currDynScope.setValue((IRubyObject) rubyValue, bindingSlot, 0);

        return null;
    }
}
