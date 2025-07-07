# Paymentic Wallet Transactions Data Product

## Overview

The **Paymentic Wallet Transactions** data product provides real-time analytical access to payment and refund transactions processed through the Paymentic wallet system. This data product enables comprehensive financial reporting, transaction monitoring, fraud detection, and business intelligence capabilities.

## Data Product Information

- **Owner**: Paymentic Wallet Team
- **Domain**: Payments & Financial Services
- **Version**: 1.0.0
- **Contact**: wallet-analytics@paymentic.com
- **Data Product Type**: Real-time Analytical Data Product

## Purpose & Use Cases

### Primary Use Cases
1. **Real-time Transaction Monitoring**: Monitor payment flows and detect anomalies in real-time
2. **Financial Reporting & Analytics**: Generate revenue reports, analyze payment patterns, and track financial metrics
3. **Fraud Detection**: Identify suspicious transaction patterns and potential fraud
4. **Business Intelligence**: Analyze buyer behavior, merchant performance, and transaction trends
5. **Compliance & Audit**: Maintain audit trails and support regulatory compliance requirements

### Key Business Questions Answered
- What is the transaction volume and revenue by seller, time period, and currency?
- What are the payment success rates and patterns?
- How do buyers behave across different payment methods?
- What are the refund patterns and rates?
- Which merchants are performing best/worst?

## Data Source & Architecture

- **Source System**: Paymentic Wallet Application
- **Event Stream**: Kafka topic `merchant-account`
- **Event Format**: CloudEvents JSON
- **Event Type**: `funny-bunny.xyz.merchant-account.v1.transaction.registered`
- **Data Freshness**: < 30 seconds
- **Availability SLA**: 99.9%

## Schema Definition

### Dimension Fields (Filtering & Grouping)

| Field Name | Data Type | Description | Example | Nullable |
|------------|-----------|-------------|---------|----------|
| `transactionId` | STRING | Unique transaction identifier | "123e4567-e89b-12d3-a456-426614174000" | No |
| `operationType` | STRING | Type of operation (PAYMENT, REFUND) | "PAYMENT" | No |
| `sellerId` | STRING | Unique seller/merchant identifier | "seller123" | No |
| `buyerDocument` | STRING | Buyer's document/ID number | "12345678901" | No |
| `buyerName` | STRING | Buyer's full name | "John Doe" | No |
| `currency` | STRING | Transaction currency code | "USD" | No |
| `paymentOrderId` | STRING | Associated payment order identifier | "order123" | No |
| `checkoutId` | STRING | Checkout session identifier (null for refunds) | "checkout123" | Yes |
| `eventSource` | STRING | CloudEvent source identifier | "/transactions/123e4567-e89b-12d3-a456-426614174000" | No |
| `eventSubject` | STRING | CloudEvent subject | "payment-created" | No |
| `eventType` | STRING | CloudEvent type | "funny-bunny.xyz.merchant-account.v1.transaction.registered" | No |
| `cloudEventId` | STRING | CloudEvent unique identifier | "ce-123e4567-e89b-12d3-a456-426614174000" | No |

### Metric Fields (Aggregation)

| Field Name | Data Type | Description | Example | Nullable |
|------------|-----------|-------------|---------|----------|
| `amount` | BIG_DECIMAL | Transaction amount | 100.50 | No |

### Time Fields

| Field Name | Data Type | Description | Example | Nullable |
|------------|-----------|-------------|---------|----------|
| `transactionTimestamp` | TIMESTAMP | When the transaction occurred | 2024-01-15T10:30:00.000Z | No |
| `eventTimestamp` | TIMESTAMP | When the event was processed | 2024-01-15T10:30:01.123Z | No |

## Data Quality Guarantees

### SLA/SLO Commitments
- **Freshness**: Data available within 30 seconds of transaction occurrence
- **Availability**: 99.9% uptime guarantee
- **Completeness**: 100% of transactions captured (no data loss)
- **Accuracy**: All financial amounts accurate to 2 decimal places
- **Consistency**: All related fields (buyer, seller, payment order) are consistent

