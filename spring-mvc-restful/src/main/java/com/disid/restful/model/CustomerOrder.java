package com.disid.restful.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.annotations.RooJavaBean;
import org.springframework.roo.addon.javabean.annotations.RooToString;
import org.springframework.roo.addon.jpa.annotations.entity.RooJpaEntity;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@RooJavaBean
@RooToString
@RooJpaEntity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CustomerOrder {

  /**
   */
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private Date orderDate;

  /**
   */
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private Date shippedDate;

  /**
   */
  private String shipAddress;

  /**
  * Bidirectional composition one-to-many relationship. Parent side.
  * TODO: convertir a List la propiedad details, tal y como está ahora el 
  * remove de lineas y luego añadir no funcionará bien.
  */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<OrderDetail> details = new HashSet<OrderDetail>();

  /**
   * Bidirectional non-aggregation many-to-one relationship. Child side.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private Customer customer;

  /**
   * Adds a list of <b>new</b> details to the order, taking care to update the 
   * relationship from the {@link OrderDetail} to the
   * {@link CustomerOrder} either.
   * @param details to add to the order (required)
   * @throws IllegalArgumentException if the detail is null or empty
   */
  public void addToDetails(Collection<OrderDetail> detailsToAdd) {
    Assert.notEmpty(detailsToAdd, "At least one order detail to add is required");
    int lastDetail = getDetails().size();
    for (OrderDetail detail : detailsToAdd) {
      OrderDetailPK detailPK = new OrderDetailPK(getId(), lastDetail);
      details.add(detail);
      detail.setId(detailPK);
      detail.setCustomerOrder(this);
      lastDetail++;
    }
  }

  /**
   * Removes a detail from the order, taking care to update the 
   * relationship from the {@link OrderDetail} to the
   * {@link CustomerOrder} either.
   * As the {@link #details} property is configured with orphanRemoval
   * to true, the detail will be removed when this order is persisted.
   * @param detail to remove from the order (required)
   * @throws IllegalArgumentException if the detail is null or it isn't
   * a detail of this customer.
   */
  public void removeFromDetails(Collection<OrderDetail> detailsToRemove) {
    Assert.notEmpty(detailsToRemove, "At least one order detail to remove is required");
    for (OrderDetail detail : detailsToRemove) {
      details.remove(detail);
      detail.setCustomerOrder(null);
    }
  }
}
