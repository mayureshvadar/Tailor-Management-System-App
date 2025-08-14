package com.tailor.managementsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String garmentType;

    private double price;

    private LocalDate deliveryDate;

    private String notes;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    public Long getId() {
    	return id;
    }

	public String getGarmentType() {
		return garmentType;
	}

	public double getPrice() {
		return price;
	}

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public String getNotes() {
		return notes;
	}
	 public Customer getCustomer() { 
		return customer; }
	 
	// Setters
	    public void setId(Long id) { 
	    	this.id = id; 
	    	}
	    public void setGarmentType(String garmentType) { 
	    	this.garmentType = garmentType; 
	    	}
	    public void setPrice(double price) { 
	    	this.price = price; 
	    	}
	    public void setDeliveryDate(LocalDate deliveryDate) { 
	    	this.deliveryDate = deliveryDate; 
	    	}
	    public void setNotes(String notes) { 
	    	this.notes = notes; 
	    	}
	    public void setCustomer(Customer customer) { 
	    	this.customer = customer; 
	    	}


}