package com.documentum.qa.tools.ucf;
/*
 * Copyright ?1994-2004. EMC Corporation.  All Rights Reserved.
 */

public class TDfUCFRequestConfig
{
    public String getUcfMode ()
    {
        return ucfMode;
    }

    public void setUcfMode (String ucfMode)
    {
        this.ucfMode = ucfMode;
    }

    public String getHost ()
    {
        return host;
    }

    public void setHost (String host)
    {
        this.host = host;
    }

    public int getPort ()
    {
        return port;
    }

    public void setPort (int port)
    {
        this.port = port;
    }

    public String getAppRoot ()
    {
        return appRoot;
    }

    public void setAppRoot (String appRoot)
    {
        this.appRoot = appRoot;
    }

    public String getJsessionID ()
    {
        return jsessionID;
    }

    public void setJsessionID (String jsessionID)
    {
        this.jsessionID = jsessionID;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    private String ucfMode;
    private String host;
    private int port;
    private String appRoot;
    private String jsessionID;
    private String message;
}
