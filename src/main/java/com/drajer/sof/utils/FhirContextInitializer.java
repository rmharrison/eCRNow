package com.drajer.sof.utils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.drajer.sof.model.LaunchDetails;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FhirContextInitializer {

  private static final String DSTU2 = "DSTU2";
  private static final String DSTU2_1 = "DSTU2_1";
  private static final String DSTU3 = "DSTU3";
  private static final String R4 = "R4";
  private static final String QUERY_PATIENT = "?patient=";

  private static final Logger logger = LoggerFactory.getLogger(FhirContextInitializer.class);

  /**
   * Get FhirContext appropriate to fhirVersion
   *
   * @param fhirVersion The FHIR Version to use, either as a fhir version or a package name.
   * @return The appropriate FhirContext to use for the server
   */
  public FhirContext getFhirContext(String fhirVersion) {
    switch (fhirVersion) {
      case DSTU2:
        return FhirContext.forDstu2();
      case DSTU2_1:
        return FhirContext.forDstu2_1();
      case DSTU3:
        return FhirContext.forDstu3();
      case R4:
        return FhirContext.forR4();
      default:
        return FhirContext.forDstu2();
    }
  }

  /**
   * Creates a GenericClient with standard intercepters used throughout the services.
   *
   * @param url the base URL of the FHIR server to connect to
   * @param access_token the name of the key to use to generate the token
   * @return a Generic Client
   */
  public IGenericClient createClient(FhirContext context, String url, String accessToken) {
    logger.info("Initializing the Client");
    IGenericClient client = context.newRestfulGenericClient(url);
    // client.setLogRequestAndResponse(true);
    context.getRestfulClientFactory().setSocketTimeout(30 * 1000);
    client.registerInterceptor(new BearerTokenAuthInterceptor(accessToken));
    logger.info("Initialized the Client");
    return client;
  }

  public IBaseResource getResouceById(
      LaunchDetails authDetails,
      IGenericClient genericClient,
      FhirContext context,
      String resourceName,
      String resourceId) {
    IBaseResource resource = null;
    try {
      logger.info("Getting {} data", resourceName);
      resource = genericClient.read().resource(resourceName).withId(resourceId).execute();
      // logger.info(resourceName + ":::::::::::::::::" +
      // context.newJsonParser().encodeResourceToString(resource));
    } catch (Exception e) {
      logger.error("Error in getting " + resourceName + " resource by Id: " + resourceId);
    }
    return resource;
  }

  protected IBaseBundle getResourceByPatientId(
      LaunchDetails authDetails,
      IGenericClient genericClient,
      FhirContext context,
      String resourceName) {
    IBaseBundle bundleResponse = null;
    String url =
        authDetails.getEhrServerURL()
            + "/"
            + resourceName
            + QUERY_PATIENT
            + authDetails.getLaunchPatientId();
    bundleResponse = getResourceBundleByUrl(authDetails, genericClient, context, resourceName, url);
    return bundleResponse;
  }

  protected IBaseBundle getObservationByPatientId(
      LaunchDetails authDetails,
      IGenericClient genericClient,
      FhirContext context,
      String resourceName,
      String category) {
    IBaseBundle bundleResponse = null;
    String url =
        authDetails.getEhrServerURL()
            + "/"
            + resourceName
            + QUERY_PATIENT
            + authDetails.getLaunchPatientId()
            + "&category="
            + category;
    bundleResponse = getResourceBundleByUrl(authDetails, genericClient, context, resourceName, url);
    return bundleResponse;
  }

  protected IBaseBundle getObservationByPatientIdAndCode(
      LaunchDetails authDetails,
      IGenericClient genericClient,
      FhirContext context,
      String resourceName,
      String code,
      String system) {
    IBaseBundle bundleResponse = null;
    String url =
        authDetails.getEhrServerURL()
            + "/"
            + resourceName
            + QUERY_PATIENT
            + authDetails.getLaunchPatientId()
            + "&code="
            + system
            + "|"
            + code;
    bundleResponse = getResourceBundleByUrl(authDetails, genericClient, context, resourceName, url);
    return bundleResponse;
  }

  public static IBaseBundle getResourceBundleByUrl(
      LaunchDetails authDetails,
      IGenericClient genericClient,
      FhirContext context,
      String resourceName,
      String url) {
    logger.info("Invoking url::::::::::::::: {}", url);
    IBaseBundle bundleResponse = null;
    try {
      logger.info(
          "Getting {} data using Patient Id: {}", resourceName, authDetails.getLaunchPatientId());
      if (authDetails.getFhirVersion().equalsIgnoreCase(DSTU2) && logger.isInfoEnabled()) {
        bundleResponse = genericClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        Bundle bundle = (Bundle) bundleResponse;
        logger.info(
            "Total No of {} received::::::::::::::::: {}", resourceName, bundle.getEntry().size());
      } else if (authDetails.getFhirVersion().equalsIgnoreCase(R4) && logger.isInfoEnabled()) {
        bundleResponse =
            genericClient
                .search()
                .byUrl(url)
                .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
                .execute();
        org.hl7.fhir.r4.model.Bundle bundle = (org.hl7.fhir.r4.model.Bundle) bundleResponse;
        logger.info(
            "Total No of {} received::::::::::::::::: {}", resourceName, bundle.getEntry().size());
      }
    } catch (Exception e) {
      logger.info(
          "Error in getting "
              + resourceName
              + " resource by Patient Id: "
              + authDetails.getLaunchPatientId());
    }

    return bundleResponse;
  }
}
