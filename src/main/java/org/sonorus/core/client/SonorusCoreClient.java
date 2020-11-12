/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.sonorus.core.dto.SonorusDTO;
import org.sonorus.core.dto.SpeechServicePaths;
import org.hedwig.cloud.response.HedwigResponseCode;

/**
 *
 * @author dgrfiv
 */
public class SonorusCoreClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = SpeechServicePaths.DGRF_SPEECH_BASE_URI+"/"+SpeechServicePaths.DGRF_SPEECH_BASE;

    private SonorusDTO callDGRFSpeechService(SonorusDTO dGRFSpeechDTO) {
        WebTarget resource = webTarget;
        ObjectMapper objectMapper = new ObjectMapper();
        String speechDTOJSON;
        try {
            speechDTOJSON = objectMapper.writeValueAsString(dGRFSpeechDTO);
        } catch (JsonProcessingException ex) {
            dGRFSpeechDTO.setResponseCode(HedwigResponseCode.JSON_FORMAT_PROBLEM);
            return dGRFSpeechDTO;
        }
        Invocation.Builder ib = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON);
        Response response = ib.post(javax.ws.rs.client.Entity.entity(speechDTOJSON, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            Logger.getLogger(SonorusCoreClient.class.getName()).log(Level.SEVERE, "Service connection response"+Integer.toString(response.getStatus()));
            dGRFSpeechDTO.setResponseCode(HedwigResponseCode.SERVICE_CONNECTION_FAILURE);
            return dGRFSpeechDTO;
        }
        String respJSON = response.readEntity(String.class);
        try {
            dGRFSpeechDTO = objectMapper.readValue(respJSON, SonorusDTO.class);
            return dGRFSpeechDTO;
        } catch (IOException ex) {
            Logger.getLogger(SonorusCoreClient.class.getName()).log(Level.SEVERE, null, ex);
            dGRFSpeechDTO.setResponseCode(HedwigResponseCode.JSON_FORMAT_PROBLEM);
            return dGRFSpeechDTO;
        }
    }

    public SonorusDTO convertWavToCsv(SonorusDTO dGRFSpeechDTO) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path(SpeechServicePaths.WAV_TO_CSV_AND_UPLOAD);
        return callDGRFSpeechService(dGRFSpeechDTO);
    }

    public SonorusDTO calculateSpeechEmotion(SonorusDTO dGRFSpeechDTO) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path(SpeechServicePaths.DECIDE_SPEECH_EMO);
        return callDGRFSpeechService(dGRFSpeechDTO);
    }
    
    public SonorusDTO deleteEmoInstance(SonorusDTO dGRFSpeechDTO) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path(SpeechServicePaths.DELETE_SPEECH_EMO);
        return callDGRFSpeechService(dGRFSpeechDTO);
    }
}
