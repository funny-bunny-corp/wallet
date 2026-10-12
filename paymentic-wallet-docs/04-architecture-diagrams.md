# Paymentic Wallet - Architecture Diagrams

## Class Diagrams

### 1. Domain Entities and Value Objects

```mermaid
classDiagram
    class PaymentTransaction {
        -UUID id
        -String amount
        -String currency
        -CheckoutId checkout
        -BuyerInfo buyer
        -PaymentOrderId paymentOrder
        -SellerInfo sellerInfo
        +newPaymentTransactionRegistered() PaymentTransaction$
        +getId() UUID
        +getAmount() String
        +getCurrency() String
        +getCheckout() CheckoutId
        +getBuyer() BuyerInfo
        +getPaymentOrder() PaymentOrderId
        +getSellerInfo() SellerInfo
    }

    class RefundTransaction {
        -UUID id
        -String amount
        -String currency
        -BuyerInfo buyer
        -PaymentOrderId paymentOrder
        -SellerInfo sellerInfo
        +newRefundTransaction() RefundTransaction$
        +getId() UUID
        +getAmount() String
        +getCurrency() String
        +getBuyer() BuyerInfo
        +getPaymentOrder() PaymentOrderId
        +getSellerInfo() SellerInfo
    }

    class BuyerInfo {
        <<Value Object>>
        -String document
        -String name
        +getDocument() String
        +getName() String
    }

    class SellerInfo {
        <<Value Object>>
        -String id
        +getId() String
    }

    class CheckoutId {
        <<Value Object>>
        -String id
        +getId() String
    }

    class PaymentOrderId {
        <<Value Object>>
        -String id
        +getId() String
    }

    class TransactionId {
        <<Value Object>>
        -UUID id
        +id() UUID
    }

    PaymentTransaction *-- BuyerInfo
    PaymentTransaction *-- SellerInfo
    PaymentTransaction *-- CheckoutId
    PaymentTransaction *-- PaymentOrderId
    
    RefundTransaction *-- BuyerInfo
    RefundTransaction *-- SellerInfo
    RefundTransaction *-- PaymentOrderId
```

### 2. Service Layer Architecture

```mermaid
classDiagram
    class PaymentTransactionService {
        -PaymentTransactionRepository repository
        -TransactionRegisteredPublisher publisher
        +register(PaymentTransaction) void
    }

    class RefundTransactionService {
        -RefundTransactionRepository repository
        -TransactionRegisteredPublisher publisher
        +register(RefundTransaction) void
    }

    class PaymentTransactionRepository {
        <<interface>>
        +save(PaymentTransaction) PaymentTransaction
        +findById(UUID) Optional~PaymentTransaction~
        +findAll() List~PaymentTransaction~
        +delete(PaymentTransaction) void
    }

    class RefundTransactionRepository {
        <<interface>>
        +save(RefundTransaction) RefundTransaction
        +findById(UUID) Optional~RefundTransaction~
        +findAll() List~RefundTransaction~
        +delete(RefundTransaction) void
    }

    class TransactionRegisteredPublisher {
        <<interface>>
        +publish(TransactionRegisteredEvent) void
    }

    class TransactionRegisteredBridge {
        -KafkaTemplate kafkaTemplate
        -ObjectMapper mapper
        +publish(TransactionRegisteredEvent) void
    }

    PaymentTransactionService --> PaymentTransactionRepository
    PaymentTransactionService --> TransactionRegisteredPublisher
    RefundTransactionService --> RefundTransactionRepository
    RefundTransactionService --> TransactionRegisteredPublisher
    TransactionRegisteredBridge ..|> TransactionRegisteredPublisher
```

### 3. Controller and DTO Layer

