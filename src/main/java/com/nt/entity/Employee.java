package com.nt.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="BATCH_EMPLOYEE")
@Data
public class Employee {
	@Id
	private Integer empno;
	private String ename;
	private String eadd;
	private Double salary;
	private Double grossSalary;
	private Double netSalary;

}
