package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlBaseCatalog</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="base-catalog" type="string"/>
 * 
 * <tag name="base-catalog">
 *   <attribute name="path" type="string"/>
 *   <attribute name="limiting-mag" type="decimal"/>
 * </tag>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="base-catalog" type="string"/&gt;
 * &lt;tag name="base-catalog"&gt;
 *   &lt;attribute name="path" type="string"/&gt;
 *   &lt;attribute name="limiting-mag" type="decimal"/&gt;
 * &lt;/tag&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlBaseCatalog extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    private String content;
    private String path;
    private java.math.BigDecimal limitingMag;
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlBaseCatalog</code>.
     *
     */
    public XmlBaseCatalog() {
    }

    /**
     * Creates a <code>XmlBaseCatalog</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlBaseCatalog(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlBaseCatalog</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlBaseCatalog(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlBaseCatalog</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlBaseCatalog(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlBaseCatalog</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlBaseCatalog</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlBaseCatalog</code> by the Stack <code>stack</code>
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
        content = URelaxer.getElementPropertyAsString(element);
        path = URelaxer.getAttributePropertyAsString(element, "path");
        limitingMag = URelaxer.getAttributePropertyAsBigDecimal(element, "limiting-mag");
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
        Element element = doc.createElement("base-catalog");
        URelaxer.setElementPropertyByString(element, content);
        int size;
        URelaxer.setAttributePropertyByString(element, "path", path);
        URelaxer.setAttributePropertyByBigDecimal(element, "limiting-mag", limitingMag);
        parent.appendChild(element);
    }

    /**
     * Gets the String property <b>content</b>.
     *
     * @return String
     */
    public final String getContent() {
        return (content);
    }

    /**
     * Sets the String property <b>content</b>.
     *
     * @param content
     */
    public final void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the String property <b>path</b>.
     *
     * @return String
     */
    public final String getPath() {
        return (path);
    }

    /**
     * Sets the String property <b>path</b>.
     *
     * @param path
     */
    public final void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the java.math.BigDecimal property <b>limitingMag</b>.
     *
     * @return java.math.BigDecimal
     */
    public final java.math.BigDecimal getLimitingMag() {
        return (limitingMag);
    }

    /**
     * Sets the java.math.BigDecimal property <b>limitingMag</b>.
     *
     * @param limitingMag
     */
    public final void setLimitingMag(java.math.BigDecimal limitingMag) {
        this.limitingMag = limitingMag;
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
     * for the <code>XmlBaseCatalog</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"base-catalog".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        if (!target.isEmptyElement()) {
            return (false);
        }
        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlBaseCatalog</code>.
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
     * is valid for the <code>XmlBaseCatalog</code>.
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
