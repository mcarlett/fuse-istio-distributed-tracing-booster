package com.redhat.fuse.boosters.istio.dt;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A simple Camel REST DSL route that implement the greetings service.
 * 
 */
@Component
public class CamelRouter extends RouteBuilder {

    @Value("${nameService.host}")
    String nameServiceHost;

    @Value("${nameService.port}")
    String nameServicePort;

    @Override
    public void configure() throws Exception {

        // @formatter:off
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json);
        
        rest("/greetings").description("Greetings REST service")
            .consumes("application/json")
            .produces("application/json")

            .get().outType(Greetings.class)
                .responseMessage().code(200).endResponseMessage()
                .to("direct:greetingsImpl");

        from("direct:greetingsImpl").description("Greetings REST service implementation route")
            .streamCaching()
            .log(" Try to call name Service")
            .to("http://"+nameServiceHost+":"+nameServicePort+"/camel/name?bridgeEndpoint=true")
            .log(" Successfully called name Service")
            .to("bean:greetingsService?method=getGreetings")
            .log(" Internal route called")
            .to("direct:internal-route");

        from("direct:internal-route").routeId("internal-route")
             .log(" Running internal route")
             .to("seda:a")
             .to("bean:greetingsService?method=doNothing");

        from("seda:a").routeId("a")
                .process((exchange) -> {
                    log.info("route a");
                })
                .to("seda:b")
                .to("seda:c");

        from("seda:b").routeId("b")
                .process((exchange) -> {
                    log.info("route b");
                });

        from("seda:c").routeId("c")
                .process((exchange) -> {
                    log.info("route c");
                });
        // @formatter:on
    }

}
