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

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

/**
 * @author HyunGil Jeong
 */
public class DapengPluginConfig {
    private final boolean traceDapengClient;
    private final boolean traceDapengClientAsync;
    private final boolean traceDapengProcessor;
    private final boolean traceDapengProcessorAsync;
    private final boolean traceDapengServiceArgs;
    private final boolean traceDapengServiceResult;
    
    public DapengPluginConfig(ProfilerConfig src) {
        this.traceDapengClient = src.readBoolean("profiler.dapeng.client", true);
        this.traceDapengClientAsync = src.readBoolean("profiler.dapeng.client.async", true);
        this.traceDapengProcessor = src.readBoolean("profiler.dapeng.processor", true);
        this.traceDapengProcessorAsync = src.readBoolean("profiler.dapeng.processor.async", true);
        this.traceDapengServiceArgs = src.readBoolean("profiler.dapeng.service.args", false);
        this.traceDapengServiceResult = src.readBoolean("profiler.dapeng.service.result", false);
    }
    
    public boolean traceDapengClient() {
        return this.traceDapengClient;
    }

    public boolean traceDapengClientAsync() {
        return this.traceDapengClientAsync;
    }
    
    public boolean traceDapengProcessor() {
        return this.traceDapengProcessor;
    }

    public boolean traceDapengProcessorAsync() {
        return this.traceDapengProcessorAsync;
    }
    
    public boolean traceDapengServiceArgs() {
        return this.traceDapengServiceArgs;
    }
    
    public boolean traceDapengServiceResult() {
        return this.traceDapengServiceResult;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ThriftPluginConfig={traceDapengClient=").append(this.traceDapengClient);
        sb.append(", traceDapengClientAsync=").append(this.traceDapengClientAsync);
        sb.append(", traceDapengProcessor=").append(this.traceDapengProcessor);
        sb.append(", traceDapengProcessorAsync=").append(this.traceDapengProcessorAsync);
        sb.append(", traceDapengServiceArgs=").append(this.traceDapengServiceArgs);
        sb.append(", traceDapengServiceResult=").append(this.traceDapengServiceResult);
        sb.append("}");
        return sb.toString();
    }

}
