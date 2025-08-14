package com.tailor.managementsystem.service;

import com.tailor.managementsystem.model.Customer;
import com.tailor.managementsystem.model.Order;
import com.tailor.managementsystem.repository.CustomerRepository;
import com.tailor.managementsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
	@Autowired
	private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrdersByCustomerId(Long Id) {
        return orderRepository.findById(Id);
    }

    public Order saveOrder(Order incomingOrder) {
        Customer incomingCustomer = incomingOrder.getCustomer();
        Customer linkedCustomer;

        if (incomingCustomer != null && incomingCustomer.getId() != null) {
            // Link to existing customer and update fields
            linkedCustomer = customerRepository.findById(incomingCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            linkedCustomer.setName(incomingCustomer.getName());
            linkedCustomer.setMobile(incomingCustomer.getMobile());
            linkedCustomer.setGarmentType(incomingCustomer.getGarmentType());
            linkedCustomer.setMeasurements(incomingCustomer.getMeasurements());
        } else {
            // Create new customer
            linkedCustomer = customerRepository.save(incomingCustomer);
        }

        incomingOrder.setCustomer(linkedCustomer);
        return orderRepository.save(incomingOrder);
    }
    
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update order fields
        existingOrder.setGarmentType(updatedOrder.getGarmentType());
        existingOrder.setPrice(updatedOrder.getPrice());
        existingOrder.setDeliveryDate(updatedOrder.getDeliveryDate());
        existingOrder.setNotes(updatedOrder.getNotes());

        // Handle customer update
        Customer incomingCustomer = updatedOrder.getCustomer();
        if (incomingCustomer != null && incomingCustomer.getId() != null) {
            Customer existingCustomer = customerRepository.findById(incomingCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            existingCustomer.setName(incomingCustomer.getName());
            existingCustomer.setMobile(incomingCustomer.getMobile());
            existingCustomer.setGarmentType(incomingCustomer.getGarmentType());
            existingCustomer.setMeasurements(incomingCustomer.getMeasurements());

            existingOrder.setCustomer(existingCustomer);
        }

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    public String exportOrdersToCSV() {
        List<Order> orders = orderRepository.findAll();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Order ID,Customer Name,Mobile,Garment Type,Price,Delivery Date,Notes,Measurements\n");

        for (Order order : orders) {
            Customer c = order.getCustomer();
            
            csvBuilder.append(order.getId()).append(",");
            
            if(c != null) {
            csvBuilder.append(c.getName()).append(",");
            csvBuilder.append(c.getMobile()).append(",");
            csvBuilder.append(order.getGarmentType()).append(",");
            csvBuilder.append(order.getPrice()).append(",");
            csvBuilder.append(order.getDeliveryDate()).append(",");
            csvBuilder.append(order.getNotes()).append(",");
            csvBuilder.append(c.getMeasurements()).append("\n");
        }else {
        	csvBuilder.append("N/A,N/A,");
            csvBuilder.append(order.getGarmentType()).append(",");
            csvBuilder.append(order.getPrice()).append(",");
            csvBuilder.append(order.getDeliveryDate()).append(",");
            csvBuilder.append(order.getNotes()).append(",");
            csvBuilder.append("N/A\n");
        	}
        }

        return csvBuilder.toString();
    }
}