package com.team14.clientProject.addApplicant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.opencsv.bean.CsvBindByName;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantCSVForm {
    // CsvBindByName annotation used to map the field in the CSV file to the field in the form class
    @CsvBindByName (column = "firstName")
    private String firstName;
    @CsvBindByName (column = "lastName")
    private String lastName;
    @CsvBindByName (column = "location")
    private String location;
    @CsvBindByName (column = "email")
    private String email;
    @CsvBindByName (column = "phoneNumber")
    private String phoneNumber;
    @CsvBindByName (column = "eventAttended")
    private String eventAttended;
    @CsvBindByName (column = "skill")
    private String skill;
    @CsvBindByName (column = "subscribeToNewsletter")
    private String subscribeToNewsletter;
    @CsvBindByName (column = "subscribeToBulletins")
    private String subscribeToBulletins;
    @CsvBindByName (column = "subscribeToJobUpdates")
    private String subscribeToJobUpdates;
    @CsvBindByName (column = "currentPosition")
    private String currentPosition;
    @CsvBindByName (column = "status")
    private String status;
    @Null
    @CsvBindByName (column = "cvPath")
    private String cvPath;
    @Null
    @CsvBindByName (column = "coverLetterPath")
    private String coverLetterPath;

}