```mermaid
classDiagram
    class PayoutController {
        -PaymentTransactionService service
        +createPayout(PayoutRequest) ResponseEntity~PayoutResponse~
    }

    class PayoutRequest {
        -Amount amount
        -Seller seller
        -String note
        +getAmount() Amount
        +getSeller() Seller
        +getNote() String
    }

    class PayoutResponse {
        -String transactionId
        -String status
        -String note
        +getTransactionId() String
        +getStatus() String
        +getNote() String
    }

    class Amount {
        -String value
        -String currency
        +getValue() String
        +getCurrency() String
    }

    class Seller {
        -String account
        +getAccount() String
    }

    PayoutController --> PaymentTransactionService
    PayoutController ..> PayoutRequest : uses
    PayoutController ..> PayoutResponse : creates
    PayoutRequest *-- Amount
    PayoutRequest *-- Seller
```

### 4. Event Architecture

```mermaid
classDiagram
    class TransactionRegisteredEvent {
        -TransactionId transaction
        -SellerInfo seller
        -PaymentOrderId payment
        -CheckoutId checkout
        -BuyerInfo buyer
        -String amount
        -String currency
        -LocalDateTime at
        -OperationType operationType
        +newTransactionWithCheckout()$ TransactionRegisteredEvent
        +newTransactionWithoutCheckout()$ TransactionRegisteredEvent
        +type() String
        +source() String
        +subject() String
        +getTransaction() TransactionId
        +getSeller() SellerInfo
        +getPayment() PaymentOrderId
        +getCheckout() CheckoutId
        +getBuyer() BuyerInfo
        +getAmount() String
        +getCurrency() String
        +getAt() LocalDateTime
        +getOperationType() OperationType
    }

    class OperationType {
        <<enumeration>>
        PAYMENT
        REFUND
        +subject() String
    }

    class CloudEvent {
        <<external>>
        -String id
        -URI source
        -String type
        -String subject
        -Object data
        -Map~String,Object~ extensions
    }

    TransactionRegisteredEvent --> OperationType
    TransactionRegisteredEvent --> TransactionId
    TransactionRegisteredEvent --> SellerInfo
    TransactionRegisteredEvent --> PaymentOrderId
    TransactionRegisteredEvent --> CheckoutId
    TransactionRegisteredEvent --> BuyerInfo
    CloudEvent ..> TransactionRegisteredEvent : wraps
```

### 5. Kafka Adapter Architecture

```mermaid
classDiagram
    class PaymentOrderApprovedBridge {
        -PaymentTransactionService service
        +handlePaymentOrderApproved(CloudEvent) void
    }

    class RefundApprovedBridge {
        -RefundTransactionService service
        +handleRefundApproved(CloudEvent) void
    }

    class TransactionRegisteredBridge {
        -KafkaTemplate~String,CloudEvent~ sender
        -ObjectMapper mapper
        +publish(TransactionRegisteredEvent) void
    }

    class KafkaTemplate {
        <<external>>
        +send(String, CloudEvent) CompletableFuture
    }

    class CloudEventBuilder {
        <<external>>
        +withId(String) CloudEventBuilder
        +withSource(URI) CloudEventBuilder
        +withType(String) CloudEventBuilder
        +withSubject(String) CloudEventBuilder
        +withData(Object) CloudEventBuilder
        +withExtension(String, Object) CloudEventBuilder
        +build() CloudEvent
    }

    PaymentOrderApprovedBridge --> PaymentTransactionService
    RefundApprovedBridge --> RefundTransactionService
    TransactionRegisteredBridge --> KafkaTemplate
    TransactionRegisteredBridge ..> CloudEventBuilder : uses
```

## Entity Relationship Diagram

