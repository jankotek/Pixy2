package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlMagRecordHolder</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="mag-record-holder">
 *   <ref label="mag-record" occurs="*"/>
 * </elementRule>
 * 
 * <tag name="mag-record-holder"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="mag-record-holder"&gt;
 *   &lt;ref label="mag-record" occurs="*"/&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="mag-record-holder"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlMagRecordHolder extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    // List<XmlMagRecord>
    private java.util.List magRecord = new java.util.ArrayList();
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlMagRecordHolder</code>.
     *
     */
    public XmlMagRecordHolder() {
    }

    /**
     * Creates a <code>XmlMagRecordHolder</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlMagRecordHolder(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlMagRecordHolder</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlMagRecordHolder(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlMagRecordHolder</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlMagRecordHolder(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlMagRecordHolder</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlMagRecordHolder</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlMagRecordHolder</code> by the Stack <code>stack</code>
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
        magRecord.clear();
        while (!stack.isEmptyElement()) {
            if (XmlMagRecord.isMatch(stack)) {
                addMagRecord(factory.createXmlMagRecord(stack));
            } else {
                break;
            }
        }
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
        Element element = doc.createElement("mag-record-holder");
        int size;
        size = magRecord.size();
        for (int i = 0;i < size;i++) {
            XmlMagRecord value = (XmlMagRecord)this.magRecord.get(i);
            value.makeElement(element);
        }
        parent.appendChild(element);
    }

    /**
     * Gets the XmlMagRecord property <b>magRecord</b>.
     *
     * @return XmlMagRecord[]
     */
    public final XmlMagRecord[] getMagRecord() {
        IPixyFactory factory = PixyFactory.getFactory();
        XmlMagRecord[] array = factory.createArrayXmlMagRecord(magRecord.size());
        return ((XmlMagRecord[])magRecord.toArray(array));
    }

    /**
     * Sets the XmlMagRecord property <b>magRecord</b>.
     *
     * @param magRecord
     */
    public final void setMagRecord(XmlMagRecord[] magRecord) {
        this.magRecord.clear();
        this.magRecord.addAll(java.util.Arrays.asList(magRecord));
        for (int i = 0;i < magRecord.length;i++) {
            magRecord[i].setParentRNode(this);
        }
    }

    /**
     * Adds the XmlMagRecord property <b>magRecord</b>.
     *
     * @param magRecord
     */
    public final void addMagRecord(XmlMagRecord magRecord) {
        this.magRecord.add(magRecord);
        magRecord.setParentRNode(this);
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
        classNodes.addAll(magRecord);
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
     * for the <code>XmlMagRecordHolder</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"mag-record-holder".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        while (!target.isEmptyElement()) {
            if (!XmlMagRecord.isMatchHungry(target)) {
                break;
            }
        }
        if (!target.isEmptyElement()) {
            return (false);
        }
        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>XmlMagRecordHolder</code>.
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
     * is valid for the <code>XmlMagRecordHolder</code>.
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
