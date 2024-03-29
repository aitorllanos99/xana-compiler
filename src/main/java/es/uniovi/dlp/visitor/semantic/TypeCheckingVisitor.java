package es.uniovi.dlp.visitor.semantic;


import es.uniovi.dlp.ast.Program;
import es.uniovi.dlp.ast.definitions.FuncDefinition;
import es.uniovi.dlp.ast.definitions.VarDefinition;
import es.uniovi.dlp.ast.expressions.*;
import es.uniovi.dlp.ast.statements.*;
import es.uniovi.dlp.ast.types.*;
import es.uniovi.dlp.error.ErrorManager;

import es.uniovi.dlp.error.ErrorReason;
import es.uniovi.dlp.error.Location;
import es.uniovi.dlp.visitor.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/* --------- Comentario Examen---------
Se ha añadido una nueva regla para la clase RegisterAssignment

En ella se comprueban los dos errores pedidos haciendo llamadas al metodo correspondiente de la clase Type
Los siguientes metodos solo son sobreescritos en el tipo RecordType(struct)
 - RegisterAssignment, nos dice si la expression derecha (r) es un recordType y si es valido para hacer esta operacion
 - HasDifferentFields, mira si existen los campos con nombre y clase igual a los de la clase (r)
 - AreSimpleType, mira si los tipos de los campos de la clase r son de tipos simples, int, char y double.

En cada caso se ejecuta un error en caso de no cumplirse con su razon correspondiente, ver ErrorReason

 No se ha modificado ninguna regla anterior.
 */
public class TypeCheckingVisitor extends AbstractVisitor<Type, Type> {
    private SymbolTable table = new SymbolTable();

    @Override
    public Type visit(Indexing indexing, Type param) {
        super.visit(indexing, param);
        Type arrayType = indexing.getArray().getType();
        Type indexType = indexing.getIndex().getType();
        indexing.setType(arrayType.indexing(indexType));
        if (!(indexing.getArray().getType() instanceof ArrayType)) {
            ErrorManager.getInstance().addError(new Location(indexing.getArray().getLine(), indexing.getArray().getColumn()), ErrorReason.INVALID_INDEXING);
            return new ErrorType(indexing.getLine(), indexing.getColumn(), "Indexing error");
        }
        if (indexing.getType() == null) {
            ErrorManager.getInstance().addError(new Location(indexing.getIndex().getLine(), indexing.getIndex().getColumn()), ErrorReason.INVALID_INDEX_EXPRESSION);
            return new ErrorType(indexing.getLine(), indexing.getColumn(), "Indexing error");
        }
        return null;
    }

    @Override
    public Type visit(Cast cast, Type param) {
        super.visit(cast, param);
        cast.setType(cast.getExpression().getType().cast(cast.getTypeToCast()));
        if (cast.getType() == null) {
            ErrorManager.getInstance().addError(new Location(cast.getLine(), cast.getColumn()), ErrorReason.INVALID_CAST);
            return new ErrorType(cast.getExpression().getLine(), cast.getExpression().getColumn(), "Cast error");
        }
        return null;
    }


    @Override
    public Type visit(CharLiteral charLiteral, Type param) {
        super.visit(charLiteral, param);
        charLiteral.setType(new CharType(charLiteral.getLine(), charLiteral.getColumn()));
        return null;
    }

    @Override
    public Type visit(Comparison comparison, Type param) {
        super.visit(comparison, param);
        comparison.setType(comparison.getLeftExpression().getType().comparison(comparison.getRightExpression().getType()));
        if (comparison.getType() == null)
            ErrorManager.getInstance().addError(new Location(comparison.getLeftExpression().getLine(), comparison.getLeftExpression().getColumn()), ErrorReason.INVALID_COMPARISON);

        return null;
    }

    @Override
    public Type visit(DoubleLiteral doubleLiteral, Type param) {
        super.visit(doubleLiteral, param);
        doubleLiteral.setType(new DoubleType(doubleLiteral.getLine(), doubleLiteral.getColumn()));
        return null;
    }

