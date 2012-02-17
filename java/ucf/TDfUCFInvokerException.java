package com.documentum.qa.tools.ucf;
/*
 * Copyright ?1994-2004. EMC Corporation.  All Rights Reserved.
 */

/**
 * Thrown to indicate that the UCF invoker ran into an issue.
 */
public class TDfUCFInvokerException extends Exception
{
    /**
     * Creates an exception to indicate that the invoker aborted.
     */
    public TDfUCFInvokerException (String message)
    {
        super(message);
    }

    /**
     * Creates an exception to indicate that the invoker aborted.
     *
     * @param cause the cause of the exceptin
     */
    public TDfUCFInvokerException (String message, Throwable cause)
    {
        super(message, cause);
    }
}
