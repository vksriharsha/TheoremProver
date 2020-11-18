package edu.iastate.cs472.proj2;

import java.util.Objects;

public class Literal {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return Objects.equals(name, literal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
