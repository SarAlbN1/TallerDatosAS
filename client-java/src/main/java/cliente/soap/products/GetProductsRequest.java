package cliente.soap.products;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetProductsRequest")
@XmlRootElement(name = "GetProductsRequest", namespace = "http://example.com/products")
public class GetProductsRequest {}


