/*
 * Copyright 2015 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.dapeng;


import com.navercorp.pinpoint.plugin.thrift.ThriftHeader;

/**
 * @author HyunGil Jeong
 */
public class DapengClientCallContext {
    
    public static final com.navercorp.pinpoint.plugin.thrift.ThriftHeader NONE = null;

    private final String methodName;

    private DapengHeader traceHeaderToBeRead;

    private DapengRequestProperty traceHeader;

    public DapengClientCallContext(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public DapengHeader getTraceHeaderToBeRead() {
        return traceHeaderToBeRead;
    }

    public void setTraceHeaderToBeRead(DapengHeader traceHeaderToBeRead) {
        this.traceHeaderToBeRead = traceHeaderToBeRead;
    }

    public DapengRequestProperty getTraceHeader() {
        return traceHeader;
    }

    public void setTraceHeader(DapengRequestProperty traceHeader) {
        this.traceHeader = traceHeader;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DapengClientCallContext={methodName=").append(this.methodName);
        sb.append(", traceHeaderToBeRead=").append(this.traceHeaderToBeRead.name());
        sb.append(", traceHeader=").append(this.traceHeader.toString());
        sb.append("}");
        return sb.toString();
    }
    
    

}
