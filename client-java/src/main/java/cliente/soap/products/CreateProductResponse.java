package cliente.soap.products;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateProductResponse", propOrder = {"product"})
@XmlRootElement(name = "CreateProductResponse", namespace = "http://example.com/products")
public class CreateProductResponse {
  @XmlElement(required = true)
  private Product product;
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
}


