package com.example.spliteasybackend.bills.domain.models.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Money {

    private BigDecimal value;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    protected Money() {
        this.value = BigDecimal.ZERO;
    }

    public Money(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Money must be zero or positive");
        this.value = value;
    }

    public BigDecimal value() {
        return value;
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money subtract(Money other) {
        var result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Resulting money cannot be negative");
        return new Money(result);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return Objects.equals(value, money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
