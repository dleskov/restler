package org.restler.spring.mvc;

import net.sf.cglib.proxy.InvocationHandler;
import org.restler.client.*;
import org.restler.http.HttpCallExecutor;
import org.restler.http.RequestExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

public class SpringMvc implements CoreModule {

    private final RequestExecutor requestExecutor;
    private final List<CallEnhancer> enhancers;
    private final URI baseUri;
    private final ParameterResolver parameterResolver;

    public SpringMvc(RequestExecutor requestExecutor, List<CallEnhancer> enhancers, URI baseUri, ParameterResolver parameterResolver) {
        this.requestExecutor = requestExecutor;
        this.enhancers = enhancers;
        this.baseUri = baseUri;
        this.parameterResolver = parameterResolver;
    }

    @Override
    public boolean canHandle(ServiceDescriptor descriptor) {
        if (!(descriptor instanceof ClassServiceDescriptor)) {
            return false;
        }
        Class<?> controllerClass = ((ClassServiceDescriptor) descriptor).getServiceDescriptor();
        return controllerClass.getDeclaredAnnotation(Controller.class) != null || controllerClass.getDeclaredAnnotation(RestController.class) != null;
    }

    @Override
    public InvocationHandler createHandler(ServiceDescriptor descriptor) {
        HttpCallExecutor callExecutor = new HttpCallExecutor(requestExecutor);
        CallExecutionChain chain = new CallExecutionChain(callExecutor, enhancers);
        return new CallExecutorInvocationHandler(chain, new SpringMvcMethodInvocationMapper(baseUri, parameterResolver));
    }

}