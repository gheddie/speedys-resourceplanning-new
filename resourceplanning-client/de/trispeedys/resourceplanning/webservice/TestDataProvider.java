
package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "TestDataProvider", targetNamespace = "http://webservice.resourceplanning.trispeedys.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface TestDataProvider {


    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void startHelperRequestProcess(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1);

    /**
     * 
     */
    @WebMethod
    public void prepareSimpleEventWithFloatingHelpers();

    /**
     * 
     */
    @WebMethod
    public void startSomeProcessesWithNewHelpers();

    /**
     * 
     */
    @WebMethod
    public void prepareBlockedChoosePosition();

    /**
     * 
     */
    @WebMethod
    public void prepareRealLife();

    /**
     * 
     */
    @WebMethod
    public void duplicateUnchanged();

    /**
     * 
     */
    @WebMethod
    public void startOneProcesses();

    /**
     * 
     */
    @WebMethod
    public void startSomeProcesses();

    /**
     * 
     */
    @WebMethod
    public void killAllExecutions();

    /**
     * 
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(partName = "return")
    public int anonymizeHelperAddresses();

    /**
     * 
     * @param arg0
     */
    @WebMethod
    public void setupForTesting(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     */
    @WebMethod
    public void finish2015();

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void fireTimer(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1);

    /**
     * 
     */
    @WebMethod
    public void duplicate2015();

}
