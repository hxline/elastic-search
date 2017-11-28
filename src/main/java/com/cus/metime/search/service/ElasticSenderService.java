package com.cus.metime.search.service;

import com.cus.metime.search.domain.SearchParameter;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Handoyo
 */
public class ElasticSenderService {

    private RestTemplate restTemplate = new RestTemplate();
    private String elasticUrl;
    private SearchParameter searchParameter;

    public ElasticSenderService(String elasticUsername, String elasticPassword, String elasticUrl, SearchParameter searchParameter) {
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(elasticUsername, elasticPassword));
        this.elasticUrl = elasticUrl;
        this.searchParameter = searchParameter;
        try {
            this.sendToElastic();
        } catch (Exception e) {
            e.printStackTrace();
//            throw new Exception(e.getMessage());
        }
    }

    private void sendToElastic() throws URISyntaxException {
        //create mapping for location type
        if (!isIndexExist()) {
            //create index
//            ResponseEntity createResponse = null;
//            try {
//                String jsonStr = "{\"settings\":{\"index\":{\"number_of_shards\":5,\"number_of_replicas\":1}}}";
//                createResponse = restTemplate.exchange(
//                        RequestEntity.put(new URI(elasticUrl + "/" + searchParameter.getServiceName().toLowerCase())).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(jsonStr), Object.class);
//            } catch (Exception e) {
//            } finally {
//                if (createResponse == null) {
//                    System.out.println("Index Creating Status : Error");
//                } else {
//                    System.out.println("Index Creating Status : " + createResponse.getStatusCode());
//                }
//            }

            //create mapping of geo location if it has it
            if (isTypeUseGeolocation()) {
                String jsonStr = "{\"mappings\":{\"" + searchParameter.getType().toLowerCase() + "\":{\"properties\":{\"location\":{\"type\":\"geo_point\"}}}}}";
                System.out.println(jsonStr);
                ResponseEntity response = null;
                try {
                    response = restTemplate.exchange(
                            RequestEntity.put(new URI(elasticUrl + "/" + searchParameter.getServiceName().toLowerCase())).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(jsonStr), Object.class);
                } catch (Exception e) {
                } finally {
                    if (response == null) {
                        System.out.println("Index Creating Mapping Status : Error");
                    } else {
                        System.out.println("Index Creating Mapping Status : " + response.getStatusCode());
                    }
                }
            }
        }

        //send parameter to elastic
        ResponseEntity response = null;
        try {
            String uri = elasticUrl + "/"
                    + searchParameter.getServiceName().toLowerCase()
                    + "/" + searchParameter.getType().toLowerCase()
                    + "/" + searchParameter.getId();
            response = restTemplate.exchange(
                    RequestEntity.post(new URI(uri)).contentType(MediaType.APPLICATION_JSON).body(searchParameter.toJsonParam()), Object.class);
        } catch (Exception e) {
        } finally {
            if (response == null) {
                System.out.println("Parameter Creating Status : Error");
            } else {
                System.out.println("Parameter Creating Status : " + response.getStatusCode());
            }
        }
    }

    private boolean isTypeUseGeolocation() {
        if (searchParameter.getLatitude() == null
                || searchParameter.getLongitude() == null) {
            return false;
        }

        return true;
    }

    //check if index has exist in elastic
    //cant catch the error exception
    //if the response is null, it must be thrown an error
    private boolean isIndexExist() {
        ResponseEntity response = null;
        try {
            response = restTemplate.exchange(
                    elasticUrl + "/" + searchParameter.getServiceName().toLowerCase(),
                    HttpMethod.HEAD, null, Object.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        } catch (RestClientException e) {
            System.out.println("----Rest Client Exception----");
            System.out.println("----" + e.getMessage() + "----");
            return false;
        }

        if (response == null) {
            return false;
        } else {
            return true;
        }

    }

}
