package cliente.soap;

import cliente.application.models.Category;
import cliente.application.models.Organization;
import cliente.application.models.Product;
import cliente.application.services.ProductService;
import cliente.soap.products.CreateProductRequest;
import cliente.soap.products.CreateProductResponse;
import cliente.soap.products.GetProductsRequest;
import cliente.soap.products.GetProductsResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class ProductsEndpoint {

  private static final String NAMESPACE_URI = "http://example.com/products";

  private final ProductService productService;

  public ProductsEndpoint(ProductService productService) {
    this.productService = productService;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetProductsRequest")
  @ResponsePayload
  public GetProductsResponse getProducts(@RequestPayload GetProductsRequest request) {
    List<Product> products = productService.findAll();

    GetProductsResponse response = new GetProductsResponse();
    GetProductsResponse.Products container = new GetProductsResponse.Products();

    for (Product p : products) {
      cliente.soap.products.Product sp = new cliente.soap.products.Product();
      sp.setId(p.getId());
      sp.setName(p.getName());

      cliente.soap.products.Organization so = new cliente.soap.products.Organization();
      Organization po = p.getOrganization();
      if (po != null) {
        so.setId(po.getId());
        so.setName(po.getName());
      }
      sp.setOrganization(so);

      cliente.soap.products.Category sc = new cliente.soap.products.Category();
      Category pc = p.getCategory();
      if (pc != null) {
        sc.setId(pc.getId());
        sc.setName(pc.getName());
        sc.setDescription(pc.getDescription());
      }
      sp.setCategory(sc);

      container.getProduct().add(sp);
    }

    response.setProducts(container);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateProductRequest")
  @ResponsePayload
  public CreateProductResponse createProduct(@RequestPayload CreateProductRequest request) {
    cliente.soap.products.Product sp = request.getProduct();

    Product p = Product.builder()
        .name(sp.getName())
        .organization(Organization.builder()
            .id(sp.getOrganization() != null ? sp.getOrganization().getId() : null)
            .name(sp.getOrganization() != null ? sp.getOrganization().getName() : null)
            .build())
        .category(Category.builder()
            .id(sp.getCategory() != null ? sp.getCategory().getId() : null)
            .name(sp.getCategory() != null ? sp.getCategory().getName() : null)
            .description(sp.getCategory() != null ? sp.getCategory().getDescription() : null)
            .build())
        .build();

    Product saved = productService.create(p);

    CreateProductResponse response = new CreateProductResponse();
    cliente.soap.products.Product out = new cliente.soap.products.Product();
    out.setId(saved.getId());
    out.setName(saved.getName());

    cliente.soap.products.Organization outOrg = new cliente.soap.products.Organization();
    if (saved.getOrganization() != null) {
      outOrg.setId(saved.getOrganization().getId());
      outOrg.setName(saved.getOrganization().getName());
    }
    out.setOrganization(outOrg);

    cliente.soap.products.Category outCat = new cliente.soap.products.Category();
    if (saved.getCategory() != null) {
      outCat.setId(saved.getCategory().getId());
      outCat.setName(saved.getCategory().getName());
      outCat.setDescription(saved.getCategory().getDescription());
    }
    out.setCategory(outCat);

    response.setProduct(out);
    return response;
  }
}


