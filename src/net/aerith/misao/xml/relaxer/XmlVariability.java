package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlVariability</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="variability">
 *   <sequence>
 *     <ref label="record" occurs="+"/>
 *     <ref label="mag-record" occurs="+"/>
 *   </sequence>
 * </elementRule>
 * 
 * <tag name="variability"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="variability"&gt;
 *   &lt;sequence&gt;
 *     &lt;ref label="record" occurs="+"/&gt;
 *     &lt;ref label="mag-record" occurs="+"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="variability"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlVariability extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    // List<XmlRecord>
    private java.util.List record = new java.util.ArrayList();
    // List<XmlMagRecord>
    private java.util.List magRecord = new java.util.ArrayList();
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlVariability</code>.
     *
     */
    public XmlVariability() {
    }

    /**
     * Creates a <code>XmlVariability</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlVariability(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlVariability</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlVariability(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlVariability</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlVariability(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlVariability</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlVariability</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlVariability</code> by the Stack <code>stack</code>
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
        record.clear();
        while (!stack.isEmptyElement()) {
            if (XmlRecord.isMatch(stack)) {
                addRecord(factory.createXmlRecord(stack));
            } else {
                break;
            }
        }
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
        Element element = doc.createElement("variability");
        int size;
        size = record.size();
        for (int i = 0;i < size;i++) {
            XmlRecord value = (XmlRecord)this.record.get(i);
            value.makeElement(element);
        }
        size = magRecord.size();
        for (int i = 0;i < size;i++) {
            XmlMagRecord value = (XmlMagRecord)this.magRecord.get(i);
            value.makeElement(element);
        }
        parent.appendChild(element);
    }

    /**
     * Gets the XmlRecord property <b>record</b>.
     *
     * @return XmlRecord[]
     */
    public final XmlRecord[] getRecord() {
        IPixyFactory factory = PixyFactory.getFactory();
        XmlRecord[] array = factory.createArrayXmlRecord(record.size());
        return ((XmlRecord[])record.toArray(array));
    }

    /**
     * Sets the XmlRecord property <b>record</b>.
     *
     * @param record
     */
    public final void setRecord(XmlRecord[] record) {
        this.record.clear();
        this.record.addAll(java.util.Arrays.asList(record));
        for (int i = 0;i < record.length;i++) {
            record[i].setParentRNode(this);
        }
    }

    /**
     * Adds the XmlRecord property <b>record</b>.
     *
     * @param record
     */
    public final void addRecord(XmlRecord record) {
        this.record.add(record);
        record.setParentRNode(this);
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
        classNodes.addAll(record);
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
     * for the <code>XmlVariability</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"variability".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        if (!XmlRecord.isMatchHungry(target)) {
            return (false);
        }
        while (!target.isEmptyElement()) {
            if (!XmlRecord.isMatchHungry(target)) {
                break;
            }
        }
        if (!XmlMagRecord.isMatchHungry(target)) {
            return (false);
        }
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
     * is valid for the <code>XmlVariability</code>.
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
     * is valid for the <code>XmlVariability</code>.
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
