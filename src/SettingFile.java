import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Project : EANM
 * Author(s) : Bertral
 * Date : 28.02.17
 */
public class SettingFile extends File{
    private final String CHAR_PREFIX = "core_char_";
    private final String USER_PREFIX = "core_user_";

    private long id;
    private CharacterESIResponse esiResponse;

    public SettingFile(String s) {
        super(s);
        id = Long.valueOf("0" + this.getName().replaceAll("\\D", ""));
    }

    public SettingFile(File f) {
        this(f.getPath());
    }

    @Override
    public String toString() {
        if (isCharFile()) {
            return id + " - " + getCharName() + " " + new SimpleDateFormat("YYYY-MM-dd").format(getInfos().birthday);
        } else {
            return Long.toString(id);
        }
    }

    public long getId() {
        return id;
    }

    public String getCharName() {
        return getInfos().name;
    }

    private CharacterESIResponse getInfos() {
        if (esiResponse == null) {
            URL url;
            try {
                url = new URL("https://esi.evetech.net/latest/characters/" + id + "/?datasource=tranquility");
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
            try (InputStream is = url.openStream()) {
                esiResponse = new GsonBuilder().create().fromJson(new InputStreamReader(is), CharacterESIResponse.class);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return esiResponse;
    }

    public boolean isCharFile() {
        return getName().startsWith(CHAR_PREFIX) && !getName().startsWith(CHAR_PREFIX + "_");
    }

    public boolean isUserFile() {
        return getName().startsWith(USER_PREFIX) && !getName().startsWith(USER_PREFIX + "_");
    }

    public static final class CharacterESIResponse {
        @SerializedName("alliance_id")
        private Integer allianceId;

        @SerializedName("ancestry_id")
        private Integer ancestryId;

        @SerializedName("birthday")
        private Date birthday;

        @SerializedName("bloodline_id")
        private Integer bloodlineId;

        @SerializedName("corporation_id")
        private Integer corporationId;

        @SerializedName("description")
        private String description;

        @SerializedName("faction_id")
        private Integer factionId;

        @SerializedName("gender")
        private String gender;

        @SerializedName("name")
        private String name;

        @SerializedName("race_id")
        private Integer raceId;

        @SerializedName("security_status")
        private BigDecimal securityStatus;
    }
}