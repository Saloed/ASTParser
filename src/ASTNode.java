import java.util.LinkedList;
import java.util.List;

/**
 * Created by sobol on 10/3/16.
 */

public class ASTNode {
    public final String nodeName;
    //        public final int nodeIndex;
    public final int sourceStart;
    public final int sourceEnd;
    public final ASTNode parent;
    public final List<ASTNode> children;
    public final boolean isTerminal;
    private final String text;


    public ASTNode(String nodeName, /*int nodeIndex,*/ int startInterval, int endInterval,
                   String text, ASTNode parent, boolean isTerminal) {
        this.nodeName = nodeName;
//            this.nodeIndex = nodeIndex;
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
                .append("]");//.append(text);

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

