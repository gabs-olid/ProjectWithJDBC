package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.external;

import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.domain.AddressFromBuscaCep;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log4j2
public final class ApiBuscaCep {
    private static final Logger log = LogManager.getLogger(ApiBuscaCep.class);

    public static AddressFromBuscaCep getAddress(String zipCode) {
        URI url = URI.create("https://viacep.com.br/ws/" + zipCode + "/json/");
        try (HttpClient client = HttpClient.newHttpClient();) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return new Gson().fromJson(response.body(), AddressFromBuscaCep.class);
        } catch (Exception e) {
            log.error("Error when trying to find the address using this zip code");
        }
        return null;
    }
}