package com.navercorp.pinpoint.plugin.dapeng;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;

import java.security.ProtectionDomain;

/**
 * @author duwupeng
 */
public class DapengPlugin implements ProfilerPlugin, TransformTemplateAware {

    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {
        this.addApplicationTypeDetector(context);
        this.addTransformers();
    }

    private void addTransformers() {
        transformTemplate.transform("com.isuwang.dapeng.remoting.netty.SoaClient", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                target.getDeclaredMethod("send", "int","io.netty.buffer.ByteBuf").addInterceptor("com.navercorp.pinpoint.plugin.dapeng.interceptor.DapengClientInterceptor");

                return target.toBytecode();
            }
        });
        transformTemplate.transform("com.isuwang.dapeng.container.netty.SoaServerHandler", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                target.getDeclaredMethod("processRequest", "io.netty.channel.ChannelHandlerContext","io.netty.buffer.ByteBuf","com.isuwang.dapeng.container.netty.TSoaTransport","com.isuwang.dapeng.core.TSoaServiceProtocol","com.isuwang.dapeng.core.TransactionContext","Long","com.isuwang.dapeng.monitor.api.domain.PlatformProcessData").addInterceptor("com.navercorp.pinpoint.plugin.dapeng.interceptor.DapengServerHandler");

                return target.toBytecode();
            }
        });
    }

    /**
     * Pinpoint profiler agent uses this detector to find out the service type of current application.
     */
    private void addApplicationTypeDetector(ProfilerPluginSetupContext context) {
        context.addApplicationTypeDetector(new DapengProviderDetector());
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
