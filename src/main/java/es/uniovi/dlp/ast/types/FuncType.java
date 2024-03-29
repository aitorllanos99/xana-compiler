package es.uniovi.dlp.ast.types;

import es.uniovi.dlp.ast.definitions.VarDefinition;
import es.uniovi.dlp.ast.expressions.Expression;
import es.uniovi.dlp.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FuncType extends AbstractType implements Type {
    private List<VarDefinition> parameters;
    private Type returnType;

    public FuncType(int line, int column, List<VarDefinition> parameters, Type returnType) {
        super(line, column);
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public List<VarDefinition> getParameters() {
        return new ArrayList<>(parameters);
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public Type promotableTo(Type to) {
        return returnType.promotableTo(to);
    }

    @Override
    public Type parenthesis(List<Expression> args) {
        if (isDifferentArgs(args))
            return null;
        for (int i = 0; i < args.size(); i++)
            if (args.get(i).getType().promotableTo(parameters.get(i).getType()) == null)
                return null;
        return this;
    }

    @Override
    public boolean isDifferentArgs(List<Expression> args) {
        if (args.size() != parameters.size())
            return true;
        return false;
    }

    @Override
    public int getNumberOfBytes() {
        return returnType.getNumberOfBytes();
    }


    @Override
    public boolean isLogical() {
        if(returnType instanceof IntType)
            return true;
        return false;
    }

    @Override
    public <ParamType, ReturnType> ReturnType accept(Visitor<ReturnType, ParamType> returnTypeParamTypeAbstractVisitor, ParamType param) {
        return returnTypeParamTypeAbstractVisitor.visit(this, param);
    }

    @Override
    public String sufixCode() {
        return returnType.sufixCode();
    }
}
