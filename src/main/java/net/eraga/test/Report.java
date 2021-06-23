package net.eraga.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.eraga.test.models.Person;
import net.eraga.test.repositories.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Report {

    List<Region> regions;

    public Report(PersonRepository personRepository) {
        regions = new ArrayList<>();
        personRepository.findAll().forEach(person -> {
            int regionCode = person.getRegion();
            String cityName = person.getCity();

            Region region = getRegionByCode(regionCode);
            if (region == null) {
                region = new Region(regionCode);
                regions.add(region);
            }

            if (cityName == null) {
                region.getPersons().add(person);
            } else {
                City city = region.getCityByName(person.getCity());
                if (city == null) {
                    city = new City(cityName);
                    region.getCities().add(city);
                }
                city.getPersons().add(person);
            }
        });
    }

    public Region getRegionByCode(int regionCode) {
        return regions.stream()
                .filter(region -> region.getCode() == regionCode)
                .findFirst()
                .orElse(null);
    }

    public List<Region> getRegions() {
        return regions;
    }


    @JsonPropertyOrder({"code", "cities", "persons"})
    class Region {

        @JsonProperty("regionCode")
        int code;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<City> cities;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<Person> persons;

        public Region(int code) {
            this.code = code;
            cities = new ArrayList<>();
            persons = new ArrayList<>();
        }

        public City getCityByName(String cityName) {
            return cities.stream()
                    .filter(city -> Objects.equals(city.getName(), cityName))
                    .findFirst()
                    .orElse(null);
        }

        public int getCode() {
            return code;
        }

        public List<City> getCities() {
            return cities;
        }

        public List<Person> getPersons() {
            return persons;
        }
    }


    class City {

        String name;
        List<Person> persons;

        public City(String name) {
            this.name = name;
            persons = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public List<Person> getPersons() {
            return persons;
        }
    }

}
