public interface FailSafe3<T> {
    T invoke();

    default T failSafe() {
        try {
            return invoke();
        } catch (Throwable e) {
            System.out.println("Failed to invoke " + this.getClass().getSimpleName());
            return null;
        }
    }
}
