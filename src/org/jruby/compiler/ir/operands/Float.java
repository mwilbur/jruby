package org.jruby.compiler.ir.operands;

import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class Float extends Constant {
    final public Double value;
    private Object rubyFloat;

    public Float(Double val) {
        value = val;
        rubyFloat = null;
    }

    @Override
    public String toString() {
        return value + ":float";
    }

    public Constant computeValue(String methodName, Constant arg) {
        Double v1 = value;
        Double v2 = (arg instanceof Fixnum) ? 1.0 * ((Fixnum)arg).value : (Double)((Float)arg).value;

        if (methodName.equals("+"))
            return new Float(v1 + v2);
        else if (methodName.equals("-"))
            return new Float(v1 - v2);
        else if (methodName.equals("*"))
            return new Float(v1 * v2);
        else if (methodName.equals("/")) {
            return v2 == 0.0 ? null : new Float(v1 / v2); // If divisor is zero, don't simplify!
        }

        return null;
    }

    @Override
    public Object retrieve(ThreadContext context, IRubyObject self, DynamicScope currDynScope, Object[] temp) {
        if (rubyFloat == null) rubyFloat = context.getRuntime().newFloat(value);
        return rubyFloat;
    }
}
