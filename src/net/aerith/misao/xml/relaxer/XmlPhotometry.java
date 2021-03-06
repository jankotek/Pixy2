package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlPhotometry</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="photometry">
 *   <sequence>
 *     <ref label="catalog"/>
 *     <element name="type" type="string"/>
 *     <element name="system-formula" occurs="?" type="string"/>
 *   </sequence>
 * </elementRule>
 * 
 * <tag name="photometry"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="photometry"&gt;
 *   &lt;sequence&gt;
 *     &lt;ref label="catalog"/&gt;
 *     &lt;element name="type" type="string"/&gt;
 *     &lt;element name="system-formula" occurs="?" type="string"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="photometry"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlPhotometry extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    private XmlCatalog catalog;
    private String type;
    private String systemFormula;
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlPhotometry</code>.
     *
     */
    public XmlPhotometry() {
    }

    /**
     * Creates a <code>XmlPhotometry</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlPhotometry(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlPhotometry</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlPhotometry(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlPhotometry</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlPhotometry(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlPhotometry</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlPhotometry</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlPhotometry</code> by the Stack <code>stack</code>
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
        setCatalog(factory.createXmlCatalog(stack));
        type = URelaxer.getElementPropertyAsString(stack.popElement());
        systemFormula = URelaxer.getElementPropertyAsStringByStack(stack, "system-formula");
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
        Element element = doc.createElement("photometry");
        int size;
        catalog.makeElement(element);
        URelaxer.setElementPropertyByString(element, "type", type);
        if (systemFormula != null) {
            URelaxer.setElementPropertyByString(element, "system-formula", systemFormula);
        }
        parent.appendChild(element);
    }

    /**
     * Gets the XmlCatalog property <b>catalog</b>.
     *
     * @return XmlCatalog
     */
    public final XmlCatalog getCatalog() {
        return (catalog);
    }

    /**
     * Sets the XmlCatalog property <b>catalog</b>.
     *
     * @param catalog
     */
    public final void setCatalog(XmlCatalog catalog) {
        this.catalog = catalog;
        catalog.setParentRNode(this);
    }

    /**
     * Gets the String property <b>type</b>.
     *
     * @return String
     */
    public final String getType() {
        return (type);
    }

    /**
     * Sets the String property <b>type</b>.
     *
     * @param type
     */
    public final void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the String property <b>systemFormula</b>.
     *
     * @return String
     */
    public final String getSystemFormula() {
        return (systemFormula);
    }

    /**
     * Sets the String property <b>systemFormula</b>.
     *
     * @param systemFormula
     */
    public final void setSystemFormula(String systemFormula) {
        this.systemFormula = systemFormula;
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
        classNodes.add(catalog);
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
     * for the <code>XmlPhotometry</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"photometry".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        if (!XmlCatalog.isMatchHungry(target)) {
            return (false);
        }
        child = target.popElement();
        if (child == null) {
            return (false);
        }
        if (!"type".equals(child.getTagName())) {
            return (false);
        }
        child = target.peekElement();
        if (child != null) {
            if ("system-formula".equals(child.getTagName())) {
                target.popElement();
            }
        }
        if (!target.isEmptyElement()) {
            return (false);
        }
        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlPhotometry</code>.
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
     * is valid for the <code>XmlPhotometry</code>.
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
