package org.restler.api

import org.restler.Restler
import org.restler.integration.Controller
import org.restler.spring.mvc.SpringMvcSupport
import org.restler.util.IntegrationSpec
import spock.lang.Specification

import static org.restler.Tests.login
import static org.restler.Tests.password

class ReauthorizationIntegrationTest extends Specification implements IntegrationSpec {

    def service = new Restler("http://localhost:8080", new SpringMvcSupport()).
            add({ [new ReauthorizingEnhancer(it.securitySession)] }).
            httpBasicAuthentication(login, password).
            build()

    def "test reauthorization"() {
        given:
        def ctrl = service.produceClient(Controller)
        ctrl.logout()
        when:
        def response = ctrl.securedGet()
        then:
        response == "Secure OK"
    }

}
