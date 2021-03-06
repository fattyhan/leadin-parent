/*
 * Copyright (C) 2015-2016 Brother Group Limited
 */
package org.leadin.common.exception;

import org.leadin.common.event.Event;

/**
 * [事件分发异常]
 *
 * @ProjectName: [leadin]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:06]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class EventManagerException extends BaseRuntimeException {

    /**
     * @param msg
     */
    public EventManagerException(String msg, Event event1) {
        super(msg);

        addExceptionEvent(event1);
        //TO DO: 可以选择发往服务器报警
    }
}
