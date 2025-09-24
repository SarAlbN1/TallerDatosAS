package cliente.application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false, unique = true, length = 120)
  private String name;

  @Column(length = 512)
  private String description;

  @JsonIgnore
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
  @Builder.Default
  private List<Product> products = new ArrayList<>();
}
