package com.elanvelazquez.cognizant.challenge.loan.controllers;

import com.elanvelazquez.cognizant.challenge.loan.domain.Loan;
import com.elanvelazquez.cognizant.challenge.loan.dtos.EmployeeDTO;
import com.elanvelazquez.cognizant.challenge.loan.dtos.LoanDTO;
import com.elanvelazquez.cognizant.challenge.loan.services.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/loans")
public class LoanController {


    private LoanService loanService;
    @Autowired
    private RestTemplate restTemplate;
    private final ModelMapper MODEL_MAPPER= new ModelMapper();

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    //TODO: Rest template to connect here.
    @PostMapping("/{employeeId}")
    public ResponseEntity createLoan(@RequestBody Loan loan, @PathVariable("employeeId") int employeeId){
        EmployeeDTO employeeDTO = restTemplate.getForObject(String.format("http://localhost:8080/employees/find/%s",employeeId), EmployeeDTO.class);
        Optional<EmployeeDTO> optionalEmployee = Optional.ofNullable(employeeDTO);
        Loan createdLoan = loanService.createLoan(loan,optionalEmployee);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("loans/employee/{employeeId}")
                .buildAndExpand(employeeId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/employee/{id}")
    public List<LoanDTO> getActiveLoansByEmployee(@PathVariable int id){
        List<Loan> loans = loanService.getActiveLoansByEmployee(id);
        List<LoanDTO> loanDTOList = loans.stream()
                .map(loan ->MODEL_MAPPER.map(loan,LoanDTO.class))
                .collect(Collectors.toList());

        return loanDTOList;
    }

    @GetMapping()
    public List<LoanDTO> getAllLoans()
    {
        List<Loan> loans =loanService.getAllLoans();
        List<LoanDTO> loanDTOList =loans.stream()
                .map(loan -> MODEL_MAPPER.map(loan,LoanDTO.class))
                .collect(Collectors.toList());
        return loanDTOList;
    }

    @GetMapping("/employee/{employeeId}/paid")
    public List<LoanDTO> getPaidLoansByEmployeeId(@PathVariable int employeeId){
        EmployeeDTO employeeDTO = restTemplate.getForObject(String.format("http://localhost:8080/employees/find/%s",employeeId), EmployeeDTO.class);
        Optional<EmployeeDTO> optionalEmployee = Optional.ofNullable(employeeDTO);
        List<Loan> loans= loanService.getPaidLoansByEmployeeId(optionalEmployee,true);
        List<LoanDTO> loanDTOList = loans.stream()
                .map(loan -> MODEL_MAPPER.map(loan,LoanDTO.class))
                .collect(Collectors.toList());

        return loanDTOList;
    }

    @PostMapping("/pay/employee/{employeeId}")
    public ResponseEntity payLoan(@PathVariable int employeeId,@RequestBody Loan loan){
        EmployeeDTO employeeDTO = restTemplate.getForObject(String.format("http://localhost:8080/employees/find/%s",employeeId), EmployeeDTO.class);
        Optional<EmployeeDTO> optionalEmployee = Optional.ofNullable(employeeDTO);
        loanService.payLoan(optionalEmployee,loan);
        URI uri = ServletUriComponentsBuilder
                //.fromCurrentRequest() no
                .fromCurrentContextPath()
                .path("loans/employee/{id}/paid")
                .buildAndExpand(optionalEmployee.get().getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

}
