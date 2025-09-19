package co.com.crediya.model.exceptions;

public class InsufficientPermissionsException extends RuntimeException {

    private final String requiredRole;
    private final String userRole;
    private final String userId;

    public InsufficientPermissionsException(String message, String requiredRole, String userRole, String userId) {
        super(message);
        this.requiredRole = requiredRole;
        this.userRole = userRole;
        this.userId = userId;
    }

    public InsufficientPermissionsException(String message, String requiredRole) {
        super(message);
        this.requiredRole = requiredRole;
        this.userRole = null;
        this.userId = null;
    }


    public static InsufficientPermissionsException registrationPermissionRequired(String userId, String currentRole) {
        return new InsufficientPermissionsException(
            "insufficient permissions: Required role ADMIN o ASESOR for register of user",
            "ADMIN o ASESOR", 
            currentRole,
            userId
        );
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserId() {
        return userId;
    }
}