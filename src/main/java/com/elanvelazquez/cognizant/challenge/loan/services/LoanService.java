package com.elanvelazquez.cognizant.challenge.loan.services;

import com.elanvelazquez.cognizant.challenge.loan.domain.Loan;
import com.elanvelazquez.cognizant.challenge.loan.dtos.EmployeeDTO;
import com.elanvelazquez.cognizant.challenge.loan.exceptions.GenericRequestException;
import com.elanvelazquez.cognizant.challenge.loan.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {


    private LoanRepository loanRepository;
    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan createLoan(Loan loan, Optional<EmployeeDTO> employee){
//        Optional<Employee> findEmployee = employeeRepository.findById(employeeId);
        if( employee.isEmpty())
        {
            throw new GenericRequestException("Employee not found",HttpStatus.NOT_FOUND);
        }
        if(loanRepository.findLoansByEmployeeAndPaid(employee.get().getId(),false).stream().count() ==2)
        {
            throw new GenericRequestException("Error trying to create loan, employee can have maximum 2 loans at the same time", HttpStatus.CONFLICT);
        }

        loan.setEmployee(employee.get().getId());
        Loan createdLoan = loanRepository.save(loan);

        return createdLoan;
    }


    public List<Loan> getActiveLoansByEmployee(int id) {
        return loanRepository.findLoansByEmployeeAndPaid(id,false);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getPaidLoansByEmployeeId(Optional<EmployeeDTO> optionalEmployeeDTO, boolean paid){
        if(optionalEmployeeDTO.isEmpty()) throw new GenericRequestException("Employee not found, please try again.",HttpStatus.NOT_FOUND);

        return loanRepository.findLoansByEmployeeAndPaid(optionalEmployeeDTO.get().getId(),paid);
    }

    public Loan payLoan(Optional<EmployeeDTO> employeeId, Loan loan){

        if(employeeId.isEmpty()) throw new GenericRequestException("Employee not found, please try again.",HttpStatus.NOT_FOUND);

        Optional<Loan> findLoan = loanRepository.findById(loan.getId());

        if(findLoan.isEmpty()) throw new GenericRequestException("Loan not found, please try again.",HttpStatus.NOT_FOUND);

        if(loan.getAmount() != findLoan.get().getAmount()) throw new GenericRequestException("The loan must be paid in full.",HttpStatus.CONFLICT);

        findLoan.get().setPaid(true);
        loanRepository.save(findLoan.get());

        return findLoan.get();
    }

}
