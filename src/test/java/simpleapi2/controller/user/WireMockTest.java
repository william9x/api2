package simpleapi2.controller.user;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RunWith(SpringRunner.class)
public class WireMockTest {

    RestTemplate restTemplate;
    ResponseEntity response;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule((8081));

    @Before
    public void setup() throws Exception {
        restTemplate = new RestTemplate();
        response = null;
    }

    @Test
    public void givenWireMockAdminEndpoint_whenGetWithoutParams_thenVerifyRequest() {

        RestTemplate restTemplate = new RestTemplate();

        response = restTemplate.getForEntity("http://localhost:8089/__admin", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void givenWireMockEndpoint_whenGetWithoutParams_thenVerifyRequest() {
        wireMockRule.stubFor(get(urlEqualTo("/api/resource/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", TEXT_PLAIN_VALUE)
                        .withBody("test")));

        response = restTemplate.getForEntity("http://localhost:8089/api/resource/", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        verify(getRequestedFor(urlMatching("/api/resource/.*")));
    }

}