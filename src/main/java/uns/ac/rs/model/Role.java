package uns.ac.rs.model;

public enum Role {
    ADMIN("admin"),
    GUEST("guest"),
    HOST("host");

    private final String name;

    private Role(String s) {
        name = s;
    }

    public String getName() {
        return this.name;
    }
}
