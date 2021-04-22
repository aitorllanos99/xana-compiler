package es.uniovi.dlp.compiler;

import es.uniovi.dlp.ast.Program;
import es.uniovi.dlp.error.ErrorManager;
import es.uniovi.dlp.error.Location;
import es.uniovi.dlp.parser.XanaLexer;
import es.uniovi.dlp.parser.XanaParser;
import es.uniovi.dlp.visitor.codegeneration.OffsetVisitor;
import es.uniovi.dlp.visitor.semantic.IdentificationVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import es.uniovi.dlp.visitor.semantic.TypeCheckingVisitor;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class Compiler {
    private final String file;
    private Program program;
    private boolean reportErrors = true;
    private OutputStreamWriter out;
    public Compiler(String file) {
        this.file = file;
    }
    public Compiler(String file, OutputStreamWriter out) {
        this.file = file;
        this.out = out;
    }

    public void run() throws IOException {
        ErrorManager errorManager = ErrorManager.getInstance();
        errorManager.reset();
        program = parse(file);
        assignIdentifiers();
        assignType();
        generateOffsets();
        checkErrors();
    }

    private void generateOffsets() {
        OffsetVisitor offsetVisitor = new OffsetVisitor();
        offsetVisitor.visit(program,null);
    }

    private void assignIdentifiers() {
        IdentificationVisitor identificationVisitor = new IdentificationVisitor();
        identificationVisitor.visit(program, null);
    }

    private void checkErrors() {
        if (!reportErrors) return;

        ErrorManager errorManager = ErrorManager.getInstance();

        if (errorManager.hasErrors()) {
            errorManager.getErrors().forEach(System.err::println);

            System.exit(-1);
        }
    }

    public Program getProgram() {
        return program;
    }

    private Program parse(String file) throws IOException {
        CharStream input = CharStreams.fromFileName(file);
        XanaLexer lexer = new XanaLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XanaParser parser = new XanaParser(tokens);

        return parser.program().ast;
    }

    private void assignType() {
        TypeCheckingVisitor typeCheckingVisitor = new TypeCheckingVisitor();
        typeCheckingVisitor.visit(program, null);

    }

    public void setReportErrors(boolean reportErrors) {
        this.reportErrors = reportErrors;
    }

    public void setShowDebug(boolean b) {
    }
}

