package com.buysell.modules.user.dto;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<UUID> roleIds;
    private UUID branchId;
}