    @Override
    public Type visit(FieldAccess fieldAccess, Type param) {
        super.visit(fieldAccess, param);

        if (!fieldAccess.getExpression1().getType().isStructField(fieldAccess.getProperty())) {
            ErrorManager.getInstance().addError(new Location(fieldAccess.getLine(), fieldAccess.getColumn()), ErrorReason.NO_SUCH_FIELD);
            return new ErrorType(fieldAccess.getLine(), fieldAccess.getColumn(), "Field access error");
        }

        fieldAccess.setType(fieldAccess.getExpression1().getType().dot(fieldAccess.getProperty()));

        if (fieldAccess.getType() == null) {
            ErrorManager.getInstance().addError(new Location(fieldAccess.getLine(), fieldAccess.getColumn()), ErrorReason.INVALID_DOT);
            return new ErrorType(fieldAccess.getLine(), fieldAccess.getColumn(), "Field access error");
        }
        return null;
    }

    @Override
    public Type visit(IntLiteral intLiteral, Type param) {
        super.visit(intLiteral, param);
        intLiteral.setType(new IntType(intLiteral.getLine(), intLiteral.getColumn()));
        return null;
    }

    @Override
    public Type visit(Invocation invocation, Type param) {
        if (invocation.getName().getDefinition() == null)
            return null; //Identification Error
        super.visit(invocation, param);
        if (invocation.getName().getDefinition().getType().isDifferentArgs(invocation.getArguments())) {
            ErrorManager.getInstance().addError(new Location(invocation.getName().getLine(), invocation.getName().getColumn()), ErrorReason.INVALID_ARGS);
            return new ErrorType(invocation.getLine(), invocation.getColumn(), "Invocation error");

        }
        if (!(invocation.getName().getDefinition().getType() instanceof FuncType)) {
            ErrorManager.getInstance().addError(new Location(invocation.getName().getLine(), invocation.getName().getColumn()), ErrorReason.INVALID_INVOCATION);
            return new ErrorType(invocation.getLine(), invocation.getColumn(), "Invocation error");

        }
        invocation.setType(invocation.getName().getDefinition().getType().parenthesis(invocation.getArguments()));

        if (invocation.getType() == null) {
            ErrorManager.getInstance().addError(new Location(invocation.getName().getLine(), invocation.getName().getColumn()), ErrorReason.INVALID_ARGS);
            return new ErrorType(invocation.getLine(), invocation.getColumn(), "Invocation error");

        }


        return null;
    }

    @Override
    public Type visit(Logical logical, Type param) {
        super.visit(logical, param);
        logical.setType(logical.getLeftExpression().getType().logical(logical.getRightExpression().getType()));
        if (logical.getType() == null) {
            ErrorManager.getInstance().addError(new Location(logical.getLeftExpression().getLine(), logical.getLeftExpression().getColumn()), ErrorReason.INVALID_LOGICAL);
            return new ErrorType(logical.getLine(), logical.getColumn(), "logical Error");
        }
        return null;
    }

    @Override
    public Type visit(UnaryMinus unaryMinus, Type param) {
        super.visit(unaryMinus, param);
        if (!unaryMinus.getExpression().getType().isArithmetic()) {
            ErrorManager.getInstance().addError(new Location(unaryMinus.getLine(), unaryMinus.getColumn()), ErrorReason.INVALID_ARITHMETIC);
            return new ErrorType(unaryMinus.getExpression().getLine(), unaryMinus.getExpression().getColumn(), "Unary Minus error");
        }
        unaryMinus.setType(new IntType(unaryMinus.getExpression().getLine(), unaryMinus.getExpression().getColumn()));
        return null;
    }

    @Override
    public Type visit(UnaryNot unaryNot, Type param) {
        super.visit(unaryNot, param);
        if (!unaryNot.getExpression().getType().isLogical()) {
            ErrorManager.getInstance().addError(new Location(unaryNot.getLine(), unaryNot.getColumn()), ErrorReason.NOT_LOGICAL);
            return new ErrorType(unaryNot.getLine(), unaryNot.getColumn(), "Unary Not Mistake");
        }
        unaryNot.setType(new IntType(unaryNot.getExpression().getLine(), unaryNot.getExpression().getColumn()));

        return null;
    }

