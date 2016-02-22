package com.inasweaterpoorlyknit.blackflagsharkalarm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class Search {
    // number of videos we want the search function to return
    public static final long NUMBER_OF_VIDEOS_TO_RETURN = 10;

    // global instance of a YouTube object
    private static YouTube youtube;


    // list of SearchResult objects to hold the search results, takes a query and web browser key as arguments
    public static List<SearchResult> Search(String query, String developerKey){
        // try to build a youtube object
        try {

            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search").build();

            // Define the API request for the search results
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(developerKey);

            search.setQ(query);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_TO_RETURN);

            // call api
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if(searchResultList != null){
                return searchResultList;
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}