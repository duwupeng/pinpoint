package com.navercorp.pinpoint.plugin.dapeng;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyFactory;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;

/**
 * @author Jinkai.Ma
 */
public interface DapengConstants {

    ServiceType DAPENG_PROVIDER_SERVICE_TYPE = ServiceTypeFactory.of(1115, "DAPENG_PROVIDER", RECORD_STATISTICS);
    ServiceType DAPENG_CONSUMER_SERVICE_TYPE = ServiceTypeFactory.of(9115, "DAPENG_CONSUMER", RECORD_STATISTICS);
    AnnotationKey DAPENG_ARGS_ANNOTATION_KEY = AnnotationKeyFactory.of(95, "dapeng.args");
    AnnotationKey DAPENG_RESULT_ANNOTATION_KEY = AnnotationKeyFactory.of(96, "dapeng.result");

    String META_DO_NOT_TRACE = "_DAPENG_DO_NOT_TRACE";
    String META_TRANSACTION_ID = "_DAPENG_TRASACTION_ID";
    String META_SPAN_ID = "_DAPENG_SPAN_ID";
    String META_PARENT_SPAN_ID = "_DAPENG_PARENT_SPAN_ID";
    String META_PARENT_APPLICATION_NAME = "_DAPENG_PARENT_APPLICATION_NAME";
    String META_PARENT_APPLICATION_TYPE = "_DAPENG_PARENT_APPLICATION_TYPE";
    String META_FLAGS = "_DAPENG_FLAGS";

    String MONITOR_SERVICE_FQCN = "com.alibaba.dubbo.monitor.MonitorService";
}