    @Override
    public Type visit(Variable variable, Type param) {
        super.visit(variable, param);
        variable.setType(variable.getDefinition().getType());
        return null;
    }

    @Override
    public Type visit(Arithmetic arithmetic, Type param) {
        super.visit(arithmetic, param);
        arithmetic.setType(arithmetic.getLeftExpression().getType().arithmetic(arithmetic.getRightExpression().getType()));
        if (arithmetic.getType() == null) {
            ErrorManager.getInstance().addError(new Location(arithmetic.getRightExpression().getLine(), arithmetic.getRightExpression().getColumn()), ErrorReason.INVALID_ARITHMETIC);
            return new ErrorType(arithmetic.getLine(), arithmetic.getColumn(), "Arithmetic error");
        }
        return null;
    }

    @Override
    public Type visit(Assignment assignment, Type param) {
        Type visitorType = super.visit(assignment, param);
        if (visitorType instanceof ErrorType || assignment.getLeftExpression().getType() instanceof ErrorType)
            return null;
        if (!assignment.getLeftExpression().getLValue())
            ErrorManager.getInstance().addError(new Location(assignment.getLine(), assignment.getColumn()), ErrorReason.LVALUE_REQUIRED);
        if (assignment.getLeftExpression().getType().assignment(assignment.getRightExpression().getType()) == null)
            ErrorManager.getInstance().addError(new Location(assignment.getLine(), assignment.getColumn()), ErrorReason.INCOMPATIBLE_TYPES);

        return null;
    }

    @Override
    public Type visit(IfElse ifElse, Type param) {
        super.visit(ifElse, param);
        if (!ifElse.getCondition().getType().isLogical())
            ErrorManager.getInstance().addError(new Location(ifElse.getLine(), ifElse.getColumn()), ErrorReason.NOT_LOGICAL);

        return null;
    }

    @Override
    public Type visit(Read read, Type param) {
        super.visit(read, param);
        if (!read.getExpression().getLValue())
            ErrorManager.getInstance().addError(new Location(read.getLine(), read.getColumn()), ErrorReason.LVALUE_REQUIRED);
        return null;
    }

    @Override
    public Type visit(Return returnStatement, Type param) {
        super.visit(returnStatement, param);
        FuncType t = (FuncType) param;
        returnStatement.getExpression().setType(returnStatement.getExpression().getType().promotableTo(t.getReturnType()));
        if (returnStatement.getExpression().getType() == null)
            ErrorManager.getInstance().addError(new Location(returnStatement.getLine(), returnStatement.getColumn()), ErrorReason.INVALID_RETURN_TYPE);

        return null;
    }

    @Override
    public Type visit(While whileStatement, Type param) {
        super.visit(whileStatement, param);
        if (whileStatement.getCondition().getType() != null && !whileStatement.getCondition().getType().isLogical())
            ErrorManager.getInstance().addError(new Location(whileStatement.getLine(), whileStatement.getColumn()), ErrorReason.NOT_LOGICAL);

        return null;
    }


    @Override
    public Type visit(RegisterAssignment registerAssignment, Type param) {
        super.visit(registerAssignment, param);
        if (registerAssignment.getLeftExpression().getType().registerAssignment(registerAssignment.getRightExpression().getType()) == null) {
            ErrorManager.getInstance().addError(new Location(registerAssignment.getLine(), registerAssignment.getColumn()), ErrorReason.EXAM_INVALID_REGISTER_ASSIGNMENT);
            return null;
        }
        if (!registerAssignment.getLeftExpression().getType().hasDifferentFields(registerAssignment.getRightExpression().getType())) {
            ErrorManager.getInstance().addError(new Location(registerAssignment.getLine(), registerAssignment.getColumn()), ErrorReason.EXAM_NO_SUCH_FIELD);
            return null;
        }
        if (!registerAssignment.getLeftExpression().getType().areSimpleType(registerAssignment.getRightExpression().getType())) {
            ErrorManager.getInstance().addError(new Location(registerAssignment.getLine(), registerAssignment.getColumn()), ErrorReason.EXAM_NO_SIMPLE_TYPE);
            return null;
        }
        return null;
    }
}