```mermaid
erDiagram
    PAYMENT_SELLER_TRANSACTION {
        UUID transaction_id PK
        VARCHAR amount
        VARCHAR currency
        VARCHAR checkout_id
        VARCHAR buyer_document
        VARCHAR buyer_name
        VARCHAR payment_order_id
        VARCHAR seller_id
    }

    REFUND_SELLER_TRANSACTION {
        UUID transaction_id PK
        VARCHAR amount
        VARCHAR currency
        VARCHAR buyer_document
        VARCHAR buyer_name
        VARCHAR payment_order_id
        VARCHAR seller_id
    }

    SELLER {
        VARCHAR seller_id PK
        VARCHAR seller_name
        VARCHAR seller_account
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    BUYER {
        VARCHAR buyer_document PK
        VARCHAR buyer_name
        VARCHAR buyer_email
        VARCHAR buyer_phone
        TIMESTAMP created_at
    }

    PAYMENT_ORDER {
        VARCHAR payment_order_id PK
        VARCHAR order_status
        DECIMAL total_amount
        VARCHAR currency
        TIMESTAMP created_at
        TIMESTAMP completed_at
    }

    CHECKOUT_SESSION {
        VARCHAR checkout_id PK
        VARCHAR session_status
        TIMESTAMP created_at
        TIMESTAMP expires_at
    }

    PAYMENT_SELLER_TRANSACTION }|--|| SELLER : "has seller"
    PAYMENT_SELLER_TRANSACTION }|--|| BUYER : "has buyer"
    PAYMENT_SELLER_TRANSACTION }|--|| PAYMENT_ORDER : "references"
    PAYMENT_SELLER_TRANSACTION }|--|| CHECKOUT_SESSION : "has checkout"
    
    REFUND_SELLER_TRANSACTION }|--|| SELLER : "has seller"
    REFUND_SELLER_TRANSACTION }|--|| BUYER : "has buyer"
    REFUND_SELLER_TRANSACTION }|--|| PAYMENT_ORDER : "references original"
```

## Sequence Diagrams

### Use Case 1: Create Payout Transaction

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant API Gateway
    participant PayoutController
    participant PaymentTransactionService
    participant PaymentTransactionRepository
    participant TransactionRegisteredBridge
    participant Kafka
    participant Analytics Service

    Client->>API Gateway: POST /payouts
    API Gateway->>API Gateway: Authenticate & Authorize
    API Gateway->>PayoutController: Forward Request
    PayoutController->>PayoutController: Validate PayoutRequest
    PayoutController->>PayoutController: Generate Transaction ID
    PayoutController->>PayoutController: Create PaymentTransaction
    PayoutController->>PaymentTransactionService: register(paymentTransaction)
    
    activate PaymentTransactionService
    PaymentTransactionService->>PaymentTransactionRepository: save(paymentTransaction)
    PaymentTransactionRepository->>Database: INSERT transaction
    Database-->>PaymentTransactionRepository: Success
    PaymentTransactionRepository-->>PaymentTransactionService: Saved Entity
    
    PaymentTransactionService->>PaymentTransactionService: Create TransactionRegisteredEvent
    PaymentTransactionService->>TransactionRegisteredBridge: publish(event)
    
    activate TransactionRegisteredBridge
    TransactionRegisteredBridge->>TransactionRegisteredBridge: Build CloudEvent
    TransactionRegisteredBridge->>Kafka: send(merchant-account, CloudEvent)
    Kafka-->>TransactionRegisteredBridge: Acknowledged
    deactivate TransactionRegisteredBridge
    
    PaymentTransactionService-->>PayoutController: Complete
    deactivate PaymentTransactionService
    
    PayoutController->>PayoutController: Create PayoutResponse
    PayoutController-->>API Gateway: 201 Created + Response
    API Gateway-->>Client: HTTP Response
    
    Kafka->>Analytics Service: Consume Event
    Analytics Service->>Analytics Service: Process Transaction
