package com.rest.services;


import com.rest.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class CoronaVirusDataService {


    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private final Logger log = LoggerFactory.getLogger(CoronaVirusDataService.class);


    private List<LocationStats> allStats = new ArrayList<>();
    private HashMap<String, Integer> countryStats = new HashMap<>();
    private HashMap<String, Integer> sortedCountryStats = new LinkedHashMap<String, Integer>();


    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public HashMap<String, Integer> getCountryStats() {
        return countryStats;
    }

    public HashMap<String, Integer> getTopNCountryData(int n){
        return sortedCountryStats.entrySet().stream()
                .limit(n)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * ? * * *")
    public void initialDataLoader() throws IOException, InterruptedException {
        fetchVirusData();
        updateDataForCountry();
        sortCountryStatsByValue();
    }

    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStat);
        }
        this.allStats = newStats;
    }


    public void updateDataForCountry() {
        for (LocationStats locationStats : allStats) {
            Integer count = countryStats.get(locationStats.getCountry()) == null ? 0 : countryStats.get(locationStats.getCountry());
            countryStats.put(locationStats.getCountry(), count + locationStats.getLatestTotalCases());
        }
    }

    public HashMap<String, Integer> sortCountryStatsByValue()
    {
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(countryStats.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });


        for (Map.Entry<String, Integer> aa : list) {
            sortedCountryStats.put(aa.getKey(), aa.getValue());
        }
        return sortedCountryStats;
    }
}
