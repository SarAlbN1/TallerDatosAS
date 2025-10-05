package cliente.soap.users;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

/**
 * Clase generada para ValidatePaymentRequest del esquema XSD
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paymentMethod",
    "amount",
    "currency",
    "requestId"
})
@XmlRootElement(name = "ValidatePaymentRequest", namespace = "http://cliente.com/users")
public class ValidatePaymentRequest {

    @XmlElement(required = true)
    protected String paymentMethod;
    
    @XmlElement(required = true)
    protected BigDecimal amount;
    
    protected String currency;
    
    protected String requestId;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String value) {
        this.currency = value;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String value) {
        this.requestId = value;
    }
}