### Data Validation Rules
- All monetary amounts are positive
- Currency codes follow ISO 4217 standard
- Transaction IDs are unique and valid UUIDs
- Buyer documents are non-empty
- Seller IDs are validated against merchant registry

## Example Queries

### 1. Daily Revenue by Seller
```sql
SELECT 
    sellerId,
    SUM(amount) as total_revenue,
    COUNT(*) as transaction_count,
    AVG(amount) as avg_transaction_amount
FROM `paymentic-wallet-transactions`
WHERE operationType = 'PAYMENT'
    AND transactionTimestamp >= '2024-01-01'
    AND transactionTimestamp < '2024-01-02'
GROUP BY sellerId
ORDER BY total_revenue DESC;
```

### 2. Real-time Transaction Monitoring (Last 5 Minutes)
```sql
SELECT 
    transactionId,
    operationType,
    sellerId,
    buyerName,
    amount,
    currency,
    transactionTimestamp
FROM `paymentic-wallet-transactions`
WHERE transactionTimestamp >= DATEADD(MINUTE, -5, NOW())
ORDER BY transactionTimestamp DESC
LIMIT 100;
```

### 3. Currency-wise Transaction Analysis
```sql
SELECT 
    currency,
    operationType,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount
FROM `paymentic-wallet-transactions`
WHERE transactionTimestamp >= DATEADD(DAY, -30, NOW())
GROUP BY currency, operationType
ORDER BY currency, operationType;
```

### 4. Refund Rate Analysis
```sql
SELECT 
    sellerId,
    COUNT(CASE WHEN operationType = 'PAYMENT' THEN 1 END) as payments,
    COUNT(CASE WHEN operationType = 'REFUND' THEN 1 END) as refunds,
    ROUND(
        COUNT(CASE WHEN operationType = 'REFUND' THEN 1 END) * 100.0 / 
        COUNT(CASE WHEN operationType = 'PAYMENT' THEN 1 END), 2
    ) as refund_rate_percent
FROM `paymentic-wallet-transactions`
WHERE transactionTimestamp >= DATEADD(DAY, -30, NOW())
GROUP BY sellerId
HAVING COUNT(CASE WHEN operationType = 'PAYMENT' THEN 1 END) > 0
ORDER BY refund_rate_percent DESC;
```

### 5. High-Value Transaction Alert
```sql
SELECT 
    transactionId,
    sellerId,
    buyerName,
    buyerDocument,
    amount,
    currency,
    transactionTimestamp
FROM `paymentic-wallet-transactions`
WHERE amount > 10000
    AND transactionTimestamp >= DATEADD(HOUR, -1, NOW())
ORDER BY amount DESC;
```

## Data Governance & Compliance

### Data Privacy
- Buyer information is included for analytical purposes
- All data handling complies with GDPR and PCI DSS requirements
- Data retention period: 365 days
- Access control implemented via role-based permissions

### Data Lineage
- **Source**: Paymentic Wallet Application (`TransactionRegisteredEvent`)
- **Processing**: Real-time ingestion via Kafka
- **Storage**: Apache Pinot real-time table
- **Transformation**: Minimal - direct field mapping from CloudEvents

## Getting Started

### Prerequisites
- Access to Pinot cluster
- Kafka connection configured
- Proper IAM permissions

### Accessing the Data
1. Connect to Pinot cluster endpoint
2. Use table name: `paymentic-wallet-transactions`
3. Query using Pinot SQL or your preferred BI tool

### Support & Troubleshooting
- **Technical Support**: wallet-analytics@paymentic.com
- **Documentation**: This README
- **Issue Tracking**: Internal Paymentic JIRA
- **On-call Support**: 24/7 via PagerDuty

## Change Log

### Version 1.0.0 (Initial Release)
- Real-time transaction ingestion
- Complete schema definition
- Primary analytical use cases supported
- SLA commitments established