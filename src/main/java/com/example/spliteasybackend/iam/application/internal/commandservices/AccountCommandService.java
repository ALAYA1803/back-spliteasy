package com.example.spliteasybackend.iam.application.internal.commandservices;

import com.example.spliteasybackend.iam.interfaces.rest.resources.AccountProfileResource;

public interface AccountCommandService {
    AccountProfileResource getMyProfile(String principalName);
    AccountProfileResource updateMyProfile(String principalName, String username, String email);
    void changeMyPassword(String principalName, String currentPassword, String newPassword);
    void deleteMyAccount(String principalName);
}
