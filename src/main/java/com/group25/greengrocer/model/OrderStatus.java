package com.group25.greengrocer.model;

/**
 * Enumeration representing the possible states of an order in the greengrocer application.
 * 
 * Order lifecycle typically flows as follows:
 * CART → PLACED → ASSIGNED → DELIVERED (or CANCELLED at any point)
 * 
 */
public enum OrderStatus {
    
    CART,
    
    PLACED,
    
    ASSIGNED,
    
    DELIVERED,
    
    CANCELLED
}
