package com.documentum.performance.ucf.indexer.config;

public interface IConfigElements
{
    public static final String CONFIG_DOCBASE = "docbase";
    public static final String CONFIG_PROTOCOL = "protocol";
    public static final String CONFIG_ROOT_HOST = "host";
    public static final String CONFIG_ROOT_PORT = "port";
    public static final String CONFIG_APPLICATION = "application";

    public static final String CONFIG_ADMIN_USER = "adminuser";
    public static final String CONFIG_ADMIN_PASSWORD = "adminpassword";
    public static final String CONFIG_RAMPUP = "rampup";
    public static final String CONFIG_THINKTIME = "thinktime";

    public static final String CONFIG_LOGFILE = "logfile";
    public static final String CONFIG_LOG_LEVEL = "loglevel";
    public static final String CONFIG_LOG_THROW_OUT = "throwouthighlow";

    public static final String CONFIG_USERS = "users";
    public static final String CONFIG_USER = "user";
    public static final String CONFIG_USER_NAME = "name";
    public static final String CONFIG_USER_PASSWORD = "passwd";
    public static final String CONFIG_USER_ACTION = "action";
    public static final String CONFIG_USER_CHECKOUTDIR = "checkoutdir";
    public static final String CONFIG_USER_EXPORTDIR = "exportdir";
    public static final String CONFIG_USER_IMPORTPATH = "importpath";
    public static final String CONFIG_USER_NETLOCID = "netlocid";             // for DCT checkout
    public static final String CONFIG_USER_PREFERACS = "preferacs";
    public static final String CONFIG_USER_ALLOWBOCS = "allowbocs";
    public static final String CONFIG_USER_SYNCBOCS = "synchronousbocs";
    public static final String CONFIG_PATH = "path";
    public static final String CONFIG_USER_UID = "uid";
    public static final String CONFIG_USER_OBJECTID = "objectid";

    public static final int CONFIG_CHECKOUT = 1;
    public static final int CONFIG_CHECKIN = 2;
    public static final int CONFIG_EXPORT = 4;
    public static final int CONFIG_IMPORT = 8;
    public static final int CONFIG_CANCELCHECKOUT = 16;
    public static final int CONFIG_DCTCHECKOUT = 32;                     // for  DCT checkout
    public static final int CONFIG_DCTCHECKIN = 64;
    public static final int CONFIG_DCTEXPORT = 128;
    public static final int CONFIG_DCTIMPORT = 256;
}
