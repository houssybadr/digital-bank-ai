package ma.enset.ebankingbackend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferRequestDTO {
    @NotBlank(message = "Source account ID is required")
    private String accountSource;

    @NotBlank(message = "Destination account ID is required")
    private String accountDestination;

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    private String description;
}
