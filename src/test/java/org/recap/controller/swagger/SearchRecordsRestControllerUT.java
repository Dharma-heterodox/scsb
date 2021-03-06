package org.recap.controller.swagger;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.SearchRecordsRequest;
import org.recap.model.SearchRecordsResponse;
import org.recap.model.SearchResultRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 3/2/17.
 */
public class SearchRecordsRestControllerUT extends BaseTestCase{

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClient;

    @Mock
    RestTemplate mockRestTemplate;

    @Mock
    SearchRecordsRestController searchRecordsRestController;

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public String getScsbSolrClient() {
        return scsbSolrClient;
    }

    public void setScsbSolrClient(String scsbSolrClient) {
        this.scsbSolrClient = scsbSolrClient;
    }

    @Test
    public void testSearchRecordService(){
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldValue("test");
        searchRecordsRequest.setFieldName("test");
        searchRecordsRequest.setTotalPageCount(3);
        searchRecordsRequest.setAvailability(Arrays.asList("Available"));
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        HttpEntity<SearchRecordsRequest> httpEntity = new HttpEntity<>(searchRecordsRequest, getHttpHeaders());
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        searchRecordsResponse.setTotalPageCount(3);
        ResponseEntity<SearchRecordsResponse> responseEntity = new ResponseEntity<SearchRecordsResponse>(searchRecordsResponse,HttpStatus.OK);
        Mockito.when(mockRestTemplate.exchange(serverProtocol+scsbSolrClient+ ReCAPConstants.URL_SEARCH_BY_JSON, HttpMethod.POST, httpEntity, SearchRecordsResponse.class)).thenReturn(responseEntity);
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(searchRecordsRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest)).thenCallRealMethod();
        SearchRecordsResponse recordsResponse = searchRecordsRestController.searchRecordsServiceGetParam(searchRecordsRequest);
        assertNotNull(recordsResponse);
    }

    @Test
    public void testSearchRecordServiceGet(){
        HttpEntity request = new HttpEntity(getHttpHeaders());
        List<SearchResultRow> searchResultRowList = new ArrayList<>();
        ResponseEntity<List> httpEntity = new ResponseEntity<List>(searchResultRowList,HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverProtocol + scsbSolrClient + ReCAPConstants.URL_SEARCH_BY_PARAM)
                .queryParam("fieldValue", "test")
                .queryParam("fieldName", "test")
                .queryParam("owningInstitutions", "PUL")
                .queryParam("collectionGroupDesignations","Shared")
                .queryParam("availability","Available")
                .queryParam("materialTypes","Monograph")
                .queryParam("useRestrictions","NoRestrictions")
                .queryParam("pageSize", 10);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class)).thenReturn(httpEntity);
        Mockito.when(searchRecordsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(searchRecordsRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(searchRecordsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10)).thenCallRealMethod();
        List<SearchResultRow> searchResultRows= searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10);
        assertNotNull(searchResultRows);

    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}