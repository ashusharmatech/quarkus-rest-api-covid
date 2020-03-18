package com.rest.resources;

import com.rest.services.CoronaVirusDataService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@ApplicationScoped
@Path("/rest")
public class RestResource {
    private final Logger log = LoggerFactory.getLogger(RestResource.class);

    @Inject
    private CoronaVirusDataService coronaVirusDataService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        return ok(coronaVirusDataService.getAllStats()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/country")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByCountry() {
        return ok(coronaVirusDataService.getCountryStats()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/country/{n}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findTopNByCountry(@PathParam int n) {
        return ok(coronaVirusDataService.getTopNCountryData(n)).header("Access-Control-Allow-Origin", "*").build();
    }

}
