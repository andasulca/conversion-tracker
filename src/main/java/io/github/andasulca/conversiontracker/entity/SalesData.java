package io.github.andasulca.conversiontracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "sales_data",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"tracking_id", "visit_date", "sale_date", "product_id", "action_type"}
        )
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty("trackingId")
    @Column(name = "tracking_id", nullable = false)
    private String trackingId;

    @JsonProperty("visitDate")
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @JsonProperty("saleDate")
    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @JsonProperty("salePrice")
    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @JsonProperty("commissionAmount")
    @Column(name = "commission_amount", precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "landing_page_code")
    private String landingPageCode;

    @Column(name = "action_type")
    private String actionType;

    @JsonProperty("product")
    @Column(name = "product_id")
    private String productId;

    /**
     * Returns a timestamp used for filtering: saleDate if exists, else visitDate.
     */
    public LocalDateTime getTimestamp() {
        return (saleDate != null) ? saleDate : visitDate;
    }
}
