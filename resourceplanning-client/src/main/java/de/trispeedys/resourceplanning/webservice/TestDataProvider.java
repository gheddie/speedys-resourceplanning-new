
package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
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
     */
    @WebMethod
    public void duplicateUnchanged();

    /**
     * 
     */
    @WebMethod
    public void duplicate2015();

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
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void fireTimer(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1);

}
