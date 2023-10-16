package com.paymentic.wallet.infra.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity(name = "wallet_event")
public class Event {

  @Id
  @Column(name = "event_id")
  private UUID id;
  public Event(){}
  public Event(UUID id) {
    this.id = id;
  }
  public UUID getId() {
    return id;
  }

}
