import Parser.Java8Lexer;
import Parser.Java8Parser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sobol on 9/28/16.
 */
public class ASTParser {

    public ASTParser() {
    }

    public ASTParser getParser() {
        return this;
    }

    public ASTNode parseFile(String filename) throws IOException {

        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(filename));
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        TreeConverter converter = new TreeConverter();
        ASTNode root = converter.convert(tree, null);
//        System.out.println(root);
        return root;
    }

    public static class ASTNode {
        public final String nodeName;
        public final int nodeIndex;
        public final int sourceStart;
        public final int sourceEnd;
        public final ASTNode parent;
        public final List<ASTNode> children;
        public final boolean isTerminal;

        public ASTNode(String nodeName, int nodeIndex, Interval sourceInterval, ASTNode parent, boolean isTerminal) {
            this.nodeName = nodeName;
            this.nodeIndex = nodeIndex;
            this.sourceStart = sourceInterval.a;
            this.sourceEnd = sourceInterval.b;
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
