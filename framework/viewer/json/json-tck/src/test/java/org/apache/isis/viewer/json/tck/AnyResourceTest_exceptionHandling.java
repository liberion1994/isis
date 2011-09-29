package org.apache.isis.viewer.json.tck;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.apache.isis.runtimes.dflt.webserver.WebServer;
import org.apache.isis.viewer.json.applib.HttpMethod;
import org.apache.isis.viewer.json.applib.JsonRepresentation;
import org.apache.isis.viewer.json.applib.RepresentationType;
import org.apache.isis.viewer.json.applib.RestfulClient;
import org.apache.isis.viewer.json.applib.RestfulRequest;
import org.apache.isis.viewer.json.applib.RestfulResponse;
import org.apache.isis.viewer.json.applib.RestfulResponse.HttpStatusCode;
import org.apache.isis.viewer.json.applib.homepage.HomePageRepresentation;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AnyResourceTest_exceptionHandling {

    @Rule
    public IsisWebServerRule webServerRule = new IsisWebServerRule();
    
    private RestfulClient client;

    @Before
    public void setUp() throws Exception {
        WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void noMediaType() throws Exception {
        // given
        RestfulRequest restfulReq = client.createRequest(HttpMethod.GET, "/");
        
        // when
        RestfulResponse<HomePageRepresentation> restfulResp = restfulReq.executeT();
        
        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.OK));
        assertThat(restfulResp.getHeader(RestfulResponse.Header.CONTENT_TYPE), is(RepresentationType.HOME_PAGE.getMediaType()));
    }

    @Test
    public void correctMediaType() throws Exception {

        // given
        RestfulRequest restfulReq = client.createRequest(HttpMethod.GET, "/");
        restfulReq.getClientRequest().header(HttpHeaderNames.ACCEPT, MediaType.APPLICATION_JSON_TYPE);
        
        // when
        RestfulResponse<HomePageRepresentation> restfulResp = restfulReq.executeT();
        
        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.OK));
        assertThat(restfulResp.getHeader(RestfulResponse.Header.CONTENT_TYPE), is(RepresentationType.HOME_PAGE.getMediaType()));
    }

    
    @Test
    public void incorrectMediaType_returnsNotAcceptable() throws Exception {

        // given
        ClientRequest clientRequest = client.getClientRequestFactory().createRelativeRequest("/");
        clientRequest.accept(MediaType.APPLICATION_ATOM_XML_TYPE);
        
        // when
        ClientResponse<?> resp = clientRequest.get();
        RestfulResponse<JsonRepresentation> restfulResp = RestfulResponse.of(resp);
        
        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.NOT_ACCEPTABLE));
    }


    @Test
    public void runtimeException_isMapped() throws Exception {

        // given
        RestfulRequest restfulReq = client.createRequest(HttpMethod.GET, "capabilities");
        restfulReq.getClientRequest().header("X-FAIL", "true");
        
        // when
        RestfulResponse<JsonRepresentation> jsonResp = restfulReq.execute();
        
        // then
        assertThat(jsonResp.getStatus(), is(HttpStatusCode.METHOD_FAILURE));
    }
}


    