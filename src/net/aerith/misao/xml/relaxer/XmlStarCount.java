package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlStarCount</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="star-count">
 *   <sequence>
 *     <element name="str" type="int"/>
 *     <element name="var" type="int"/>
 *     <element name="mov" type="int"/>
 *     <element name="new" type="int"/>
 *     <element name="err" type="int"/>
 *     <element name="neg" type="int"/>
 *   </sequence>
 * </elementRule>
 * 
 * <tag name="star-count"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="star-count"&gt;
 *   &lt;sequence&gt;
 *     &lt;element name="str" type="int"/&gt;
 *     &lt;element name="var" type="int"/&gt;
 *     &lt;element name="mov" type="int"/&gt;
 *     &lt;element name="new" type="int"/&gt;
 *     &lt;element name="err" type="int"/&gt;
 *     &lt;element name="neg" type="int"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="star-count"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlStarCount extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    private int str;
    private int var;
    private int mov;
    private int newValue;
    private int err;
    private int neg;
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlStarCount</code>.
     *
     */
    public XmlStarCount() {
    }

    /**
     * Creates a <code>XmlStarCount</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlStarCount(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlStarCount</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlStarCount(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlStarCount</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlStarCount(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlStarCount</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlStarCount</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlStarCount</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public void setup(RStack stack) {
        setup(stack.popElement());
    }

    /**
     * @param element
     */
    private void init(Element element) {
        IPixyFactory factory = PixyFactory.getFactory();
        RStack stack = new RStack(element);
        str = URelaxer.getElementPropertyAsInt(stack.popElement());
        var = URelaxer.getElementPropertyAsInt(stack.popElement());
        mov = URelaxer.getElementPropertyAsInt(stack.popElement());
        newValue = URelaxer.getElementPropertyAsInt(stack.popElement());
        err = URelaxer.getElementPropertyAsInt(stack.popElement());
        neg = URelaxer.getElementPropertyAsInt(stack.popElement());
    }

    /**
     * Creates a DOM representation of the object.
     * Result is appended to the Node <code>parent</code>.
     *
     * @param parent
     */
    public void makeElement(Node parent) {
        Document doc;
        if (parent instanceof Document) {
            doc = (Document)parent;
        } else {
            doc = parent.getOwnerDocument();
        }
        Element element = doc.createElement("star-count");
        int size;
        URelaxer.setElementPropertyByInt(element, "str", str);
        URelaxer.setElementPropertyByInt(element, "var", var);
        URelaxer.setElementPropertyByInt(element, "mov", mov);
        URelaxer.setElementPropertyByInt(element, "new", newValue);
        URelaxer.setElementPropertyByInt(element, "err", err);
        URelaxer.setElementPropertyByInt(element, "neg", neg);
        parent.appendChild(element);
    }

    /**
     * Gets the int property <b>str</b>.
     *
     * @return int
     */
    public final int getStr() {
        return (str);
    }

    /**
     * Sets the int property <b>str</b>.
     *
     * @param str
     */
    public final void setStr(int str) {
        this.str = str;
    }

    /**
     * Gets the int property <b>var</b>.
     *
     * @return int
     */
    public final int getVar() {
        return (var);
    }

    /**
     * Sets the int property <b>var</b>.
     *
     * @param var
     */
    public final void setVar(int var) {
        this.var = var;
    }

    /**
     * Gets the int property <b>mov</b>.
     *
     * @return int
     */
    public final int getMov() {
        return (mov);
    }

    /**
     * Sets the int property <b>mov</b>.
     *
     * @param mov
     */
    public final void setMov(int mov) {
        this.mov = mov;
    }

    /**
     * Gets the int property <b>new</b>.
     *
     * @return int
     */
    public final int getNew() {
        return (newValue);
    }

    /**
     * Sets the int property <b>new</b>.
     *
     * @param newValue
     */
    public final void setNew(int newValue) {
        this.newValue = newValue;
    }

    /**
     * Gets the int property <b>err</b>.
     *
     * @return int
     */
    public final int getErr() {
        return (err);
    }

    /**
     * Sets the int property <b>err</b>.
     *
     * @param err
     */
    public final void setErr(int err) {
        this.err = err;
    }

    /**
     * Gets the int property <b>neg</b>.
     *
     * @return int
     */
    public final int getNeg() {
        return (neg);
    }

    /**
     * Sets the int property <b>neg</b>.
     *
     * @param neg
     */
    public final void setNeg(int neg) {
        this.neg = neg;
    }

    /**
     * Gets the IRNode property <b>parentRNode</b>.
     *
     * @return IRNode
     */
    public final IRNode getParentRNode() {
        return (parentRNode);
    }

    /**
     * Sets the IRNode property <b>parentRNode</b>.
     *
     * @param parentRNode
     */
    public final void setParentRNode(IRNode parentRNode) {
        this.parentRNode = parentRNode;
    }

    /**
     * Gets child RNodes.
     *
     * @return IRNode[]
     */
    public IRNode[] getRNodes() {
        java.util.List classNodes = new java.util.ArrayList();
        IRNode[] nodes = new IRNode[classNodes.size()];
        return ((IRNode[])classNodes.toArray(nodes));
    }

    /**
     * Gets the RContext property <b>rContext</b>.
     *
     * @return RContext
     */
    public final RContext getRContext() {
        return (rContext);
    }

    /**
     * Sets the RContext property <b>rContext</b>.
     *
     * @param rContext
     */
    public final void setRContext(RContext rContext) {
        this.rContext = rContext;
        IRNode[] contextRNodes = getRNodes();
        for (int i = 0;i < contextRNodes.length;i++) {
            contextRNodes[i].setRContext(rContext);
        }
    }

    /**
     * Gets the property "rContext" which is resolved recursively.
     *
     * @return RContext
     */
    public RContext getRContextResolved() {
        if (rContext != null) {
            return (rContext);
        }
        if (parentRNode == null) {
            return (null);
        }
        return (parentRNode.getRContextResolved());
    }

    /**
     * Tests if a Element <code>element</code> is valid
     * for the <code>XmlStarCount</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"star-count".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"str".equals(child.getTagName())) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"var".equals(child.getTagName())) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"mov".equals(child.getTagName())) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"new".equals(child.getTagName())) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"err".equals(child.getTagName())) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"neg".equals(child.getTagName())) {
            return (false);
        }
        if (!target.isEmptyElement()) {
            return (false);
        }
        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlStarCount</code>.
     * This mehtod is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     * @return boolean
     */
    public static boolean isMatch(RStack stack) {
        Element element = stack.peekElement();
        if (element == null) {
            return (false);
        }
        return (isMatch(element));
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlStarCount</code>.
     * This method consumes the stack contents during matching operation.
     * This mehtod is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     * @return boolean
     */
    public static boolean isMatchHungry(RStack stack) {
        Element element = stack.peekElement();
        if (element == null) {
            return (false);
        }
        if (isMatch(element)) {
            stack.popElement();
            return (true);
        } else {
            return (false);
        }
    }
}
