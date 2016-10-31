package com.navercorp.pinpoint.plugin.dapeng.interceptor;

import com.isuwang.dapeng.remoting.netty.SoaClient;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor2;
import com.navercorp.pinpoint.plugin.dapeng.DapengConstants;
/**
 * @author duwupeng
 */
public class DapengClientInterceptor implements AroundInterceptor2 {

    private final MethodDescriptor descriptor;
    private final TraceContext traceContext;

    public DapengClientInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this.descriptor = descriptor;
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object arg0, Object arg1) {
        Trace trace = this.getTrace(target);
        if (trace == null) {
            return;
        }

//        SoaClient soaClient = (SoaClient) arg0;

        if (trace.canSampled()) {
            SpanEventRecorder recorder = trace.traceBlockBegin();

            // RPC call trace have to be recorded with a service code in RPC client code range.
            recorder.recordServiceType(DapengConstants.DAPENG_CONSUMER_SERVICE_TYPE);

            // You have to issue a TraceId the receiver of this request will use.
            TraceId nextId = trace.getTraceId().getNextTraceId();

            // Then record it as next span id.
            recorder.recordNextSpanId(nextId.getSpanId());

            // Finally, pass some tracing data to the server.
            // How to put them in a message is protocol specific.
            // This example assumes that the target protocol message can include any metadata (like HTTP headers).
            //invocation.setAttachment(DapengConstants.META_TRANSACTION_ID, nextId.getTransactionId());
            //invocation.setAttachment(DapengConstants.META_SPAN_ID, Long.toString(nextId.getSpanId()));
            //invocation.setAttachment(DapengConstants.META_PARENT_SPAN_ID, Long.toString(nextId.getParentSpanId()));
            //invocation.setAttachment(DapengConstants.META_PARENT_APPLICATION_TYPE, Short.toString(traceContext.getServerTypeCode()));
            //invocation.setAttachment(DapengConstants.META_PARENT_APPLICATION_NAME, traceContext.getApplicationName());
            //invocation.setAttachment(DapengConstants.META_FLAGS, Short.toString(nextId.getFlags()));
        }
    }

    @Override
    public void after(Object target, Object arg0, Object arg1, Object result, Throwable throwable) {
        Trace trace = this.getTrace(target);
        if (trace == null) {
            return;
        }

        SoaClient soaClient = (SoaClient) arg0;

        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();

            recorder.recordApi(descriptor);

            if (throwable == null) {
                String endPoint = (String)SoaClient.class.getDeclaredField("host").get(soaClient);
                // RPC client have to record end point (server address)
                recorder.recordEndPoint(endPoint);
                // Optionally, record the destination id (logical name of server. e.g. DB name)
                recorder.recordDestinationId(endPoint);
                recorder.recordAttribute(DapengConstants.DAPENG_ARGS_ANNOTATION_KEY, "seqid: "+arg0 +" request: " + arg1);
                recorder.recordAttribute(DapengConstants.DAPENG_RESULT_ANNOTATION_KEY, result);
            } else {
                recorder.recordException(throwable);
            }
        } catch (Exception e){} finally {
            trace.traceBlockEnd();
        }
    }

    private Trace getTrace(Object target) {
//        Invoker invoker = (Invoker) target;
//        // Ignore monitor service.
//        if (DapengConstants.MONITOR_SERVICE_FQCN.equals(invoker.getInterface().getName())) {
//            return null;
//        }
        return traceContext.currentTraceObject();
    }

}
