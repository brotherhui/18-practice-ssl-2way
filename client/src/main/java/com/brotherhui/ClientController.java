package com.brotherhui;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.brotherhui.config.TestBody;

@RestController
class ClientController {

	private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);
	
    @Autowired
    private RestOperations restTemplate;
    

    @RequestMapping("/door")
    //http://localhost:8080/door?uri=https://localhost:8443/user
    public String door(@RequestParam String uri) {
    	log.info("I am calling resource");
        return restTemplate.getForObject(uri, String.class);
    }
    
    
    @RequestMapping("/door2")
    //http://localhost:8080/door2
    public String door() {
        URI url = 
//        		UriComponentsBuilder.fromHttpUrl("https://svst-edge-service.ci.npe.ac2.io")
        		UriComponentsBuilder.fromHttpUrl("https://localhost:8443")
        		.path("/ws/PCEHRProfile_Service")
                .build()
                .toUri();

        RequestEntity<String> requestEntity = RequestEntity
                .post(url)
                .header("Content-Type", "application/soap+xml")
                .body(TestBody.GAINPCEHR_BODY);
        String response = "default msg";
        try{
	        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
	        System.out.println("body is "+responseEntity.getBody());
	        response = responseEntity.getBody();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
    	
    	log.info("I am calling edge");
        return response;
    }
}