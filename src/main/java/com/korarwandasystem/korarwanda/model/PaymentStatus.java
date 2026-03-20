package com.korarwandasystem.korarwanda.model;

public enum PaymentStatus {
    INITIATED,   // Payment started but not yet completed
    SUCCESS,     // Payment completed successfully
    FAILED,      // Payment failed
    REFUNDED     // Payment has been refunded
}