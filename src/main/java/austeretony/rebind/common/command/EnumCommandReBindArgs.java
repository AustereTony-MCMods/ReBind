package austeretony.rebind.common.command;

public enum EnumCommandReBindArgs {

    HELP("help"),
    LIST("list"),
    UPDATE("update");

    public final String arg;

    EnumCommandReBindArgs(String arg) {
        this.arg = arg;
    }

    public static EnumCommandReBindArgs get(String strArg) {
        for (EnumCommandReBindArgs arg : values())
            if (arg.arg.equals(strArg))
                return arg;
        return null;
    }

    @Override
    public String toString() {
        return this.arg;
    }
}
