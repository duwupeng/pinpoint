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

package com.navercorp.pinpoint.plugin.dapeng.interceptor.client;

import static com.navercorp.pinpoint.plugin.dapeng.DapengScope.DAPENG_CLIENT_SCOPE;

import com.isuwang.dapeng.remoting.BaseServiceClient;
import com.isuwang.org.apache.thrift.TBase;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.Scope;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;


import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.Name;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.StringUtils;
import com.navercorp.pinpoint.plugin.dapeng.DapengUtils;
import com.navercorp.pinpoint.plugin.dapeng.DapengConstants;
import com.navercorp.pinpoint.plugin.dapeng.DapengRequestProperty;

/**
 * Starting point for tracing synchronous client calls for dapeng services.
 * <p>
 * Note that in order to trace remote agents, trace data must be sent to them. These data are serialized as dapeng fields and attached to the body of the dapeng
 * message by other interceptors down the chain.
 * <p>
 * <b><tt>TServiceClientSendBaseInterceptor</tt></b> -> <tt>TProtocolWriteFieldStopInterceptor</tt>
 * <p>
 * Based on dapeng 0.8.0+
 * 
 * @author HyunGil Jeong
 * 
 */
@Scope(value = DAPENG_CLIENT_SCOPE, executionPolicy = ExecutionPolicy.BOUNDARY)
public class DapengServiceClientSendBaseInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;
    private final MethodDescriptor descriptor;
    private final InterceptorScope scope;

    private final boolean traceServiceArgs;

    public DapengServiceClientSendBaseInterceptor(TraceContext traceContext, MethodDescriptor descriptor, @Name(DAPENG_CLIENT_SCOPE) InterceptorScope scope,
                                                  boolean traceServiceArgs) {
        this.traceContext = traceContext;
        this.descriptor = descriptor;
        this.scope = scope;
        this.traceServiceArgs = traceServiceArgs;
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }
        if (target instanceof BaseServiceClient) {
            BaseServiceClient client = (BaseServiceClient)target;
            final Trace trace = traceContext.currentRawTraceObject();
            if (trace == null) {
                return;
            }
            DapengRequestProperty parentTraceInfo = new DapengRequestProperty();
            final boolean shouldSample = trace.canSampled();
            if (!shouldSample) {
                if (isDebug) {
                    logger.debug("set Sampling flag=false");
                }
                parentTraceInfo.setShouldSample(shouldSample);
            } else {
                SpanEventRecorder recorder = trace.traceBlockBegin();
                recorder.recordServiceType(DapengConstants.DAPENG_CLIENT);

                // retrieve connection information
                String remoteAddress = DapengConstants.UNKNOWN_ADDRESS;

                recorder.recordDestinationId(remoteAddress);

                String methodName = DapengConstants.UNKNOWN_METHOD_NAME;
                if (args[0] instanceof String) {
                    methodName = (String)args[0];
                }
                String serviceName = DapengUtils.getClientServiceName(client);

                String dapengUrl = getServiceUrl(remoteAddress, serviceName, methodName);
                recorder.recordAttribute(DapengConstants.DAPENG_URL, dapengUrl);

                TraceId nextId = trace.getTraceId().getNextTraceId();
                recorder.recordNextSpanId(nextId.getSpanId());

                parentTraceInfo.setTraceId(nextId.getTransactionId());
                parentTraceInfo.setSpanId(nextId.getSpanId());
                parentTraceInfo.setParentSpanId(nextId.getParentSpanId());

                parentTraceInfo.setFlags(nextId.getFlags());
                parentTraceInfo.setParentApplicationName(traceContext.getApplicationName());
                parentTraceInfo.setParentApplicationType(traceContext.getServerTypeCode());
                parentTraceInfo.setAcceptorHost(remoteAddress);
            }
            InterceptorScopeInvocation currentTransaction = this.scope.getCurrentInvocation();
            currentTransaction.setAttachment(parentTraceInfo);
        }
    }

    private String getServiceUrl(String url, String serviceName, String methodName) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("/").append(serviceName).append("/").append(methodName);
        return sb.toString();
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        Trace trace = this.traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            if (this.traceServiceArgs) {
                if (args.length == 2 && (args[1] instanceof TBase)) {
                    recorder.recordAttribute(DapengConstants.DAPENG_ARGS, getMethodArgs((TBase<?, ?>)args[1]));
                }
            }
            recorder.recordApi(descriptor);
            recorder.recordException(throwable);
        } finally {
            trace.traceBlockEnd();
        }
    }

    private String getMethodArgs(TBase<?, ?> args) {
        return StringUtils.drop(args.toString(), 256);
    }

}
