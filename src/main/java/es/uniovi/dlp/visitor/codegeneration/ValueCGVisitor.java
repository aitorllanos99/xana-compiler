package es.uniovi.dlp.visitor.codegeneration;

import es.uniovi.dlp.ast.expressions.*;
import es.uniovi.dlp.ast.types.FuncType;
import es.uniovi.dlp.visitor.AbstractVisitor;


public class ValueCGVisitor extends AbstractVisitor {
    private CodeGenerator generator;
    private AddressCGVisitor addressCGVisitor;

    public ValueCGVisitor(CodeGenerator generator) {
        this.generator = generator;
    }

    public void setAddressCGVisitor(AddressCGVisitor addressCGVisitor) {
        this.addressCGVisitor = addressCGVisitor;
    }

    @Override
    public Object visit(Arithmetic arithmetic, Object param) {
        super.visit(arithmetic, param);
        generator.promoteTo(arithmetic.getLeftExpression().getType(), arithmetic.getType());
        generator.arithmetic(arithmetic.getOperator(), arithmetic.getType().sufixCode());
        return null;
    }

    @Override
    public Object visit(Cast cast, Object param) {
        super.visit(cast, param);
        generator.promoteTo(cast.getExpression().getType(), cast.getTypeToCast());
        return null;
    }


    @Override
    public Object visit(CharLiteral charLiteral, Object param) {
        generator.push(charLiteral.getType().sufixCode(), charLiteral.getValue());
        return null;
    }

    @Override
    public Object visit(DoubleLiteral doubleLiteral, Object param) {
        generator.push(doubleLiteral.getType().sufixCode(), doubleLiteral.getValue());
        return null;
    }

    @Override
    public Object visit(IntLiteral intLiteral, Object param) {
        generator.push(intLiteral.getType().sufixCode(), intLiteral.getValue());
        return null;
    }

    @Override
    public Object visit(Comparison comparison, Object param) {
        super.visit(comparison, param);
        generator.comparison(comparison.getOperator(), comparison.getType().sufixCode());
        return null;
    }

    @Override
    public Object visit(Indexing indexing, Object param) {
        return super.visit(indexing, param);
    }

    @Override
    public Object visit(Invocation invocation, Object param) {
        int contador = 0;
        for (var e : invocation.getArguments()) {
            e.accept(this, param);
            //generator.promoteTo(e.getType(), ((FuncType) invocation.getName().getType()).getParameters().get(contador).getType());
        }
        generator.call(invocation.getName().getIdent());
        return null;
    }

    @Override
    public Object visit(Logical logical, Object param) {
        super.visit(logical, param);
        generator.logic(logical.getOperation());
        return null;
    }

    @Override
    public Object visit(UnaryMinus unaryMinus, Object param) {
        super.visit(unaryMinus, param);
        generator.arithmetic(unaryMinus.getOperator(), unaryMinus.getType().sufixCode());
        return null;
    }

    @Override
    public Object visit(UnaryNot unaryNot, Object param) {
        super.visit(unaryNot, param);
        generator.logic(unaryNot.getOperator());
        return null;
    }

    @Override
    public Object visit(Variable variable, Object param) {
        super.visit(variable, param);
        variable.accept(addressCGVisitor, param);
        generator.load(variable.getType().sufixCode());
        return null;
    }
}
