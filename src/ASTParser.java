import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTree;

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


        System.out.println(tree.toStringTree(parser));
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
        private final String text;

        public ASTNode(String nodeName, int nodeIndex, int startInterval, int endInterval,
                       String text, ASTNode parent, boolean isTerminal) {
            this.nodeName = nodeName;
            this.nodeIndex = nodeIndex;
            this.sourceStart = startInterval;
            this.sourceEnd = endInterval;
            this.parent = parent;
            this.text = text;
            this.children = new LinkedList<>();
            this.isTerminal = isTerminal;
        }

        private String shiftedString(String shift) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n")
                    .append(shift).append("┗ ")
                    .append(nodeName)
                    .append(" [").append(sourceStart)
                    .append(":").append(sourceEnd)
                    .append("]").append(text);

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
            Token token = node.getSymbol();
            int tokenIndex = token.getType();
            String tokenName = Java8Parser.VOCABULARY.getSymbolicName(tokenIndex);
            ASTNode child = new ASTNode(tokenName, tokenIndex, token.getLine(), token.getLine(),
                    token.getText(), parent, true);
            parent.children.add(child);
            return child;
        }


        private ASTNode enterRuleNode(RuleNode r, ASTNode parent) {
            ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
            String tokenName = Java8Parser.ruleNames[ctx.getRuleIndex()];
            Token startToken = ctx.getStart();
            Token endToken = ctx.getStop();
            ASTNode node = new ASTNode(tokenName, ctx.getRuleIndex(), startToken.getLine(), endToken.getLine(),
                    ctx.getText(), parent, false);
            if (parent != null) {
                parent.children.add(node);
            }
            return node;
        }


    }
}
