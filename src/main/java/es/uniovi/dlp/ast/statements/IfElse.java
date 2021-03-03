package es.uniovi.dlp.ast.statements;

import es.uniovi.dlp.ast.expressions.Expression;

import java.util.List;

public class IfElse extends AbstractStatement implements Statement{
    public Expression condition;
    public List<Statement> ifStatements;
    public List<Statement> elseStatements;

    public IfElse(int line, int column, Expression condition, List<Statement> ifStatements, List<Statement> elseStatements) {
        super(line, column);
        this.condition = condition;
        this.ifStatements = ifStatements;
        this.elseStatements = elseStatements;
    }
}
