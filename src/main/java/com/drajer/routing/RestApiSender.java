package com.drajer.routing;

import com.drajer.ecrapp.security.AuthorizationService;
import com.drajer.sof.model.LaunchDetails;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestApiSender {

  private final Logger logger = LoggerFactory.getLogger(RestApiSender.class);

  @Autowired private AuthorizationService authorizationService;

  @Autowired private RestTemplate restTemplate;

  public JSONObject sendEicrXmlDocument(LaunchDetails launchDetails, String eicrXml) {
    JSONObject bundleResponse = null;
    URIBuilder ub = null;
    String accessToken = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      logger.info("In Initialization");

      if (authorizationService != null) {
        accessToken = authorizationService.getAuthorizationHeader(launchDetails);
      }

      headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      headers.add("X-Request-ID", launchDetails.getxRequestId());

      if (accessToken != null && !accessToken.isEmpty()) {
        logger.info("Setting Access_token============>" + accessToken);
        headers.add("Authorization", accessToken);
      }

      HttpEntity<String> request = new HttpEntity<String>(eicrXml, headers);
      logger.info(launchDetails.getRestAPIURL());
      ub = new URIBuilder(launchDetails.getRestAPIURL());
      ub.addParameter("fhirServerURL", launchDetails.getEhrServerURL());
      ub.addParameter("patientId", launchDetails.getLaunchPatientId());
      ub.addParameter("encounterId", launchDetails.getEncounterId());

      if (logger.isInfoEnabled()) {
        logger.info("Sending Eicr XML Document to Endpoint::::: {}", ub.toString());
      }

      ResponseEntity<String> response =
          restTemplate.exchange(ub.toString(), HttpMethod.POST, request, String.class);

      bundleResponse = new JSONObject(response.getBody());
      bundleResponse.put("status", response.getStatusCodeValue());

      if (logger.isInfoEnabled()) {
        logger.info("Received response: {}", bundleResponse.toString());
      }

    } catch (Exception e) {
      logger.error("RestAPI Exception", e);

      if (ub != null) {
        logger.error("Error in Sending Eicr XML to Endpoint: {}", ub.toString(), e);
      }
    }
    return bundleResponse;
  }

  /*
   * private String getAccessToken(LaunchDetails launchDetails) { String
   * access_token = ""; RestTemplate restTemplate = new RestTemplate(); JSONObject
   * requestBody = new JSONObject(); requestBody.put("client_id",
   * launchDetails.getClientId()); requestBody.put("client_secret",
   * launchDetails.getClientSecret()); HttpHeaders headers = new HttpHeaders();
   * headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
   * HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(),
   * headers);
   *
   * logger.info( "Sending Authorization request to Endpoint::::: {}",
   * launchDetails.getEhrAuthorizationUrl()); ResponseEntity<String> response =
   * restTemplate.exchange( launchDetails.getEhrAuthorizationUrl(),
   * HttpMethod.POST, request, String.class);
   *
   * JSONObject responseObj = new JSONObject(response.getBody()); access_token =
   * responseObj.getString("access_token");
   * logger.info("received Access_token====>" + access_token); return
   * access_token; }
   */
}
