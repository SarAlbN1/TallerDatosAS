package cliente.soap.products;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateProductRequest", propOrder = {"product"})
@XmlRootElement(name = "CreateProductRequest", namespace = "http://example.com/products")
public class CreateProductRequest {
  @XmlElement(required = true)
  private Product product;
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
}


