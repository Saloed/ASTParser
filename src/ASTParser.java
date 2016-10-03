import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sobol on 9/28/16.
 */
public class ASTParser {

    public ASTParser() {
    }

    private static String getNodeName(Node node) {
        String[] nameParts = node.getClass().getName().split("\\.");
        return nameParts[nameParts.length - 1];
    }

    private static String getClassName(Class<? extends Node> clazz) {
        String[] nameParts = clazz.getName().split("\\.");
        return nameParts[nameParts.length - 1];
    }

    private static ASTNode convert(Node node, ASTNode root) {
        List<Node> children = node.getChildrenNodes();
        boolean isTerminal = children.isEmpty();
        String nodeName = getNodeName(node);
        ASTNode nd = new ASTNode(nodeName,
                node.getBegin().line, node.getEnd().line, node.toString(), root, isTerminal);
        if (root != null) root.children.add(nd);
        if (!isTerminal) for (Node child : children) {
            convert(child, nd);
        }
        return nd;

    }

    public static List<String> getAllAvailableTokens() {
        return new Reflections("com.github.javaparser.ast")
                .getSubTypesOf(Node.class)
                .stream().map(ASTParser::getClassName).collect(Collectors.toList());
    }

    public ASTParser getParser() {
        return this;
    }

    public ASTNode parseFile(String filename) {

        try {
            FileInputStream fileInputStream = new FileInputStream(filename);

            CompilationUnit ast = JavaParser.parse(fileInputStream);

            return convert(ast, null);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
