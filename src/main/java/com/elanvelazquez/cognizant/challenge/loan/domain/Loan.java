package com.elanvelazquez.cognizant.challenge.loan.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Loan {
    @Id
    @GeneratedValue
    private int id;
    private double amount;
    private boolean paid = Boolean.FALSE;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name="employee")
    //@JsonIgnore
    private int employee;

    public Loan() {
    }

    public Loan(double amount, int employee) {
        this.amount = amount;
        this.employee = employee;
    }

    public Loan(int id, double amount, boolean paid, int employee) {
        this.id = id;
        this.amount = amount;
        this.paid = paid;
        this.employee = employee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public int getEmployee() {
        return employee;
    }

    public void setEmployee(int employee) {
        this.employee = employee;
    }
}
