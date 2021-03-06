/*
 * Copyright (C) 2015-2016 Brother Group Limited
 */
package org.leadin.common.event.events;

import org.leadin.common.constant.EnumEventType;
import org.leadin.common.event.Event;

/**
 * [client运行状态事件,避免耦合，不需要传递事件源]
 *
 * @ProjectName: [leadin]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:28]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class ClientStatusEvent extends Event {
    public ClientStatusEvent() {
        setEventType(EnumEventType.CLIENT_STATUS_EVENT);
    }
}
