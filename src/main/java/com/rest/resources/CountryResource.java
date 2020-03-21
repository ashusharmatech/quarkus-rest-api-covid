package com.rest.resources;

import com.rest.services.DataService;
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
@Path("/rest/country")
public class CountryResource {
    private final Logger log = LoggerFactory.getLogger(CountryResource.class);

    @Inject
    private DataService dataService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCountries(@PathParam String country) {
        return ok(dataService.getCountries()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stats")
    public Response getAllCountriesStats(@PathParam String country) {
        return ok(dataService.getCountriesStats()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllByCountry(@PathParam String country) {
        return ok(dataService.getAllByCountryName(country)).header("Access-Control-Allow-Origin", "*").build();
    }

}
