package ro.upb.acs.fleetman.employee.driver;

import jakarta.persistence.*;

import java.sql.Date;

@Embeddable
public class License {

    @Column(name = "license_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    @Column(name = "issue_date", nullable = false)
    private Date issueDate;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
