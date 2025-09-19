package co.com.crediya.model;

import co.com.crediya.model.valueobjects.*;
import java.time.LocalDate;

public class CompleteUser {
    private final Long id;
    private final FullName fullName;
    private final Email email;
    private final DocumentIdentity documentIdentity;
    private final BirthDate birthDate;
    private final Phone phone;
    private final RoleId roleId;
    private final BaseSalary baseSalary;

    public CompleteUser(Long id, String firstName, String lastName, String email, 
                       String documentIdentity, LocalDate birthDate, String phone, 
                       Long roleId, Integer baseSalary) {
        this.id = id;
        this.fullName = FullName.of(firstName, lastName);
        this.email = Email.of(email);
        this.documentIdentity = DocumentIdentity.of(documentIdentity);
        this.birthDate = BirthDate.of(birthDate);
        this.phone = Phone.of(phone);
        this.roleId = RoleId.of(roleId);
        this.baseSalary = BaseSalary.of(baseSalary);
        this.role = null;
    }

    public Long getId() { return id; }
    public String getFirstName() { return fullName.getFirstName(); }
    public String getLastName() { return fullName.getLastName(); }
    public String getEmail() { return email.getValue(); }
    public String getDocumentIdentity() { return documentIdentity.getValue(); }
    public LocalDate getBirthDate() { return birthDate.getDate(); }
    public String getPhone() { return phone.getNumber(); }
    public Long getRoleId() { return roleId.getValue(); }
    public Integer getBaseSalary() { return baseSalary.getValue(); }

    public FullName getFullName() { return fullName; }
    public Email getEmailValue() { return email; }
    public DocumentIdentity getDocument() { return documentIdentity; }
    public BirthDate getBirthDateValue() { return birthDate; }
    public Phone getPhoneValue() { return phone; }
    public RoleId getRoleIdValue() { return roleId; }
    public BaseSalary getSalary() { return baseSalary; }
    
    private Role role;
    
    public Role getRole() { 
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}