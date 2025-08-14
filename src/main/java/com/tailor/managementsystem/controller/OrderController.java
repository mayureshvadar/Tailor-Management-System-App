package com.tailor.managementsystem.controller;

import com.tailor.managementsystem.model.Order;
import com.tailor.managementsystem.repository.OrderRepository;
import com.tailor.managementsystem.service.OrderService;
import com.tailor.managementsystem.utils.InvoiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
	 
    @Autowired
    private OrderService orderService;
    

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/byCustomer/{customerId}")
    public Optional<Order> getOrdersByCustomer(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }
    
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
    	
    //PDF bill Endpoint.
    @Autowired
    private OrderRepository orderRepository;
    
    @GetMapping("/{id}/bill")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }

        byte[] pdfBytes = InvoiceGenerator.generateBill(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
            .filename("Invoice_Yash Mens Wear_" + id + ".pdf")
            .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportOrdersAsCSV() {
        String csv = orderService.exportOrdersToCSV();
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename("orders_export.csv")
            .build());

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }



}