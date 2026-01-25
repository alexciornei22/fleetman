package ro.upb.acs.fleetman.employee.driver;

import jakarta.persistence.*;
import ro.upb.acs.fleetman.employee.Employee;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "driver")
public class Driver extends Employee {

    @ElementCollection
    private List<License> licenses;

    @Column(name = "medical_certificate_expiry_date")
    private Date medicalCertificateExpiryDate;

    @Column(name = "tachograph_card_number", unique = true, length = 20)
    private String tachographCardNumber;

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public Date getMedicalCertificateExpiryDate() {
        return medicalCertificateExpiryDate;
    }

    public void setMedicalCertificateExpiryDate(Date medicalCertificateExpiryDate) {
        this.medicalCertificateExpiryDate = medicalCertificateExpiryDate;
    }

    public String getTachographCardNumber() {
        return tachographCardNumber;
    }

    public void setTachographCardNumber(String tachographCardNumber) {
        this.tachographCardNumber = tachographCardNumber;
    }
}
