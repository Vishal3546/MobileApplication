package com.buysell.modules.user.dto;
import lombok.Data;
import java.util.UUID;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateUserRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String email;
    @NotBlank private String phone;
    private List<UUID> roleIds;
    private UUID branchId;
}
