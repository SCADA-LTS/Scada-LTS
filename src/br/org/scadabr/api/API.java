/**
 * API.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api;

public interface API extends javax.xml.rpc.Service {
    public java.lang.String getAPIAddress();

    public br.org.scadabr.api.ScadaBRAPI getAPI() throws javax.xml.rpc.ServiceException;

    public br.org.scadabr.api.ScadaBRAPI getAPI(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
