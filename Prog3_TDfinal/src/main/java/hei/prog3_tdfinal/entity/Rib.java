package hei.prog3_tdfinal.entity;

import java.util.regex.Pattern;

public class Rib {
    private final String value;
    private static final Pattern RIB_PATTERN = Pattern.compile("^\\d{23}$");

    public Rib(String value) {
        if (value == null || !RIB_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("RIB must be exactly 23 digits (format: 5 bank code + 5 branch code + 11 account number + 2 key digits)");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getBankCode() {
        return value.substring(0, 5);
    }

    public String getBranchCode() {
        return value.substring(5, 10);
    }

    public String getAccountNumber() {
        return value.substring(10, 21);
    }

    public String getKeyDigits() {
        return value.substring(21, 23);
    }

    @Override
    public String toString() {
        return value;
    }
}
