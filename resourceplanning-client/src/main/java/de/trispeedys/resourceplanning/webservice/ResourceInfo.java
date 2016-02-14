
package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ResourceInfo", targetNamespace = "http://webservice.resourceplanning.trispeedys.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ResourceInfo {


    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public HierarchicalEventItemDTOArray getEventNodes(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        boolean arg1);

    /**
     * 
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.EventDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public EventDTOArray queryEvents();

    /**
     * 
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.HelperDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public HelperDTOArray queryHelpers();

    /**
     * 
     */
    @WebMethod
    public void finishUp();

    /**
     * 
     * @param arg5
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void createHelper(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3,
        @WebParam(name = "arg4", partName = "arg4")
        int arg4,
        @WebParam(name = "arg5", partName = "arg5")
        int arg5);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns long
     */
    @WebMethod
    @WebResult(partName = "return")
    public long createDomain(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        int arg1);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void killSwap(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1,
        @WebParam(name = "arg2", partName = "arg2")
        long arg2);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void swapPositions(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1,
        @WebParam(name = "arg2", partName = "arg2")
        long arg2,
        @WebParam(name = "arg3", partName = "arg3")
        boolean arg3);

    /**
     * 
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.PositionDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public PositionDTOArray queryAvailablePositions(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.PositionDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public PositionDTOArray queryEventPositions(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        boolean arg1);

    /**
     * 
     */
    @WebMethod
    public void sendAllMessages();

    /**
     * 
     * @param arg5
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void duplicateEvent(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3,
        @WebParam(name = "arg4", partName = "arg4")
        int arg4,
        @WebParam(name = "arg5", partName = "arg5")
        int arg5);

    /**
     * 
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.RequestedSwapDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public RequestedSwapDTOArray queryRequestedSwaps(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0);

    /**
     * 
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.MessageDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public MessageDTOArray queryUnsentMessages();

    /**
     * 
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.ManualAssignmentDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public ManualAssignmentDTOArray queryManualAssignments(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0);

    /**
     * 
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.ExecutionDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public ExecutionDTOArray queryExecutions();

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void cancelAssignment(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1);

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void completeManualAssignment(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1);

    /**
     * 
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void createPosition(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        int arg1,
        @WebParam(name = "arg2", partName = "arg2")
        long arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3,
        @WebParam(name = "arg4", partName = "arg4")
        boolean arg4);

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void removePositionsFromEvent(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1);

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void addPositionsToEvent(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1);

    /**
     * 
     * @param arg0
     */
    @WebMethod
    public void startProcessesForActiveHelpersByEventId(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0);

    /**
     * 
     * @param arg0
     */
    @WebMethod
    public void killSwapByBusinessKey(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

}