```

### Use Case 2: Process Payment Order Approved Event

```mermaid
sequenceDiagram
    autonumber
    participant Payment Service
    participant Kafka
    participant PaymentOrderApprovedBridge
    participant PaymentTransactionService
    participant PaymentTransactionRepository
    participant TransactionRegisteredBridge
    participant Downstream Services

    Payment Service->>Kafka: Publish PaymentOrderApproved
    Kafka->>PaymentOrderApprovedBridge: Consume CloudEvent
    
    activate PaymentOrderApprovedBridge
    PaymentOrderApprovedBridge->>PaymentOrderApprovedBridge: Extract Event Data
    PaymentOrderApprovedBridge->>PaymentOrderApprovedBridge: Map to PaymentTransaction
    PaymentOrderApprovedBridge->>PaymentTransactionService: register(paymentTransaction)
    
    activate PaymentTransactionService
    PaymentTransactionService->>PaymentTransactionRepository: save(paymentTransaction)
    PaymentTransactionRepository->>Database: INSERT transaction
    Database-->>PaymentTransactionRepository: Success
    PaymentTransactionRepository-->>PaymentTransactionService: Saved Entity
    
    PaymentTransactionService->>PaymentTransactionService: Create TransactionRegisteredEvent
    PaymentTransactionService->>TransactionRegisteredBridge: publish(event)
    
    activate TransactionRegisteredBridge
    TransactionRegisteredBridge->>TransactionRegisteredBridge: Build CloudEvent
    TransactionRegisteredBridge->>Kafka: send(merchant-account, CloudEvent)
    Kafka-->>TransactionRegisteredBridge: Acknowledged
    deactivate TransactionRegisteredBridge
    
    PaymentTransactionService-->>PaymentOrderApprovedBridge: Complete
    deactivate PaymentTransactionService
    
    PaymentOrderApprovedBridge->>PaymentOrderApprovedBridge: Log Success
    deactivate PaymentOrderApprovedBridge
    
    Kafka->>Downstream Services: Distribute Event
    Downstream Services->>Downstream Services: Process Transaction
```

### Use Case 3: Process Refund Transaction

```mermaid
sequenceDiagram
    autonumber
    participant Refund Service
    participant Kafka
    participant RefundApprovedBridge
    participant RefundTransactionService
    participant RefundTransactionRepository
    participant TransactionRegisteredBridge
    participant Notification Service
    participant Accounting Service

    Refund Service->>Kafka: Publish RefundApproved
    Kafka->>RefundApprovedBridge: Consume CloudEvent
    
    activate RefundApprovedBridge
    RefundApprovedBridge->>RefundApprovedBridge: Extract Event Data
    RefundApprovedBridge->>RefundApprovedBridge: Map to RefundTransaction
    RefundApprovedBridge->>RefundTransactionService: register(refundTransaction)
    
    activate RefundTransactionService
    RefundTransactionService->>RefundTransactionRepository: save(refundTransaction)
    RefundTransactionRepository->>Database: INSERT refund
    Database-->>RefundTransactionRepository: Success
    RefundTransactionRepository-->>RefundTransactionService: Saved Entity
    
    RefundTransactionService->>RefundTransactionService: Create TransactionRegisteredEvent (no checkout)
    RefundTransactionService->>TransactionRegisteredBridge: publish(event)
    
    activate TransactionRegisteredBridge
    TransactionRegisteredBridge->>TransactionRegisteredBridge: Build CloudEvent
    TransactionRegisteredBridge->>TransactionRegisteredBridge: Set subject = "refund"
    TransactionRegisteredBridge->>Kafka: send(merchant-account, CloudEvent)
    Kafka-->>TransactionRegisteredBridge: Acknowledged
    deactivate TransactionRegisteredBridge
    
    RefundTransactionService-->>RefundApprovedBridge: Complete
    deactivate RefundTransactionService
    
    RefundApprovedBridge->>RefundApprovedBridge: Log Success
    deactivate RefundApprovedBridge
    
    par Parallel Processing
        Kafka->>Notification Service: Consume Event
        Notification Service->>Notification Service: Send Refund Notification
    and
        Kafka->>Accounting Service: Consume Event
        Accounting Service->>Accounting Service: Update Ledger
    end
