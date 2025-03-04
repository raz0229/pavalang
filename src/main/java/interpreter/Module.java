package interpreter;

public class Module {
    public final String name;
    public final Environment env;
    public Module(String name, Environment env) {
        this.name = name;
        this.env = env;
    }
    @Override
    public String toString() {
        return "<module " + name + ">";
    }
}