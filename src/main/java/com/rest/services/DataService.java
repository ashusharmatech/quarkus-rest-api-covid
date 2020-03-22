package com.rest.services;


import com.rest.enums.DataTypeEnum;
import com.rest.models.CountryDateStats;
import com.rest.models.CountryStats;
import com.rest.models.DateStats;
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


    private static String CONFIRMED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static String DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
    private static String RECOVERED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";


    private final Logger log = LoggerFactory.getLogger(DataService.class);

    private HashMap<String,TreeMap<Date, CountryDateStats>> countryWiseDataMap = new HashMap<>();
    private HashMap<String, CountryStats> countryStatsMap = new HashMap<>();
    private List<DateStats> allConfirmedData = new ArrayList<>();
    private List<DateStats> allRecoveredData = new ArrayList<>();
    private List<DateStats> allDeathData = new ArrayList<>();




    List<String> headerNames = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");

    @PostConstruct
    @Scheduled(cron = "0 0 4 ? * * *")
    public void initialDataLoader() throws IOException, InterruptedException, ParseException {

        loadData();
        consolidateAllDataIntoMaps();
    }

    private void loadData() throws IOException, InterruptedException, ParseException {
        HashMap<String, CountryStats> tempStats = new HashMap<>();
        loadData(CONFIRMED_DATA_URL,tempStats,DataTypeEnum.CONFIRMED);
        loadData(RECOVERED_DATA_URL,tempStats,DataTypeEnum.RECOVERED);
        loadData(DEATH_DATA_URL,tempStats,DataTypeEnum.DEATH);
        this.countryStatsMap = tempStats;
        log.info(" country stats map has "+countryStatsMap.size());
    }



    private void loadData(String url, HashMap<String, CountryStats> tempStats, DataTypeEnum dataTypeEnum) throws IOException, InterruptedException, ParseException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
                if(dataTypeEnum==DataTypeEnum.CONFIRMED){
                    countryStats.setLatestConfirmedCases(latestCases);
                    countryStats.setDiffFromPrevDayConfirmedCases(Math.max(0,latestCases - prevDayCases));
                }
                else if(dataTypeEnum==DataTypeEnum.RECOVERED){
                    countryStats.setLatestRecoveredCases(latestCases);
                    countryStats.setDiffFromPrevDayRecoveredCases(Math.max(0,latestCases - prevDayCases));
                }
                else{
                    countryStats.setLatestDeathCases(latestCases);
                    countryStats.setDiffFromPrevDayDeathCases(Math.max(0,latestCases - prevDayCases));
                }
                tempStats.put(country, countryStats);
            } else {

                if(dataTypeEnum==DataTypeEnum.CONFIRMED){
                    existingCountryStats.setLatestConfirmedCases(existingCountryStats.getLatestConfirmedCases() + latestCases);
                    existingCountryStats.setDiffFromPrevDayConfirmedCases(existingCountryStats.getDiffFromPrevDayConfirmedCases() + Math.max(0,latestCases - prevDayCases));
                }
                else if(dataTypeEnum==DataTypeEnum.RECOVERED){
                    existingCountryStats.setLatestRecoveredCases(existingCountryStats.getLatestRecoveredCases() + latestCases);
                    existingCountryStats.setDiffFromPrevDayRecoveredCases(existingCountryStats.getDiffFromPrevDayRecoveredCases() + Math.max(0,latestCases - prevDayCases));
                }
                else{
                    existingCountryStats.setLatestDeathCases(existingCountryStats.getLatestDeathCases() + latestCases);
                    existingCountryStats.setDiffFromPrevDayDeathCases(existingCountryStats.getDiffFromPrevDayDeathCases() + Math.max(0,latestCases - prevDayCases));
                }
            }
            if(dataTypeEnum==DataTypeEnum.CONFIRMED){
                createDateStats(record,state,country,latitude,longitude,allConfirmedData);
            }
            else if(dataTypeEnum==DataTypeEnum.RECOVERED){
                createDateStats(record,state,country,latitude,longitude,allRecoveredData);
            }
            else{
                createDateStats(record,state,country,latitude,longitude,allDeathData);
            }
        }
    }





    public void createDateStats(CSVRecord record, String state, String country, String latitude, String longitude, List<DateStats> allData) throws ParseException {
        for (int i = 4; i < record.size() - 1; i++) {
            String strDate = headerNames.get(i);
            Date date = simpleDateFormat.parse(strDate);
            allData.add(new DateStats(state, country, latitude, longitude, date, Integer.parseInt(record.get(strDate))));
        }
    }


    public void consolidateAllDataIntoMaps(){
         HashMap<String,TreeMap<Date, CountryDateStats>> tempCountryWiseDataMap = new HashMap<>();

        //Confirmed Cases Load

        for (DateStats dateStats : allConfirmedData) {
            TreeMap<Date, CountryDateStats> dateLocationDateStatHashMap = tempCountryWiseDataMap.get(dateStats.getCountry());
            if(dateLocationDateStatHashMap == null){
                CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                countryDateStats.setConfirmed(dateStats.getNumberOfPeople());
                dateLocationDateStatHashMap = new TreeMap<>();
                dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
            }
            else{
                CountryDateStats existingRecord = dateLocationDateStatHashMap.get(dateStats.getDate());
                if(existingRecord==null){
                    CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                    countryDateStats.setConfirmed(dateStats.getNumberOfPeople());
                    dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
                }
                else{
                    existingRecord.setConfirmed(existingRecord.getConfirmed()+dateStats.getNumberOfPeople());
                }
            }
            tempCountryWiseDataMap.put(dateStats.getCountry(),dateLocationDateStatHashMap);
        }




        //Confirmed Cases Load

        for (DateStats dateStats : allRecoveredData) {
            TreeMap<Date, CountryDateStats> dateLocationDateStatHashMap = tempCountryWiseDataMap.get(dateStats.getCountry());
            if(dateLocationDateStatHashMap == null){
                CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                countryDateStats.setRecovered(dateStats.getNumberOfPeople());
                dateLocationDateStatHashMap = new TreeMap<>();
                dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
            }
            else{
                CountryDateStats existingRecord = dateLocationDateStatHashMap.get(dateStats.getDate());
                if(existingRecord==null){
                    CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                    countryDateStats.setRecovered(dateStats.getNumberOfPeople());
                    dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
                }
                else{
                    existingRecord.setRecovered(existingRecord.getRecovered()+dateStats.getNumberOfPeople());
                }
            }
            tempCountryWiseDataMap.put(dateStats.getCountry(),dateLocationDateStatHashMap);
        }


        //Confirmed Cases Load

        for (DateStats dateStats : allDeathData) {
            TreeMap<Date, CountryDateStats> dateLocationDateStatHashMap = tempCountryWiseDataMap.get(dateStats.getCountry());
            if(dateLocationDateStatHashMap == null){
                CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                countryDateStats.setDeath(dateStats.getNumberOfPeople());
                dateLocationDateStatHashMap = new TreeMap<>();
                dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
            }
            else{
                CountryDateStats existingRecord = dateLocationDateStatHashMap.get(dateStats.getDate());
                if(existingRecord==null){
                    CountryDateStats countryDateStats = createCountryDateStatsFromDateStats(dateStats);
                    countryDateStats.setDeath(dateStats.getNumberOfPeople());
                    dateLocationDateStatHashMap.put(dateStats.getDate(), countryDateStats);
                }
                else{
                    existingRecord.setDeath(existingRecord.getDeath()+dateStats.getNumberOfPeople());
                }
            }
            tempCountryWiseDataMap.put(dateStats.getCountry(),dateLocationDateStatHashMap);
        }

        this.countryWiseDataMap = tempCountryWiseDataMap;

    }

    private CountryDateStats createCountryDateStatsFromDateStats(DateStats dateStats) {
        return new CountryDateStats(dateStats.getState(),dateStats.getCountry(),dateStats.getLatitude(),dateStats.getLongitude(),dateStats.getDate());
    }

    public Collection<CountryDateStats> getAllByCountryName(String country){
        return countryWiseDataMap.get(country).values();
    }

    public HashMap<String, CountryStats> getCountryStatsMap() {
        return countryStatsMap;
    }


    public ArrayList<CountryStats> getCountriesStats() {
        ArrayList<CountryStats> countryStatsList = countryStatsMap.values().stream().
                collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(countryStatsList);
        return countryStatsList;
    }

    public ArrayList<String> getCountries() {
        return countryStatsMap.keySet().stream().
                collect(Collectors.toCollection(ArrayList::new));
    }

}
