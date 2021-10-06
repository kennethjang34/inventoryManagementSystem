package model;

import java.time.LocalDate;

public class ExpiringProduct extends Product {
    private LocalDate expirationDate;



    public ExpiringProduct() {
        super("abc", 123456789, "A1");
        expirationDate = LocalDate.of(2023, 12, 31);
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
