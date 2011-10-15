package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlReport</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="report">
 *   <sequence>
 *     <ref label="system"/>
 *     <ref label="information"/>
 *     <ref label="data"/>
 *   </sequence>
 * </elementRule>
 * 
 * <tag name="report"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="report"&gt;
 *   &lt;sequence&gt;
 *     &lt;ref label="system"/&gt;
 *     &lt;ref label="information"/&gt;
 *     &lt;ref label="data"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="report"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlReport extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    private XmlSystem system;
    private XmlInformation information;
    private XmlData data;
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlReport</code>.
     *
     */
    public XmlReport() {
    }

    /**
     * Creates a <code>XmlReport</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlReport(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlReport</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlReport(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlReport</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlReport(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlReport</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlReport</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlReport</code> by the Stack <code>stack</code>
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
        setSystem(factory.createXmlSystem(stack));
        setInformation(factory.createXmlInformation(stack));
        setData(factory.createXmlData(stack));
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
        Element element = doc.createElement("report");
        int size;
        system.makeElement(element);
        information.makeElement(element);
        data.makeElement(element);
        parent.appendChild(element);
    }

    /**
     * Gets the XmlSystem property <b>system</b>.
     *
     * @return XmlSystem
     */
    public final XmlSystem getSystem() {
        return (system);
    }

    /**
     * Sets the XmlSystem property <b>system</b>.
     *
     * @param system
     */
    public final void setSystem(XmlSystem system) {
        this.system = system;
        system.setParentRNode(this);
    }

    /**
     * Gets the XmlInformation property <b>information</b>.
     *
     * @return XmlInformation
     */
    public final XmlInformation getInformation() {
        return (information);
    }

    /**
     * Sets the XmlInformation property <b>information</b>.
     *
     * @param information
     */
    public final void setInformation(XmlInformation information) {
        this.information = information;
        information.setParentRNode(this);
    }

    /**
     * Gets the XmlData property <b>data</b>.
     *
     * @return XmlData
     */
    public final XmlData getData() {
        return (data);
    }

    /**
     * Sets the XmlData property <b>data</b>.
     *
     * @param data
     */
    public final void setData(XmlData data) {
        this.data = data;
        data.setParentRNode(this);
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
        classNodes.add(system);
        classNodes.add(information);
        classNodes.add(data);
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
     * for the <code>XmlReport</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"report".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        if (!XmlSystem.isMatchHungry(target)) {
            return (false);
        }
        if (!XmlInformation.isMatchHungry(target)) {
            return (false);
        }
        if (!XmlData.isMatchHungry(target)) {
            return (false);
        }
        if (!target.isEmptyElement()) {
            return (false);
        }
        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlReport</code>.
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
     * is valid for the <code>XmlReport</code>.
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