package com.elanvelazquez.cognizant.challenge.loan.repositories;

import com.elanvelazquez.cognizant.challenge.loan.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Integer> {

    List<Loan> findLoanByEmployee(int id);
    List<Loan> findLoansByEmployeeAndPaid(int employee, boolean paid);

}
