/**
 * Copyright (C) 2015-2016 Brother Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leadin.common.cache.impl;

import org.leadin.common.bean.BaseBean;
import org.leadin.common.cache.ICache;
import org.leadin.common.constant.EnumEventType;
import org.leadin.common.event.Event;
import org.leadin.common.event.events.CollectFinishedEvent;
import org.leadin.common.event.events.DataCollectedEvent;
import org.leadin.common.event.events.DataReceivedEvent;
import org.leadin.common.exception.CacheException;
import org.leadin.common.util.GuidGenerator;
import org.leadin.common.util.LogUtil;
import org.leadin.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [内存缓存接口实现类]
 * 进行缓存数据到内存的读取和写入，修改
 * @ProjectName: [leadin]
 * @Author: [lixu]
 * @CreateDate: [2015/3/1 22:46]
 * @Update: [说明本次修改内容] BY[lixu][2015/3/4]
 * @Version: [v1.0]
 */
public class MemoryCache implements ICache<String> {
    public static Map<String, String> dataMap = new HashMap<String, String>();
    /**
     * 将事件数据加入到内存中
     * @param object
     * @return
     * @throws CacheException
     */
    @Override
    public String add(Event<String> object) throws CacheException {
        String cacheNo = object.getEventNo();
        EnumEventType eventType = object.getEventType();
        String cacheData = object.getEventObject();
        if (StringUtil.isBlank(cacheNo)) {
            while (true) {
                String guid = GuidGenerator.getGUID();
                if (!isExistCacheData(guid, eventType))
                    dataMap.put(getCacheName(guid, eventType), cacheData);
                return guid;
            }
        } else {
            if (!isExistCacheData(cacheNo, eventType)) {
                dataMap.put(getCacheName(cacheNo, eventType), cacheData);
            } else {
                throw new CacheException(StringUtil.join("Event No:[", cacheNo, "] is already exists in the memory ,cannot crate new cache!"));
            }
            return cacheNo;
        }
    }
    @Override
    public void remove(Event<String> event) {
        String eventNo = event.getEventNo();
        EnumEventType eventType = event.getEventType();
        if (!StringUtil.isBlank(eventNo)) {
            dataMap.remove(getCacheName(eventNo, eventType));
        } else {
            throw new CacheException(StringUtil.join("Event No is null ,cannot delete this cache!"));
        }
    }

    @Override
    public Event<String> get(String cacheNo , EnumEventType eventType , BaseBean baseBean) {
        try {
            if (dataMap.containsKey(cacheNo+"_"+eventType.name())) {
                Event<String> event = null;
                return event;
            }
            return null;
        }catch(Exception e){
          throw e;
        }
    }

    @Override
    public List<Event<String>> getAll() {
        List<Event<String>> events = new ArrayList<Event<String>>();
        for(Map.Entry<String , String> entry : dataMap.entrySet()){
            try {
                String cacheName = entry.getKey();
                int size = cacheName.indexOf("_", 0);
                String cacheNo = cacheName.substring(0, size);
                EnumEventType eventType = EnumEventType.valueOf(cacheName.substring(size + 1));
                String cacheData = entry.getValue();
                Event<String> event = getEventObject(eventType, cacheNo, cacheData);
                events.add(event);
            }catch(Exception e){
                LogUtil.err("analysis the key:{} error��reason is ->{}",entry.getKey() , e.getMessage());
                throw new CacheException(StringUtil.join("analysis the key{}:",entry.getKey(),"error, the reason is:",e.getMessage()));
            }
        }
        return events;
    }

    @Override
    public boolean rename(String eventNo, EnumEventType targetEventType, EnumEventType newEventType , String prefix) {
        String targetCacheName = getCacheName(eventNo,targetEventType);
        String newCacheName = getCacheName(eventNo,newEventType);
        if(dataMap.containsKey(targetCacheName)){
            if(dataMap.containsKey(newCacheName)) {
                LogUtil.err("the memory is already include the key :{}, please check the data", newCacheName);
                return false;
            }
            dataMap.put(newCacheName,dataMap.get(targetCacheName));
            dataMap.remove(targetCacheName);
            return true;
        }
        LogUtil.err("the memory isnot include the key:{}", targetCacheName);
        return false;
    }

    private boolean isExistCacheData(String cacheNo , EnumEventType eventType){
        String cacheKey = StringUtil.join(cacheNo, "_", eventType.name());
        if (dataMap.containsKey(cacheKey)) {
            LogUtil.info("the memory include the key:{}", cacheKey);
            return true;
        }
        LogUtil.info("the memory isnot include the key:{}", cacheKey);
        return false;
    }
    /**
     * 获取内存中缓存数据的Key值
     *
     * @param cacheNo
     * @param eventType
     * @return
     */
    private String getCacheName(String cacheNo, EnumEventType eventType) {
        return StringUtil.join(cacheNo, "_", eventType.name());
    }

    /**
     * 将内存中缓存数据封装成事件对象
     * @param eventType
     * @param cacheNo
     * @param data
     * @return
     */
    private Event<String> getEventObject(EnumEventType eventType , String cacheNo , String data){
        Event<String> event  = null;
        switch(eventType){
            case DATA_COLLECTED_EVENT:   event = new DataCollectedEvent();break;
            case COLLECT_FINISHED_EVENT: event = new CollectFinishedEvent();break;
            case DATA_RECEIVED_EVENT:    event = new DataReceivedEvent(); break;
            default: return null;
        }
        event.setEventNo(cacheNo);
        event.setEventObject(data);
        return event;
    }

}
