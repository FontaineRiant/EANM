import java.io.File;

/**
 * Project : EANM
 * Author(s) : Bertral
 * Date : 28.02.17
 */
public class SettingFile extends File{
    private final String CHAR_PREFIX = "core_char_";
    private final String USER_PREFIX = "core_user_";

    private long id;

    public SettingFile(String s) {
        super(s);
        id = Long.valueOf("0" + this.getName().replaceAll("\\D", ""));
    }

    public SettingFile(File f) {
        this(f.getPath());
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public long getId() {
        return id;
    }

    public boolean isCharFile() {
        return getName().startsWith(CHAR_PREFIX) && !getName().startsWith(CHAR_PREFIX + "_");
    }

    public boolean isUserFile() {
        return getName().startsWith(USER_PREFIX) && !getName().startsWith(USER_PREFIX + "_");
    }
}
