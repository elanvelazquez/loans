package com.elanvelazquez.cognizant.challenge.loan.dtos;

import com.elanvelazquez.cognizant.challenge.loan.domain.Loan;

import java.util.List;

public class EmployeeDTO {
    private int id;
    private String firstName;
    private String lastName;

    public EmployeeDTO() {
    }

    public EmployeeDTO(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
