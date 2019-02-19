package com.npcode.learning.kotlin;

public class ProtectedMethodHolder {
    protected int foo() {
        return 1;
    }

    public int bar() {
        return foo();
    }
}
