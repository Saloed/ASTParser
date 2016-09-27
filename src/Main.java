import Parser.Java8Lexer;
import Parser.Java8Parser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static void parseFile(String filename) throws IOException {

        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(filename));
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        TreeConverter converter = new TreeConverter();
        ASTNode root = converter.convert(tree, null);
        System.out.println(root);
    }

    public static void main(String[] args) {
        try {
            parseFile("java_files/Arguments.java");
        } catch (IOException ex) {
            System.err.println(ex.toString());
            System.err.println(ex.getMessage());
        }
    }


    private static class ASTNode {
        final String nodeName;
        final int nodeIndex;
        final Interval sourceInterval;
        final ASTNode parent;
        final List<ASTNode> children;
        final boolean isTerminal;

        ASTNode(String nodeName, int nodeIndex, Interval sourceInterval, ASTNode parent, boolean isTerminal) {
            this.nodeName = nodeName;
            this.nodeIndex = nodeIndex;
            this.sourceInterval = sourceInterval;
            this.parent = parent;
            this.children = new LinkedList<>();
            this.isTerminal = isTerminal;
        }

        private String shiftedString(String shift) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            stringBuilder.append(shift);
            stringBuilder.append("┗ ");
            stringBuilder.append(nodeName);
            for (ASTNode child : children) {
                if (child.isTerminal) {
                    stringBuilder.append(" ");
                    stringBuilder.append(child.nodeName);
                } else {
                    stringBuilder.append(child.shiftedString(shift + " "));
                }
            }
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            return shiftedString("");
        }
    }

    private static class TreeConverter {

        TreeConverter() {
        }

        ASTNode convert(ParseTree t, ASTNode parent) {
            if (t instanceof TerminalNode) {

                return visitTerminal((TerminalNode) t, parent);

            } else {

                RuleNode r = (RuleNode) t;
                ASTNode thisNode = enterRuleNode(r, parent);

                int n = r.getChildCount();

                for (int i = 0; i < n; ++i) {
                    convert(r.getChild(i), thisNode);
                }

                return thisNode;
            }
        }

        private ASTNode visitTerminal(TerminalNode node, ASTNode parent) {
            if (parent == null) {
                System.err.println("Parent of terminal is null");
                return null;
            }
            int tokenIndex = node.getSymbol().getType();
            String tokenName = Java8Parser.VOCABULARY.getSymbolicName(tokenIndex);
            ASTNode child = new ASTNode(tokenName, tokenIndex, node.getSourceInterval(), parent, true);
            parent.children.add(child);
            return child;
        }


        private ASTNode enterRuleNode(RuleNode r, ASTNode parent) {
            ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
            String tokenName = Java8Parser.ruleNames[ctx.getRuleIndex()];
            ASTNode node = new ASTNode(tokenName, ctx.getRuleIndex(), ctx.getSourceInterval(), parent, false);
            if (parent != null) {
                parent.children.add(node);
            }
            return node;
        }


    }

}
