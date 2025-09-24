package cliente.soap.products;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Product", propOrder = {"id", "name", "organization", "category"})
public class Product {
  @XmlElement
  private Long id;
  @XmlElement(required = true)
  private String name;
  @XmlElement(required = true)
  private Organization organization;
  @XmlElement(required = true)
  private Category category;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Organization getOrganization() { return organization; }
  public void setOrganization(Organization organization) { this.organization = organization; }
  public Category getCategory() { return category; }
  public void setCategory(Category category) { this.category = category; }
}


