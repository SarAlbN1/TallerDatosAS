package cliente.soap.products;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetProductsResponse", propOrder = {"products"})
@XmlRootElement(name = "GetProductsResponse", namespace = "http://example.com/products")
public class GetProductsResponse {
  @XmlElement(name = "products")
  private Products products;

  public Products getProducts() { return products; }
  public void setProducts(Products products) { this.products = products; }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "Products", propOrder = {"product"})
  public static class Products {
    @XmlElement(name = "product")
    private List<Product> product = new ArrayList<>();
    public List<Product> getProduct() { return product; }
  }
}


