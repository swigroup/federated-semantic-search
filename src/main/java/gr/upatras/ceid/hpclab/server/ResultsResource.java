/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.server;

import gr.upatras.ceid.hpclab.response.PrepareResponseWrapper;
import gr.upatras.ceid.hpclab.response.model.Results;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.stream.StreamResult;

/**
 * REST Web Service
 *
 * @author koutsomi
 */
@Path("/")
public class ResultsResource {

    @Context
    private ServletContext context;

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
    @Produces({"application/xml", "application/rdf+xml"})
    public Response getRdf(@QueryParam("q") List<String> query) {
        PrepareResponseWrapper response = new PrepareResponseWrapper();
        final Results  res = response.getResults(query);
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                Transform2RDF.transform(res, new StreamResult(output), context);
            }
        };

        return Response.ok(stream).build();
    }
     
     @Path("/results2")
     @GET
     @Produces({"application/xml", "application/json"})
     public Results getXml(@QueryParam("q") List<String> query) {
     PrepareResponseWrapper response = new PrepareResponseWrapper();
     return response.getResults(query);
     }
     
}
