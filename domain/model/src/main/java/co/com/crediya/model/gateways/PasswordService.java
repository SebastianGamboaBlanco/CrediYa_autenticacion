package co.com.crediya.model.gateways;

public interface PasswordService {
    
    String generateRandomPassword();
    
    String encryptPassword(String plainPassword);
    
    boolean verifyPassword(String plainPassword, String encryptedPassword);
}