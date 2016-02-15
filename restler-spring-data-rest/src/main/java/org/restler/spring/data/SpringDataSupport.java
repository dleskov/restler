package org.restler.spring.data;

import com.fasterxml.jackson.databind.Module;
import org.restler.RestlerConfig;
import org.restler.client.CallEnhancer;
import org.restler.client.CoreModule;
import org.restler.http.RequestExecutor;
import org.restler.spring.data.chain.ChainCallEnhancer;
import org.restler.spring.data.proxy.ProxyCallEnhancer;
import org.restler.spring.data.proxy.ProxyCashingCallEnhancer;
import org.restler.spring.mvc.SpringMvcRequestExecutor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpringDataSupport implements Function<RestlerConfig, CoreModule> {

    private final List<Module> jacksonModules = new ArrayList<>();

    private Optional<RequestExecutor> requestExecutor = Optional.empty();

    @Override
    public CoreModule apply(RestlerConfig config) {
        List<CallEnhancer> totalEnhancers = new ArrayList<>();
        totalEnhancers.add(new ChainCallEnhancer());
        totalEnhancers.add(new ProxyCallEnhancer(config));
        totalEnhancers.add(new ProxyCashingCallEnhancer());
        totalEnhancers.addAll(config.getEnhancers());

        return new SpringData(config.getBaseUri(), requestExecutor.orElseGet(this::createExecutor),  totalEnhancers);
    }

    public SpringDataSupport addJacksonModule(Module module) {
        jacksonModules.add(module);
        return this;
    }

    public SpringDataSupport requestExecutor(RequestExecutor requestExecutor) {
        this.requestExecutor = Optional.of(requestExecutor);
        return this;
    }

    private SpringMvcRequestExecutor createExecutor() {
        RestTemplate restTemplate = new RestTemplate();

        List<MappingJackson2HttpMessageConverter> jacksonConverters = restTemplate.getMessageConverters().stream().
                filter(converter -> converter instanceof MappingJackson2HttpMessageConverter).
                map(converter -> (MappingJackson2HttpMessageConverter) converter).
                collect(Collectors.toList());

        jacksonModules.stream().forEach(module ->
                jacksonConverters.forEach(converter ->
                        converter.getObjectMapper().registerModule(module)));

        restTemplate.getMessageConverters().add(0, new SpringDataRestMessageConverter());
        return new SpringMvcRequestExecutor(restTemplate);
    }

}
