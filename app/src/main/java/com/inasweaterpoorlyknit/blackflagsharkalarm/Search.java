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

// class used to return search results from YouTube as a List<SearchResult>
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

            // user the YouTube browser developer key passes as the
            search.setKey(developerKey);

            // set the query as the query passed
            search.setQ(query);
            // only search for videos(not playlists or channels)
            search.setType("video");
            // different fields to be returned from the JSON organized results
            // id/kind (should always be "youtube#video")
            // id/videoId = id of video (used to play)
            // snippet/title = title of video (used for our playlist)
            // snippet/thumbnails/default/url = urls of the thumbnails (used for listing results)
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            // we only want NUMBER_OF_VIDEOS_TO_RETURN amount of videos
            search.setMaxResults(NUMBER_OF_VIDEOS_TO_RETURN);

            // call YouTube API to get a search response
            SearchListResponse searchResponse = search.execute();
            // extract the search results from the search response
            List<SearchResult> searchResultList = searchResponse.getItems();
            // only return search results if anything was found
            if(searchResultList != null){
                return searchResultList;
            }
        // handle exceptions
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // return null if search was unsuccessful
        return null;
    }
}
