/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.server;

import gr.upatras.ceid.hpclab.response.PrepareResponseWrapper;
import gr.upatras.ceid.hpclab.response.model.Results;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author koutsomi
 */
@Path("/")
public class ResultsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ResultsResource
     */
    public ResultsResource() {
    }

    /**
     * Retrieves representation of an instance of
     * gr.upatras.ceid.hpclab.server.ResultsResource
     * @param query
     * @return an instance of gr.upatras.ceid.hpclab.response.model.Results
     */
    
    @Path("/results{p: (/update)?}")
    @GET
    @Produces({"application/xml", "application/json"})
    public Results getXml(@QueryParam("q") List<String> query) {
        PrepareResponseWrapper response = new PrepareResponseWrapper();
        return response.getResults(query);
    }
   /* 
    @Path("/update")
    @GET
    @Produces({"application/xml", "application/rdf+xml"})
    public Results getRdf(@QueryParam("q") List<String> query) {
        PrepareResponseWrapper response = new PrepareResponseWrapper();
        return response.getResults(query);
    }
*/
}
