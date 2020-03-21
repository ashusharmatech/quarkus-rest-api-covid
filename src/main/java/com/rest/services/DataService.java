package com.rest.services;


import com.rest.models.CountryStats;
import com.rest.models.DateCountryStats;
import io.quarkus.scheduler.Scheduled;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class DataService {


    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private final Logger log = LoggerFactory.getLogger(DataService.class);

    private HashMap<String,TreeMap<Date, DateCountryStats>> countryWiseDataMap = new HashMap<>();
    private HashMap<String, CountryStats> countryStatsMap = new HashMap<>();
    private List<DateCountryStats> allData = new ArrayList<>();


    List<String> headerNames = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");

    @PostConstruct
    @Scheduled(cron = "0 0 * ? * * *")
    public void initialDataLoader() throws IOException, InterruptedException, ParseException {

        log.info("Calling initial data load");
        updateCountryData();
        updateCountrywideData();
    }

    private void updateCountryData() throws IOException, InterruptedException, ParseException {

        log.info("Calling updateCountryData");

        HashMap<String, CountryStats> tempStats = new HashMap<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");


        for (CSVRecord record : records) {
            //log.info("CSV RECORD IS "+record.toString());
            if (headerNames.isEmpty()) {
                headerNames = record.getParser().getHeaderNames();
            }
            String country = record.get("Country/Region");
            String state = record.get("Province/State");
            String latitude = record.get("Lat");
            String longitude = record.get("Long");
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            CountryStats existingCountryStats = tempStats.get(country);
            if (existingCountryStats == null) {
                CountryStats countryStats = new CountryStats();
                countryStats.setCountry(country);
                countryStats.setLatestTotalCases(latestCases);
                countryStats.setDiffFromPrevDay(latestCases - prevDayCases);
                tempStats.put(country, countryStats);
            } else {
                existingCountryStats.setLatestTotalCases(existingCountryStats.getLatestTotalCases() + latestCases);
                existingCountryStats.setDiffFromPrevDay(existingCountryStats.getDiffFromPrevDay() + (latestCases - prevDayCases));
            }
            createDateStats(record,state,country,latitude,longitude);

        }
        this.countryStatsMap = tempStats;
        log.info(" country stats map has "+countryStatsMap.size());
    }


    public void createDateStats(CSVRecord record, String state, String country, String latitude, String longitude) throws ParseException {
        for (int i = 4; i < record.size() - 1; i++) {
            String strDate = headerNames.get(i);
            Date date = simpleDateFormat.parse(strDate);
            allData.add(new DateCountryStats(state, country, latitude, longitude, date, Integer.parseInt(record.get(strDate))));
        }
    }


    public void updateCountrywideData(){
        for (DateCountryStats dateCountryStats : allData) {
            TreeMap<Date, DateCountryStats> dateLocationDateStatHashMap = countryWiseDataMap.get(dateCountryStats.getCountry());
            if(dateLocationDateStatHashMap == null){
                dateLocationDateStatHashMap = new TreeMap<>();
                dateLocationDateStatHashMap.put(dateCountryStats.getDate(), dateCountryStats);
            }
            else{
                DateCountryStats existingRecord = dateLocationDateStatHashMap.get(dateCountryStats.getDate());
                if(existingRecord==null){
                    dateLocationDateStatHashMap.put(dateCountryStats.getDate(), dateCountryStats);
                }
                else{
                    DateCountryStats newLocationDataSet = new DateCountryStats(dateCountryStats.getCountry(), dateCountryStats.getState(), dateCountryStats.getLatitude(), dateCountryStats.getLongitude(), dateCountryStats.getDate(), existingRecord.getNumberOfPeople() + dateCountryStats.getNumberOfPeople());
                    dateLocationDateStatHashMap.put(dateCountryStats.getDate(),newLocationDataSet);
                }
            }
            countryWiseDataMap.put(dateCountryStats.getCountry(),dateLocationDateStatHashMap);
        }
    }

    public Collection<DateCountryStats> getAllByCountryName(String country){
        return countryWiseDataMap.get(country).values();
    }



    public HashMap<String, CountryStats> getCountryStatsMap() {
        return countryStatsMap;
    }


    public ArrayList<CountryStats> getCountriesStats() {
        return countryStatsMap.values().stream().
                collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getCountries() {
        return countryStatsMap.keySet().stream().
                collect(Collectors.toCollection(ArrayList::new));
    }

}
