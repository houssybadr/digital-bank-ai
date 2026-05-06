package ma.enset.ebankingbackend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DebitDTO {
    @NotBlank(message = "Account ID is required")
    private String accountId;

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    private String description;
}
