package com.elanvelazquez.cognizant.challenge.loan.controllers;

import com.elanvelazquez.cognizant.challenge.loan.dtos.EmployeeDTO;
import com.elanvelazquez.cognizant.challenge.loan.dtos.LoanDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 *  Errors to catch for challenge
 *  the only condition is that the employee can have maximum 2 loans at the same time.
 *  They way how the employee is going to pay the loan is in a single payment.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoanControllerTest {
    private  static  final String HOST_FOR_EMPLOYEES ="http://localhost:8080/employees";
    private  static  final String HOST_FOR_LOANS ="http://localhost:9090/loans";
    @Autowired
    private LoanController loanController = mock(LoanController.class);
    @Autowired
    private RestTemplate restTemplate;


    @Test
    void createLoan() {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Jorge");
        employee.setLastName("Hernandez");
        ResponseEntity responsePostEmployee = restTemplate.exchange(HOST_FOR_EMPLOYEES,HttpMethod.POST,new HttpEntity<>(employee),ResponseEntity.class);

        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(2000);
        ResponseEntity response =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.CREATED);

    }

    @Test
    void getActiveLoansByEmployee() {
        //CREATE EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Elan");
        employee.setLastName("Luna");
        ResponseEntity responsePostEmployee = restTemplate.exchange(HOST_FOR_EMPLOYEES,HttpMethod.POST,new HttpEntity<>(employee),ResponseEntity.class);

        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();
        //CREATE LOAN
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(2000);
        ResponseEntity response =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);

        ResponseEntity<LoanDTO[]> forEntity = restTemplate.getForEntity(String.format("%s/employee/%s",HOST_FOR_LOANS,employee.getId()),LoanDTO[].class);
        List<LoanDTO> loanDTOList = Arrays.asList(forEntity.getBody());

        Assert.assertTrue(loanDTOList.size() > 0);
    }

    @Test
    void getAllLoans() {
        //GET EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Elan");
        employee.setLastName("Luna");
        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();

        ResponseEntity<LoanDTO[]> forEntity = restTemplate.getForEntity(String.format("%s",HOST_FOR_LOANS,employee.getId()),LoanDTO[].class);
        List<LoanDTO> loanDTOList = Arrays.asList(forEntity.getBody());

        Assert.assertTrue(loanDTOList.size() > 0);
    }

    @Test
    void payLoan() {
        //GET EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Elan");
        employee.setLastName("Luna");
        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();
        //CREATE LOAN
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(3000);
        ResponseEntity responseCreatedLoan =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);
        //GET LOANS BY EMPLOYEEID
        ResponseEntity<LoanDTO[]> forEntity = restTemplate.getForEntity(String.format("%s/employee/%s",HOST_FOR_LOANS,employee.getId()),LoanDTO[].class);
        List<LoanDTO> loanDTOList = Arrays.asList(forEntity.getBody());
        loanDTO = loanDTOList.get(0);

        //PAY LOAN
        ResponseEntity responsePayLoan = restTemplate.exchange(String.format("%s/pay/employee/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);

        Assert.assertEquals(responsePayLoan.getStatusCode(),HttpStatus.CREATED);

    }

    @Test
    void getPaidLoansByEmployeeId() {
        //GET EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Elan");
        employee.setLastName("Luna");
        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();

        ResponseEntity<LoanDTO[]> forEntity = restTemplate.getForEntity(String.format("%s/employee/%s/paid",HOST_FOR_LOANS,employee.getId()),LoanDTO[].class);
        List<LoanDTO> loanDTOList = Arrays.asList(forEntity.getBody());

        Assert.assertTrue(loanDTOList.size() > 0);
    }

    /**
     * the only condition is that the employee can have maximum 2 loans at the same time.
     */
    @Test
    void createLoan_ErrorMoreThanTwoActive()
    {
        //CREATE EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Alberto");
        employee.setLastName("Valdez");
        ResponseEntity responsePostEmployee = restTemplate.exchange(HOST_FOR_EMPLOYEES,HttpMethod.POST,new HttpEntity<>(employee),ResponseEntity.class);
        //GET EMPLOYEE
        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();

        //ADD LOANS
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(2000);
        ResponseEntity responseLoan1 =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);

        loanDTO.setAmount(3000);
        ResponseEntity responseLoan2 =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);

        try {
            loanDTO.setAmount(4000);
            ResponseEntity responseLoan3 =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                    HttpMethod.POST,
                    new HttpEntity<>(loanDTO),
                    ResponseEntity.class);
        }catch (HttpClientErrorException exception){
            Assert.assertEquals(exception.getStatusCode(),HttpStatus.CONFLICT);
        }


    }

    /**
     * They way how the employee is going to pay the loan is in a single payment.
     */
    @Test
    void payLoan_ErrorSinglePayment(){
        //CREATE EMPLOYEE
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Fer");
        employee.setLastName("Garcia");
        ResponseEntity responsePostEmployee = restTemplate.exchange(HOST_FOR_EMPLOYEES,HttpMethod.POST,new HttpEntity<>(employee),ResponseEntity.class);

        ResponseEntity<EmployeeDTO[]> employeeDTOS = restTemplate.getForEntity(String.format("%s/%s",HOST_FOR_EMPLOYEES,employee.getLastName()),
                EmployeeDTO[].class);
        employee = Arrays.stream(employeeDTOS.getBody()).findFirst().get();

        //CREATE LOAN
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(3000);
        ResponseEntity responseCreatedLoan =  restTemplate.exchange(String.format("%s/%s",HOST_FOR_LOANS,employee.getId()),
                HttpMethod.POST,
                new HttpEntity<>(loanDTO),
                ResponseEntity.class);
        //GET LOANS BY EMPLOYEEID
        ResponseEntity<LoanDTO[]> forEntity = restTemplate.getForEntity(String.format("%s/employee/%s",HOST_FOR_LOANS,employee.getId()),LoanDTO[].class);
        loanDTO = Arrays.asList(forEntity.getBody()).stream().findFirst().get();

        //PAY LOAN
        try {
            loanDTO.setAmount(100);
            ResponseEntity responsePayLoan = restTemplate.exchange(String.format("%s/pay/employee/%s",HOST_FOR_LOANS,employee.getId()),
                    HttpMethod.POST,
                    new HttpEntity<>(loanDTO),
                    ResponseEntity.class);
        }catch (HttpClientErrorException exception){
            Assert.assertEquals(exception.getStatusCode(),HttpStatus.CONFLICT);
        }

    }

}