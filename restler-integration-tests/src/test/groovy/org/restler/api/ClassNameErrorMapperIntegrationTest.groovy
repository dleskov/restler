package org.restler.api

import org.restler.Restler
import org.restler.Service
import org.restler.integration.Controller
import org.restler.spring.mvc.SpringMvcSupport
import org.restler.util.IntegrationSpec
import spock.lang.Specification

import java.sql.SQLException

class ClassNameErrorMapperIntegrationTest extends Specification implements IntegrationSpec {

    Service service = new Restler("http://localhost:8080", new SpringMvcSupport()).
            addEnhancer(new ThrowExceptionByNameEnhancer()).
            build();

    def "IllegalStateException"() {
        given:
        def ctrl = service.produceClient(Controller.class)
        when:
        ctrl.throwException(IllegalStateException.class.getCanonicalName())
        then:
        thrown(IllegalStateException)
    }

    def "SQLException"() {
        given:
        def ctrl = service.produceClient(Controller.class)
        when:
        ctrl.throwException(SQLException.class.getCanonicalName())
        then:
        thrown(SQLException)
    }

}
