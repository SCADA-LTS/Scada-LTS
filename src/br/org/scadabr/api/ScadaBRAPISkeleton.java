/**
 * ScadaBRAPISkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api;

public class ScadaBRAPISkeleton implements br.org.scadabr.api.ScadaBRAPI, org.apache.axis.wsdl.Skeleton {
    private br.org.scadabr.api.ScadaBRAPI impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("removeFlexProject", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "RemoveFlexProjectResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">RemoveFlexProjectResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "removeFlexProject"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("removeFlexProject") == null) {
            _myOperations.put("removeFlexProject", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("removeFlexProject")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "SetFlexBuilderConfigParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">SetFlexBuilderConfigParams"), br.org.scadabr.api.config.SetFlexBuilderConfigParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("setFlexBuilderConfig", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "SetFlexBuilderConfigResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">SetFlexBuilderConfigResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "setFlexBuilderConfig"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("setFlexBuilderConfig") == null) {
            _myOperations.put("setFlexBuilderConfig", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("setFlexBuilderConfig")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "projectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getFlexBuilderConfig", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "GetFlexBuilderConfigResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">GetFlexBuilderConfigResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getFlexBuilderConfig"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getFlexBuilderConfig") == null) {
            _myOperations.put("getFlexBuilderConfig", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getFlexBuilderConfig")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("browseFlexProjects", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "BrowseFlexProjectsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseFlexProjectsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "browseFlexProjects"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("browseFlexProjects") == null) {
            _myOperations.put("browseFlexProjects", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("browseFlexProjects")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "ConfigureDataPointParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">ConfigureDataPointParams"), br.org.scadabr.api.config.ConfigureDataPointParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("configureDataPoint", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "ConfigureDataPointResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">ConfigureDataPointResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "configureDataPoint"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("configureDataPoint") == null) {
            _myOperations.put("configureDataPoint", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("configureDataPoint")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "RemoveDataPointParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">RemoveDataPointParams"), br.org.scadabr.api.config.RemoveDataPointParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("removeDataPoint", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "RemoveDataPointResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">RemoveDataPointResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "removeDataPoint"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("removeDataPoint") == null) {
            _myOperations.put("removeDataPoint", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("removeDataPoint")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "BrowseDataPointsParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseDataPointsParams"), br.org.scadabr.api.config.BrowseDataPointsParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("browseDataPoints", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "BrowseDataPointsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseDataPointsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "browseDataPoints"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("browseDataPoints") == null) {
            _myOperations.put("browseDataPoints", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("browseDataPoints")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "RemoveDataSourceParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">RemoveDataSourceParams"), br.org.scadabr.api.config.RemoveDataSourceParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("removeDataSource", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "RemoveDataSourceResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">RemoveDataSourceResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "removeDataSource"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("removeDataSource") == null) {
            _myOperations.put("removeDataSource", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("removeDataSource")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "ConfigureDataSourceParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">ConfigureDataSourceParams"), br.org.scadabr.api.config.ConfigureDataSourceParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("configureDataSource", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "ConfigureDataSourceResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">ConfigureDataSourceResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "configureDataSource"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("configureDataSource") == null) {
            _myOperations.put("configureDataSource", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("configureDataSource")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "BrowseDataSourcesParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseDataSourcesParams"), br.org.scadabr.api.config.BrowseDataSourcesParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("browseDataSources", _params, new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "BrowseDataSourcesResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseDataSourcesResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "browseDataSources"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("browseDataSources") == null) {
            _myOperations.put("browseDataSources", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("browseDataSources")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getStatus", _params, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "GetStatusResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">GetStatusResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getStatus"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStatus") == null) {
            _myOperations.put("getStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "ReadDataParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">ReadDataParams"), br.org.scadabr.api.da.ReadDataParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("readData", _params, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "ReadDataResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">ReadDataResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "readData"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("readData") == null) {
            _myOperations.put("readData", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("readData")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "WriteDataParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">WriteDataParams"), br.org.scadabr.api.da.WriteDataParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("writeData", _params, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "WriteDataResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">WriteDataResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "writeData"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("writeData") == null) {
            _myOperations.put("writeData", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("writeData")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "WriteStringDataParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">WriteStringDataParams"), br.org.scadabr.api.da.WriteStringDataParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("writeStringData", _params, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "WriteStringDataResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">WriteStringDataResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "writeStringData"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("writeStringData") == null) {
            _myOperations.put("writeStringData", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("writeStringData")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "BrowseTagsParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">BrowseTagsParams"), br.org.scadabr.api.da.BrowseTagsParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("browseTags", _params, new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "BrowseTagsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">BrowseTagsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "browseTags"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("browseTags") == null) {
            _myOperations.put("browseTags", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("browseTags")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "GetDataHistoryParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", ">GetDataHistoryParams"), br.org.scadabr.api.hda.GetDataHistoryParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDataHistory", _params, new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "GetDataHistoryResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", ">GetDataHistoryResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getDataHistory"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDataHistory") == null) {
            _myOperations.put("getDataHistory", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDataHistory")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "GetActiveEventsParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">GetActiveEventsParams"), br.org.scadabr.api.ae.GetActiveEventsParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getActiveEvents", _params, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "GetActiveEventsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">GetActiveEventsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getActiveEvents"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getActiveEvents") == null) {
            _myOperations.put("getActiveEvents", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getActiveEvents")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "GetEventsHistoryParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">GetEventsHistoryParams"), br.org.scadabr.api.ae.GetEventsHistoryParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getEventsHistory", _params, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "GetEventsHistoryResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">GetEventsHistoryResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getEventsHistory"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getEventsHistory") == null) {
            _myOperations.put("getEventsHistory", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getEventsHistory")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AckEventsParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AckEventsParams"), br.org.scadabr.api.ae.AckEventsParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("ackEvents", _params, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AckEventsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AckEventsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ackEvents"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("ackEvents") == null) {
            _myOperations.put("ackEvents", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("ackEvents")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "BrowseEventsParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">BrowseEventsParams"), br.org.scadabr.api.ae.BrowseEventsParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("browseEventsDefinitions", _params, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "BrowseEventsResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">BrowseEventsResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "browseEventsDefinitions"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("browseEventsDefinitions") == null) {
            _myOperations.put("browseEventsDefinitions", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("browseEventsDefinitions")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AnnotateEventParams"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AnnotateEventParams"), br.org.scadabr.api.ae.AnnotateEventParams.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("annotateEvent", _params, new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AnnotateEventResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AnnotateEventResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "annotateEvent"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("annotateEvent") == null) {
            _myOperations.put("annotateEvent", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("annotateEvent")).add(_oper);
    }

    public ScadaBRAPISkeleton() {
        this.impl = new br.org.scadabr.api.ScadaBRAPIImpl();
    }

    public ScadaBRAPISkeleton(br.org.scadabr.api.ScadaBRAPI impl) {
        this.impl = impl;
    }
    public br.org.scadabr.api.config.RemoveFlexProjectResponse removeFlexProject(int id) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.RemoveFlexProjectResponse ret = impl.removeFlexProject(id);
        return ret;
    }

    public br.org.scadabr.api.config.SetFlexBuilderConfigResponse setFlexBuilderConfig(br.org.scadabr.api.config.SetFlexBuilderConfigParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.SetFlexBuilderConfigResponse ret = impl.setFlexBuilderConfig(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.GetFlexBuilderConfigResponse getFlexBuilderConfig(int projectId) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.GetFlexBuilderConfigResponse ret = impl.getFlexBuilderConfig(projectId);
        return ret;
    }

    public br.org.scadabr.api.config.BrowseFlexProjectsResponse browseFlexProjects() throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.BrowseFlexProjectsResponse ret = impl.browseFlexProjects();
        return ret;
    }

    public br.org.scadabr.api.config.ConfigureDataPointResponse configureDataPoint(br.org.scadabr.api.config.ConfigureDataPointParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.ConfigureDataPointResponse ret = impl.configureDataPoint(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.RemoveDataPointResponse removeDataPoint(br.org.scadabr.api.config.RemoveDataPointParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.RemoveDataPointResponse ret = impl.removeDataPoint(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.BrowseDataPointsResponse browseDataPoints(br.org.scadabr.api.config.BrowseDataPointsParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.BrowseDataPointsResponse ret = impl.browseDataPoints(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.RemoveDataSourceResponse removeDataSource(br.org.scadabr.api.config.RemoveDataSourceParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.RemoveDataSourceResponse ret = impl.removeDataSource(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.ConfigureDataSourceResponse configureDataSource(br.org.scadabr.api.config.ConfigureDataSourceParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.ConfigureDataSourceResponse ret = impl.configureDataSource(parameters);
        return ret;
    }

    public br.org.scadabr.api.config.BrowseDataSourcesResponse browseDataSources(br.org.scadabr.api.config.BrowseDataSourcesParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.config.BrowseDataSourcesResponse ret = impl.browseDataSources(parameters);
        return ret;
    }

    public br.org.scadabr.api.da.GetStatusResponse getStatus() throws java.rmi.RemoteException
    {
        br.org.scadabr.api.da.GetStatusResponse ret = impl.getStatus();
        return ret;
    }

    public br.org.scadabr.api.da.ReadDataResponse readData(br.org.scadabr.api.da.ReadDataParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.da.ReadDataResponse ret = impl.readData(parameters);
        return ret;
    }

    public br.org.scadabr.api.da.WriteDataResponse writeData(br.org.scadabr.api.da.WriteDataParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.da.WriteDataResponse ret = impl.writeData(parameters);
        return ret;
    }

    public br.org.scadabr.api.da.WriteStringDataResponse writeStringData(br.org.scadabr.api.da.WriteStringDataParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.da.WriteStringDataResponse ret = impl.writeStringData(parameters);
        return ret;
    }

    public br.org.scadabr.api.da.BrowseTagsResponse browseTags(br.org.scadabr.api.da.BrowseTagsParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.da.BrowseTagsResponse ret = impl.browseTags(parameters);
        return ret;
    }

    public br.org.scadabr.api.hda.GetDataHistoryResponse getDataHistory(br.org.scadabr.api.hda.GetDataHistoryParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.hda.GetDataHistoryResponse ret = impl.getDataHistory(parameters);
        return ret;
    }

    public br.org.scadabr.api.ae.GetActiveEventsResponse getActiveEvents(br.org.scadabr.api.ae.GetActiveEventsParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.ae.GetActiveEventsResponse ret = impl.getActiveEvents(parameters);
        return ret;
    }

    public br.org.scadabr.api.ae.GetEventsHistoryResponse getEventsHistory(br.org.scadabr.api.ae.GetEventsHistoryParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.ae.GetEventsHistoryResponse ret = impl.getEventsHistory(parameters);
        return ret;
    }

    public br.org.scadabr.api.ae.AckEventsResponse ackEvents(br.org.scadabr.api.ae.AckEventsParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.ae.AckEventsResponse ret = impl.ackEvents(parameters);
        return ret;
    }

    public br.org.scadabr.api.ae.BrowseEventsResponse browseEventsDefinitions(br.org.scadabr.api.ae.BrowseEventsParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.ae.BrowseEventsResponse ret = impl.browseEventsDefinitions(parameters);
        return ret;
    }

    public br.org.scadabr.api.ae.AnnotateEventResponse annotateEvent(br.org.scadabr.api.ae.AnnotateEventParams parameters) throws java.rmi.RemoteException
    {
        br.org.scadabr.api.ae.AnnotateEventResponse ret = impl.annotateEvent(parameters);
        return ret;
    }

}
