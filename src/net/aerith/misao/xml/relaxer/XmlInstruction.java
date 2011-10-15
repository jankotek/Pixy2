package net.aerith.misao.xml.relaxer;

import org.w3c.dom.*;

/**
 * <b>XmlInstruction</b> is generated by Relaxer based on pixy.rlx.
 * This class is derived from:
 * 
 * <!-- for programmer
 * <elementRule role="instruction">
 *   <sequence>
 *     <ref label="image"/>
 *     <ref label="date" occurs="?"/>
 *     <ref label="exposure" occurs="?"/>
 *     <element name="observer" occurs="?" type="string"/>
 *     <ref label="center"/>
 *     <ref label="fov"/>
 *     <ref label="rotation" occurs="?"/>
 *     <element name="limiting-mag" occurs="?" type="float"/>
 *     <element name="upper-limit-mag" occurs="?" type="float"/>
 *     <element name="filter" occurs="?" type="string"/>
 *     <element name="chip" occurs="?" type="string"/>
 *     <element name="instruments" occurs="?" type="string"/>
 *     <ref label="base-catalog"/>
 *     <ref label="reversed-image" occurs="?"/>
 *     <ref label="sbig-image" occurs="?"/>
 *     <ref label="unofficial" occurs="?"/>
 *     <ref label="output" occurs="*"/>
 *   </sequence>
 * </elementRule>
 * 
 * <tag name="instruction"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="instruction"&gt;
 *   &lt;sequence&gt;
 *     &lt;ref label="image"/&gt;
 *     &lt;ref label="date" occurs="?"/&gt;
 *     &lt;ref label="exposure" occurs="?"/&gt;
 *     &lt;element name="observer" occurs="?" type="string"/&gt;
 *     &lt;ref label="center"/&gt;
 *     &lt;ref label="fov"/&gt;
 *     &lt;ref label="rotation" occurs="?"/&gt;
 *     &lt;element name="limiting-mag" occurs="?" type="float"/&gt;
 *     &lt;element name="upper-limit-mag" occurs="?" type="float"/&gt;
 *     &lt;element name="filter" occurs="?" type="string"/&gt;
 *     &lt;element name="chip" occurs="?" type="string"/&gt;
 *     &lt;element name="instruments" occurs="?" type="string"/&gt;
 *     &lt;ref label="base-catalog"/&gt;
 *     &lt;ref label="reversed-image" occurs="?"/&gt;
 *     &lt;ref label="sbig-image" occurs="?"/&gt;
 *     &lt;ref label="unofficial" occurs="?"/&gt;
 *     &lt;ref label="output" occurs="*"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="instruction"/&gt;
 * </pre>
 *
 * @version pixy.rlx (Tue Nov 23 20:34:01 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public class XmlInstruction extends net.aerith.misao.xml.IONode implements java.io.Serializable, IRNode {
    private XmlImage image;
    private String date;
    private XmlExposure exposure;
    private String observer;
    private XmlCenter center;
    private XmlFov fov;
    private XmlRotation rotation;
    private Float limitingMag;
    private Float upperLimitMag;
    private String filter;
    private String chip;
    private String instruments;
    private XmlBaseCatalog baseCatalog;
    private XmlReversedImage reversedImage;
    private XmlSbigImage sbigImage;
    private XmlUnofficial unofficial;
    // List<XmlOutput>
    private java.util.List output = new java.util.ArrayList();
    private IRNode parentRNode;
    private RContext rContext;

    /**
     * Creates a <code>XmlInstruction</code>.
     *
     */
    public XmlInstruction() {
    }

    /**
     * Creates a <code>XmlInstruction</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public XmlInstruction(RStack stack) {
        setup(stack);
    }

    /**
     * Creates a <code>XmlInstruction</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public XmlInstruction(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>XmlInstruction</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public XmlInstruction(Element element) {
        setup(element);
    }

    /**
     * Initializes the <code>XmlInstruction</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc) {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>XmlInstruction</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element) {
        init(element);
    }

    /**
     * Initializes the <code>XmlInstruction</code> by the Stack <code>stack</code>
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
        setImage(factory.createXmlImage(stack));
        date = URelaxer.getElementPropertyAsStringByStack(stack, "date");
        if (XmlExposure.isMatch(stack)) {
            setExposure(factory.createXmlExposure(stack));
        }
        observer = URelaxer.getElementPropertyAsStringByStack(stack, "observer");
        setCenter(factory.createXmlCenter(stack));
        setFov(factory.createXmlFov(stack));
        if (XmlRotation.isMatch(stack)) {
            setRotation(factory.createXmlRotation(stack));
        }
        limitingMag = URelaxer.getElementPropertyAsFloatByStack(stack, "limiting-mag");
        upperLimitMag = URelaxer.getElementPropertyAsFloatByStack(stack, "upper-limit-mag");
        filter = URelaxer.getElementPropertyAsStringByStack(stack, "filter");
        chip = URelaxer.getElementPropertyAsStringByStack(stack, "chip");
        instruments = URelaxer.getElementPropertyAsStringByStack(stack, "instruments");
        setBaseCatalog(factory.createXmlBaseCatalog(stack));
        if (XmlReversedImage.isMatch(stack)) {
            setReversedImage(factory.createXmlReversedImage(stack));
        }
        if (XmlSbigImage.isMatch(stack)) {
            setSbigImage(factory.createXmlSbigImage(stack));
        }
        if (XmlUnofficial.isMatch(stack)) {
            setUnofficial(factory.createXmlUnofficial(stack));
        }
        output.clear();
        while (!stack.isEmptyElement()) {
            if (XmlOutput.isMatch(stack)) {
                addOutput(factory.createXmlOutput(stack));
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
        Element element = doc.createElement("instruction");
        int size;
        image.makeElement(element);
        if (date != null) {
            URelaxer.setElementPropertyByString(element, "date", date);
        }
        if (exposure != null) {
            exposure.makeElement(element);
        }
        if (observer != null) {
            URelaxer.setElementPropertyByString(element, "observer", observer);
        }
        center.makeElement(element);
        fov.makeElement(element);
        if (rotation != null) {
            rotation.makeElement(element);
        }
        if (limitingMag != null) {
            URelaxer.setElementPropertyByFloat(element, "limiting-mag", limitingMag);
        }
        if (upperLimitMag != null) {
            URelaxer.setElementPropertyByFloat(element, "upper-limit-mag", upperLimitMag);
        }
        if (filter != null) {
            URelaxer.setElementPropertyByString(element, "filter", filter);
        }
        if (chip != null) {
            URelaxer.setElementPropertyByString(element, "chip", chip);
        }
        if (instruments != null) {
            URelaxer.setElementPropertyByString(element, "instruments", instruments);
        }
        baseCatalog.makeElement(element);
        if (reversedImage != null) {
            reversedImage.makeElement(element);
        }
        if (sbigImage != null) {
            sbigImage.makeElement(element);
        }
        if (unofficial != null) {
            unofficial.makeElement(element);
        }
        size = output.size();
        for (int i = 0;i < size;i++) {
            XmlOutput value = (XmlOutput)this.output.get(i);
            value.makeElement(element);
        }
        parent.appendChild(element);
    }

    /**
     * Gets the XmlImage property <b>image</b>.
     *
     * @return XmlImage
     */
    public final XmlImage getImage() {
        return (image);
    }

    /**
     * Sets the XmlImage property <b>image</b>.
     *
     * @param image
     */
    public final void setImage(XmlImage image) {
        this.image = image;
        image.setParentRNode(this);
    }

    /**
     * Gets the String property <b>date</b>.
     *
     * @return String
     */
    public final String getDate() {
        return (date);
    }

    /**
     * Sets the String property <b>date</b>.
     *
     * @param date
     */
    public final void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the XmlExposure property <b>exposure</b>.
     *
     * @return XmlExposure
     */
    public final XmlExposure getExposure() {
        return (exposure);
    }

    /**
     * Sets the XmlExposure property <b>exposure</b>.
     *
     * @param exposure
     */
    public final void setExposure(XmlExposure exposure) {
        this.exposure = exposure;
        exposure.setParentRNode(this);
    }

    /**
     * Gets the String property <b>observer</b>.
     *
     * @return String
     */
    public final String getObserver() {
        return (observer);
    }

    /**
     * Sets the String property <b>observer</b>.
     *
     * @param observer
     */
    public final void setObserver(String observer) {
        this.observer = observer;
    }

    /**
     * Gets the XmlCenter property <b>center</b>.
     *
     * @return XmlCenter
     */
    public final XmlCenter getCenter() {
        return (center);
    }

    /**
     * Sets the XmlCenter property <b>center</b>.
     *
     * @param center
     */
    public final void setCenter(XmlCenter center) {
        this.center = center;
        center.setParentRNode(this);
    }

    /**
     * Gets the XmlFov property <b>fov</b>.
     *
     * @return XmlFov
     */
    public final XmlFov getFov() {
        return (fov);
    }

    /**
     * Sets the XmlFov property <b>fov</b>.
     *
     * @param fov
     */
    public final void setFov(XmlFov fov) {
        this.fov = fov;
        fov.setParentRNode(this);
    }

    /**
     * Gets the XmlRotation property <b>rotation</b>.
     *
     * @return XmlRotation
     */
    public final XmlRotation getRotation() {
        return (rotation);
    }

    /**
     * Sets the XmlRotation property <b>rotation</b>.
     *
     * @param rotation
     */
    public final void setRotation(XmlRotation rotation) {
        this.rotation = rotation;
        rotation.setParentRNode(this);
    }

    /**
     * Gets the Float property <b>limitingMag</b>.
     *
     * @return Float
     */
    public final Float getLimitingMag() {
        return (limitingMag);
    }

    /**
     * Sets the Float property <b>limitingMag</b>.
     *
     * @param limitingMag
     */
    public final void setLimitingMag(Float limitingMag) {
        this.limitingMag = limitingMag;
    }

    /**
     * Gets the Float property <b>upperLimitMag</b>.
     *
     * @return Float
     */
    public final Float getUpperLimitMag() {
        return (upperLimitMag);
    }

    /**
     * Sets the Float property <b>upperLimitMag</b>.
     *
     * @param upperLimitMag
     */
    public final void setUpperLimitMag(Float upperLimitMag) {
        this.upperLimitMag = upperLimitMag;
    }

    /**
     * Gets the String property <b>filter</b>.
     *
     * @return String
     */
    public final String getFilter() {
        return (filter);
    }

    /**
     * Sets the String property <b>filter</b>.
     *
     * @param filter
     */
    public final void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Gets the String property <b>chip</b>.
     *
     * @return String
     */
    public final String getChip() {
        return (chip);
    }

    /**
     * Sets the String property <b>chip</b>.
     *
     * @param chip
     */
    public final void setChip(String chip) {
        this.chip = chip;
    }

    /**
     * Gets the String property <b>instruments</b>.
     *
     * @return String
     */
    public final String getInstruments() {
        return (instruments);
    }

    /**
     * Sets the String property <b>instruments</b>.
     *
     * @param instruments
     */
    public final void setInstruments(String instruments) {
        this.instruments = instruments;
    }

    /**
     * Gets the XmlBaseCatalog property <b>baseCatalog</b>.
     *
     * @return XmlBaseCatalog
     */
    public final XmlBaseCatalog getBaseCatalog() {
        return (baseCatalog);
    }

    /**
     * Sets the XmlBaseCatalog property <b>baseCatalog</b>.
     *
     * @param baseCatalog
     */
    public final void setBaseCatalog(XmlBaseCatalog baseCatalog) {
        this.baseCatalog = baseCatalog;
        baseCatalog.setParentRNode(this);
    }

    /**
     * Gets the XmlReversedImage property <b>reversedImage</b>.
     *
     * @return XmlReversedImage
     */
    public final XmlReversedImage getReversedImage() {
        return (reversedImage);
    }

    /**
     * Sets the XmlReversedImage property <b>reversedImage</b>.
     *
     * @param reversedImage
     */
    public final void setReversedImage(XmlReversedImage reversedImage) {
        this.reversedImage = reversedImage;
        reversedImage.setParentRNode(this);
    }

    /**
     * Gets the XmlSbigImage property <b>sbigImage</b>.
     *
     * @return XmlSbigImage
     */
    public final XmlSbigImage getSbigImage() {
        return (sbigImage);
    }

    /**
     * Sets the XmlSbigImage property <b>sbigImage</b>.
     *
     * @param sbigImage
     */
    public final void setSbigImage(XmlSbigImage sbigImage) {
        this.sbigImage = sbigImage;
        sbigImage.setParentRNode(this);
    }

    /**
     * Gets the XmlUnofficial property <b>unofficial</b>.
     *
     * @return XmlUnofficial
     */
    public final XmlUnofficial getUnofficial() {
        return (unofficial);
    }

    /**
     * Sets the XmlUnofficial property <b>unofficial</b>.
     *
     * @param unofficial
     */
    public final void setUnofficial(XmlUnofficial unofficial) {
        this.unofficial = unofficial;
        unofficial.setParentRNode(this);
    }

    /**
     * Gets the XmlOutput property <b>output</b>.
     *
     * @return XmlOutput[]
     */
    public final XmlOutput[] getOutput() {
        IPixyFactory factory = PixyFactory.getFactory();
        XmlOutput[] array = factory.createArrayXmlOutput(output.size());
        return ((XmlOutput[])output.toArray(array));
    }

    /**
     * Sets the XmlOutput property <b>output</b>.
     *
     * @param output
     */
    public final void setOutput(XmlOutput[] output) {
        this.output.clear();
        this.output.addAll(java.util.Arrays.asList(output));
        for (int i = 0;i < output.length;i++) {
            output[i].setParentRNode(this);
        }
    }

    /**
     * Adds the XmlOutput property <b>output</b>.
     *
     * @param output
     */
    public final void addOutput(XmlOutput output) {
        this.output.add(output);
        output.setParentRNode(this);
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
        classNodes.add(image);
        if (exposure != null) {
            classNodes.add(exposure);
        }
        classNodes.add(center);
        classNodes.add(fov);
        if (rotation != null) {
            classNodes.add(rotation);
        }
        classNodes.add(baseCatalog);
        if (reversedImage != null) {
            classNodes.add(reversedImage);
        }
        if (sbigImage != null) {
            classNodes.add(sbigImage);
        }
        if (unofficial != null) {
            classNodes.add(unofficial);
        }
        classNodes.addAll(output);
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
     * for the <code>XmlInstruction</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element) {
        String tagName = element.getTagName();
        if (!"instruction".equals(tagName)) {
            return (false);
        }
        RStack target = new RStack(element);
        Element child;
        if (!XmlImage.isMatchHungry(target)) {
            return (false);
        }
        child = target.peekElement();
        if (child != null) {
            if ("date".equals(child.getTagName())) {
                target.popElement();
            }
        }
        if (XmlExposure.isMatchHungry(target)) {
        }
        child = target.peekElement();
        if (child != null) {
            if ("observer".equals(child.getTagName())) {
                target.popElement();
            }
        }
        if (!XmlCenter.isMatchHungry(target)) {
            return (false);
        }
        if (!XmlFov.isMatchHungry(target)) {
            return (false);
        }
        if (XmlRotation.isMatchHungry(target)) {
        }
        child = target.peekElement();
        if (child != null) {
            if ("limiting-mag".equals(child.getTagName())) {
                target.popElement();
            }
        }
        child = target.peekElement();
        if (child != null) {
            if ("upper-limit-mag".equals(child.getTagName())) {
                target.popElement();
            }
        }
        child = target.peekElement();
        if (child != null) {
            if ("filter".equals(child.getTagName())) {
                target.popElement();
            }
        }
        child = target.peekElement();
        if (child != null) {
            if ("chip".equals(child.getTagName())) {
                target.popElement();
            }
        }
        child = target.peekElement();
        if (child != null) {
            if ("instruments".equals(child.getTagName())) {
                target.popElement();
            }
        }
        if (!XmlBaseCatalog.isMatchHungry(target)) {
            return (false);
        }
        if (XmlReversedImage.isMatchHungry(target)) {
        }
        if (XmlSbigImage.isMatchHungry(target)) {
        }
        if (XmlUnofficial.isMatchHungry(target)) {
        }
        while (!target.isEmptyElement()) {
            if (!XmlOutput.isMatchHungry(target)) {
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
     * is valid for the <code>XmlInstruction</code>.
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
     * is valid for the <code>XmlInstruction</code>.
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
