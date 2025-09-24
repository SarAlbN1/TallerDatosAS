package cliente.soap.products;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Category", propOrder = {"id", "name", "description"})
public class Category {
  @XmlElement
  private Long id;
  @XmlElement(required = true)
  private String name;
  @XmlElement
  private String description;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}