```

### Use Case 4: Transaction Query and Reporting

```mermaid
sequenceDiagram
    autonumber
    participant Report Service
    participant API Gateway
    participant Analytics API
    participant Apache Pinot
    participant Kafka
    participant TransactionRegisteredBridge

    Note over TransactionRegisteredBridge, Kafka: Continuous Event Stream
    
    loop Transaction Processing
        TransactionRegisteredBridge->>Kafka: Publish Transaction Events
        Kafka->>Apache Pinot: Stream to Pinot (Real-time ingestion)
        Apache Pinot->>Apache Pinot: Index and Store
    end
    
    Report Service->>API Gateway: GET /analytics/transactions
    API Gateway->>Analytics API: Forward Request
    Analytics API->>Analytics API: Build Query
    Analytics API->>Apache Pinot: Execute SQL Query
    
    Apache Pinot->>Apache Pinot: Process Query
    Note over Apache Pinot: Aggregate by seller,<br/>time window, currency
    
    Apache Pinot-->>Analytics API: Query Results
    Analytics API->>Analytics API: Format Response
    Analytics API-->>API Gateway: JSON Response
    API Gateway-->>Report Service: Analytics Data
    
    Report Service->>Report Service: Generate Report
    Report Service->>Report Service: Display Dashboard
```

### Use Case 5: Error Handling and Compensation

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant PayoutController
    participant PaymentTransactionService
    participant Database
    participant TransactionRegisteredBridge
    participant Kafka
    participant Error Handler
    participant Compensation Service

    Client->>PayoutController: POST /payouts
    PayoutController->>PaymentTransactionService: register(paymentTransaction)
    
    activate PaymentTransactionService
    PaymentTransactionService->>Database: save(transaction)
    Database-->>PaymentTransactionService: Success
    
    PaymentTransactionService->>TransactionRegisteredBridge: publish(event)
    activate TransactionRegisteredBridge
    TransactionRegisteredBridge->>Kafka: send(CloudEvent)
    Kafka--xTransactionRegisteredBridge: Error: Broker Unavailable
    
    TransactionRegisteredBridge->>TransactionRegisteredBridge: Retry with backoff
    loop Retry 3 times
        TransactionRegisteredBridge->>Kafka: send(CloudEvent)
        Kafka--xTransactionRegisteredBridge: Error
    end
    
    TransactionRegisteredBridge->>Error Handler: Log Error
    Error Handler->>Error Handler: Create Error Event
    Error Handler->>Compensation Service: Trigger Compensation
    
    activate Compensation Service
    Compensation Service->>Database: Mark transaction as PENDING_EVENT
    Database-->>Compensation Service: Updated
    Compensation Service->>Compensation Service: Schedule retry job
    deactivate Compensation Service
    
    TransactionRegisteredBridge-->>PaymentTransactionService: Publishing Failed
    deactivate TransactionRegisteredBridge
    
    PaymentTransactionService->>PaymentTransactionService: Handle Exception
    PaymentTransactionService-->>PayoutController: Error Response
    deactivate PaymentTransactionService
    
    PayoutController-->>Client: 500 Internal Server Error
    
    Note over Compensation Service: Later, when Kafka is available
    Compensation Service->>Database: Query PENDING_EVENT transactions
    Database-->>Compensation Service: Pending Transactions
    Compensation Service->>TransactionRegisteredBridge: Republish events
    TransactionRegisteredBridge->>Kafka: send(CloudEvent)
    Kafka-->>TransactionRegisteredBridge: Success
    Compensation Service->>Database: Update status to COMPLETED
```

## Component Interaction Diagram

