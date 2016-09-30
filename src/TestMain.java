import com.github.antlrjavaparser.Java7Parser;
import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.ParserConfigurator;
import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.api.body.BodyDeclaration;
import com.github.antlrjavaparser.api.body.TypeDeclaration;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by sobol on 9/30/16.
 */
public class TestMain {



    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream = new FileInputStream("java_files/Arguments.java");

            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream, parser -> {
                parser.removeErrorListeners();
                parser.addErrorListener(new DiagnosticErrorListener());
                parser.setErrorHandler(new BailErrorStrategy());
            });
            System.out.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
