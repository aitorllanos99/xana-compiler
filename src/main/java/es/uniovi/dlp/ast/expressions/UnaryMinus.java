package es.uniovi.dlp.ast.expressions;

import es.uniovi.dlp.visitor.Visitor;

public class UnaryMinus extends AbstractExpression implements Expression{
    public Expression expression;

    public UnaryMinus(int line, int column, Expression expressions) {
        super(line, column);
        this.expression = expressions;
    }

    @Override
    public boolean getLValue() {
        return false;
    }

    @Override
    public <ParamType, ReturnType> ReturnType accept(Visitor<ReturnType, ParamType> returnTypeParamTypeAbstractVisitor, ParamType param) {
        return returnTypeParamTypeAbstractVisitor.visit(this,param);
    }
}
