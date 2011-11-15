package org.jruby.compiler.ir.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.compiler.ir.Tuple;
import org.jruby.compiler.ir.IRExecutionScope;
import org.jruby.compiler.ir.operands.Array;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.operands.LocalVariable;
import org.jruby.compiler.ir.operands.ClosureLocalVariable;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.instructions.CallBase;
import org.jruby.compiler.ir.instructions.YieldInstr;
import org.jruby.compiler.ir.instructions.ResultInstr;

public class InlinerInfo {
    public final CFG callerCFG;
    public final CallBase call;

    private Operand[] callArgs;
    private Map<Label, Label> lblRenameMap;
    private Map<Variable, Variable> varRenameMap;
    private Map<BasicBlock, BasicBlock> bbRenameMap;
    private List yieldSites;

    public InlinerInfo(CallBase call, CFG c) {
        this.call = call;
        this.callArgs = call.getCallArgs();
        this.callerCFG = c;
        this.varRenameMap = new HashMap<Variable, Variable>();
        this.lblRenameMap = new HashMap<Label, Label>();
        this.bbRenameMap = new HashMap<BasicBlock, BasicBlock>();
        this.yieldSites = new ArrayList();
    }

    public Label getRenamedLabel(Label l) {
        Label newLbl = this.lblRenameMap.get(l);
        if (newLbl == null) {
           newLbl = this.callerCFG.getScope().getNewLabel();
           this.lblRenameMap.put(l, newLbl);
        }
        return newLbl;
    }

    public Variable getRenamedVariable(Variable v) {
        Variable newVar = this.varRenameMap.get(v);
        if (newVar == null) {
            IRExecutionScope m = this.callerCFG.getScope();
            newVar = m.getNewInlineVariable();
            if ((v instanceof LocalVariable) && !(v instanceof ClosureLocalVariable)) {
                // Frame load/store placement dataflow pass (and possible other passes later on) exploit
                // information whether a variable is a temporary or a local/self variable.
                // So, variable renaming for inlining has to preserve this information.
                newVar = m.getLocalVariable(newVar.getName(), ((LocalVariable)newVar).getScopeDepth());
            }
            this.varRenameMap.put(v, newVar);
        }
        return newVar;
    }

    public BasicBlock getRenamedBB(BasicBlock bb) {
        return bbRenameMap.get(bb);
    }

    public BasicBlock getOrCreateRenamedBB(BasicBlock bb) {
        BasicBlock renamedBB = getRenamedBB(bb);
        if (renamedBB == null) {
            renamedBB =  new BasicBlock(this.callerCFG, getRenamedLabel(bb.getLabel()));
            bbRenameMap.put(bb, renamedBB);
        }
        return renamedBB;
    }

    public int getArgsCount() {
        return callArgs.length;
    }

    public Operand getCallArg(int index) {
        return index < callArgs.length ? callArgs[index] : null;
    }

    public Operand getCallArg(int argIndex, boolean restOfArgArray) {
        if (restOfArgArray == false) {
            return getCallArg(argIndex);
        }
        else if (argIndex >= callArgs.length) {
            return new Array();
        }
        else {
            Operand[] tmp = new Operand[callArgs.length - argIndex];
            for (int j = argIndex; j < callArgs.length; j++)
                tmp[j-argIndex] = callArgs[j];

            return new Array(tmp);
        }
    }

    public Operand getCallReceiver() {
        return call.getReceiver();
    }

    public Operand getCallClosure() {
        return call.getClosureArg();
    }

    public Variable getCallResultVariable() {
        return (call instanceof ResultInstr) ? ((ResultInstr)call).getResult() : null;
    }

    public void recordYieldSite(BasicBlock bb, YieldInstr i) {
        yieldSites.add(new Tuple<BasicBlock, YieldInstr>(bb, i));
    }

    public List getYieldSites() {
        return yieldSites;
    }
}
