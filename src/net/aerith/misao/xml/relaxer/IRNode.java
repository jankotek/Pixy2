package net.aerith.misao.xml.relaxer;

/**
 * @version pixy.rlx (Tue Nov 23 20:34:02 JST 2004)
 * @author  Relaxer 0.10.1 (by ASAMI@Yokohama)
 */
public interface IRNode {
    /**
     * Sets parent RNode.
     *
     * @param parent
     */
    void setParentRNode(IRNode parent);

    /**
     * Gets parent RNode.
     *
     * @return IRNode
     */
    IRNode getParentRNode();

    /**
     * Gets child RNodes.
     *
     * @return IRNode[]
     */
    IRNode[] getRNodes();

    /**
     * Gets the RContext property <b>rContext</b>.
     *
     * @return RContext
     */
    RContext getRContext();

    /**
     * Sets the RContext property <b>rContext</b>.
     *
     * @param rContext
     */
    void setRContext(RContext rContext);

    /**
     * Gets the property "rContext" which is resolved recursively.
     *
     * @return RContext
     */
    RContext getRContextResolved();
}