```mermaid
graph TB
    subgraph "External Systems"
        ES1[Payment Gateway]
        ES2[Banking API]
        ES3[Fraud Detection]
        ES4[Notification Service]
    end
    
    subgraph "API Layer"
        AG[API Gateway]
        PC[PayoutController]
    end
    
    subgraph "Domain Layer"
        PTS[PaymentTransactionService]
        RTS[RefundTransactionService]
        PE[PaymentTransaction Entity]
        RE[RefundTransaction Entity]
    end
    
    subgraph "Infrastructure Layer"
        PTR[PaymentTransactionRepository]
        RTR[RefundTransactionRepository]
        TRB[TransactionRegisteredBridge]
        PAB[PaymentOrderApprovedBridge]
        RAB[RefundApprovedBridge]
    end
    
    subgraph "Data Storage"
        PG[(PostgreSQL)]
        K[Apache Kafka]
    end
    
    subgraph "Analytics Layer"
        AP[Apache Pinot]
        G[Grafana]
        ELK[ELK Stack]
    end
    
    ES1 -->|Payment Approval| K
    AG --> PC
    PC --> PTS
    PTS --> PE
    PTS --> PTR
    PTR --> PG
    PTS --> TRB
    TRB --> K
    
    K --> PAB
    PAB --> PTS
    K --> RAB
    RAB --> RTS
    RTS --> RE
    RTS --> RTR
    RTR --> PG
    RTS --> TRB
    
    K --> AP
    K --> ES3
    K --> ES4
    K --> ELK
    AP --> G
    
    style PTS fill:#f9f,stroke:#333,stroke-width:4px
    style RTS fill:#f9f,stroke:#333,stroke-width:4px
    style K fill:#ff9,stroke:#333,stroke-width:4px
    style PG fill:#9ff,stroke:#333,stroke-width:4px
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "Kubernetes Cluster"
        subgraph "Namespace: paymentic"
            subgraph "Deployment: wallet-service"
                P1[Pod 1]
                P2[Pod 2]
                P3[Pod 3]
            end
            
            SVC[Service: wallet-service]
            HPA[HorizontalPodAutoscaler]
            
            subgraph "ConfigMaps & Secrets"
                CM[ConfigMap: wallet-config]
                S[Secret: wallet-secrets]
            end
        end
        
        subgraph "Namespace: ingress"
            IG[Ingress Controller]
        end
        
        subgraph "Namespace: monitoring"
            PROM[Prometheus]
            GRAF[Grafana]
        end
        
        subgraph "Namespace: kafka"
            KB1[Kafka Broker 1]
            KB2[Kafka Broker 2]
            KB3[Kafka Broker 3]
        end
    end
    
    subgraph "External Services"
        RDS[(AWS RDS PostgreSQL)]
        SM[AWS Secrets Manager]
        CW[CloudWatch]
    end
    
    IG --> SVC
    SVC --> P1
    SVC --> P2
    SVC --> P3
    
    P1 --> CM
    P1 --> S
    P2 --> CM
    P2 --> S
    P3 --> CM
    P3 --> S
    
    P1 --> RDS
    P2 --> RDS
    P3 --> RDS
    
    P1 --> KB1
    P2 --> KB2
    P3 --> KB3
    
    HPA --> P1
    HPA --> P2
    HPA --> P3
    
    PROM --> P1
    PROM --> P2
    PROM --> P3
    
    P1 --> CW
    P2 --> CW
    P3 --> CW
    
    S -.->|External Secrets Operator| SM
    
    style P1 fill:#f96,stroke:#333,stroke-width:2px
    style P2 fill:#f96,stroke:#333,stroke-width:2px
    style P3 fill:#f96,stroke:#333,stroke-width:2px
    style RDS fill:#9ff,stroke:#333,stroke-width:2px
```

## Summary

These diagrams provide a comprehensive view of the Paymentic Wallet architecture:

1. **Class Diagrams** show the object-oriented structure and relationships between entities, services, and repositories
2. **Entity Relationship Diagram** illustrates the database schema and relationships
3. **Sequence Diagrams** demonstrate the flow of operations for the top 5 use cases:
   - Create Payout Transaction
   - Process Payment Order Approved Event
   - Process Refund Transaction
   - Transaction Query and Reporting
   - Error Handling and Compensation
4. **Component Interaction Diagram** provides a high-level view of system components and their interactions
5. **Deployment Architecture** shows how the system is deployed in a Kubernetes environment

These diagrams serve as essential documentation for understanding the system's structure, behavior, and deployment, making it easier for developers to work with and maintain the Paymentic Wallet service.