package com.buysell.modules.user.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isActive;
    private boolean isLocked;
    private List<String> roles;
    private UUID branchId;
}
