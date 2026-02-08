package com.jobly.config;

import com.jobly.gen.api.ApiClient;
import com.jobly.gen.api.parser.CvParserApi;
import com.jobly.gen.api.parser.data.CvParseResponse;
import com.jobly.gen.api.parser.data.Education;
import com.jobly.gen.api.parser.data.PersonalDetails;
import com.jobly.gen.api.parser.data.WorkExperience;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.AbstractResource;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class CvParserApiConfig {

    @Profile({"dev", "test"})
    @Bean
    public CvParserApi getMockCvParserApi() {
        return new CvParserApi() {
            @Override
            public Mono<CvParseResponse> parseCvPost(AbstractResource uploadedCvFile) {
                CvParseResponse mockResponse = new CvParseResponse();
                mockResponse.setPersonalDetails(generatePersonalDetails());
                mockResponse.setEducation(generateEducationHistory());
                mockResponse.setWorkExperience(generateWorkExperience());
                mockResponse.setSkills(generateRawSkills());
                return Mono.just(mockResponse);
            }
        };
    }

    @Profile("!dev & !test")
    @Bean
    public CvParserApi getCvParserApi(@Value("${cv-parser.base-url}") String cvParserBaseUrl) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(cvParserBaseUrl);
        return new CvParserApi(apiClient);
    }

    private PersonalDetails generatePersonalDetails() {
        return new PersonalDetails()
                .name("Jane Doe")
                .email("jane.doe@example.com");
    }

    private List<Education> generateEducationHistory() {
        Education education1 = new Education()
                .institution("Massachusetts Institute of Technology")
                .startDate(LocalDate.of(2016, 9, 1))
                .endDate(LocalDate.of(2020, 6, 30));

        Education education2 = new Education()
                .institution("Stanford University");

        return List.of(education1, education2);
    }

    private List<WorkExperience> generateWorkExperience() {
        WorkExperience experience1 = new WorkExperience()
                .company("Google")
                .designation("Software Engineer")
                .startDate(LocalDate.of(2020, 7, 1))
                .endDate(LocalDate.of(2023, 3, 31));

        WorkExperience experience2 = new WorkExperience()
                .company("Startup XYZ")
                .startDate(LocalDate.of(2023, 4, 1));

        WorkExperience experience3 = new WorkExperience()
                .company("Freelance");

        return List.of(experience1, experience2, experience3);
    }

    private List<String> generateRawSkills() {
        return List.of(
                "i am wokring with spring boot and rest apis",
                "Experience in Java, hibernate , JPA",
                "worked on microservices archtecture",
                "basic knowldge of docker & kubernetes",
                "good undestanding of sql databases",
                "have used aws s3 and ec2 a bit",
                "team player, good comunication skills"
        );
    }
}
