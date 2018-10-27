import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Project : EANM
 * Author(s) : Bertral
 * Date : 28.02.17
 */
public class EANM {
    public static void main(String[] args) {
        JFrame mf = new JFrame("Eve Accounts Neat Manager");
        mf.setContentPane(new EANM().mainView);
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mf.pack();
        mf.setLocationRelativeTo(null);
        mf.setVisible(true);
    }

    private JButton overwriteButton;
    private JRadioButton useMostRecentlyUsedRadioButton;
    private JRadioButton useFirstCreatedRadioButton;
    private JCheckBox copyCharacterSpecificSettingsCheckBox;
    private JCheckBox copyAccountSpecificSettingsCheckBox;
    private JPanel mainView;
    private ButtonGroup charSelect;

    private final File FOLDER = new File(System.getProperty("user.home"));

    private ArrayList<SettingFile> charList = new ArrayList<>();
    private SettingFile charLastModified;
    private SettingFile charOldest;
    private ArrayList<SettingFile> userList = new ArrayList<>();
    private SettingFile userLastModified;
    private SettingFile userOldest;


    private void updateOverwriteButtonState() {
        overwriteButton.setEnabled(copyAccountSpecificSettingsCheckBox.isSelected() || copyCharacterSpecificSettingsCheckBox.isSelected());
    }

    private void initUI() {

        // display ids
        useFirstCreatedRadioButton.setText(useFirstCreatedRadioButton.getText() + " (id : " + charOldest.toString() + ")");
        useMostRecentlyUsedRadioButton.setText(useMostRecentlyUsedRadioButton.getText() + " (id : " + charLastModified.toString() + ")");

        copyCharacterSpecificSettingsCheckBox.addActionListener(actionEvent -> {
            updateOverwriteButtonState();
        });

        copyAccountSpecificSettingsCheckBox.addActionListener(actionEvent -> {
            updateOverwriteButtonState();
        });

        // Overwrite button
        overwriteButton.addActionListener(actionEvent -> {
            if(copyAccountSpecificSettingsCheckBox.isSelected()) {
                overwriteWith(useFirstCreatedRadioButton.isSelected() ? userOldest : userLastModified);
            }

            if(copyCharacterSpecificSettingsCheckBox.isSelected()) {
                overwriteWith(useFirstCreatedRadioButton.isSelected() ? charOldest : charLastModified);
            }

            JOptionPane.showMessageDialog(mainView, "Success !");
            System.exit(0);
        });
    }

    /**
     * overwrite operation
     * @param file file used to overwrite the others
     */
    private void overwriteWith(SettingFile file) {
        ArrayList<SettingFile> list = file.isCharFile() ? new ArrayList<>(charList) : new ArrayList<>(userList);

        list.remove(file);

        for (SettingFile f : list) {
            Path from = file.toPath(); //convert from File to Path
            Path to = f.toPath(); //convert from String to Path
            try {
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public EANM() {
        // get files
        File userFolder;
        if (System.getProperty("os.name").equals("Linux")) {
            File usersFolders = new File(new File(new File(new File(FOLDER, ".eve"), "wineenv"), "drive_c"), "users");
            File[] possibleUserFolders = usersFolders.listFiles(f -> !"Public".equalsIgnoreCase(f.getName()));
            
            if (possibleUserFolders.length == 0) {
                JOptionPane.showMessageDialog(mainView, "Unable to find EVE wineenv folder");
                System.exit(1);
            }

            if (possibleUserFolders.length > 1) {
                String[] options = new String[possibleUserFolders.length];
                for (int i = 0; i < possibleUserFolders.length; i++) {
                    options[i] = possibleUserFolders[i].getName();
                }
                int responseIdx = JOptionPane.showOptionDialog(mainView,
                        "Multiple users available within EVE wineenv folder. Please select one.",
                        "Multiple Users",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]);

                userFolder = possibleUserFolders[responseIdx];
            } else {
                userFolder = possibleUserFolders[0];
            }
        } else {
            userFolder = FOLDER;
        }

        File eveFolder = new File(new File(new File(new File(userFolder, "Local Settings"), "Application Data"), "CCP"), "EVE");

        if (!eveFolder.isDirectory()) {
            JOptionPane.showMessageDialog(mainView, "Missing EVE settings");
            System.exit(1);
        }

        File tranquilitySettingsFolder = new File(eveFolder, "c_tq_tranquility");

        File[] tranquilityProfiles = tranquilitySettingsFolder.listFiles(file -> file.getName().startsWith("settings_"));
        if (tranquilityProfiles.length > 1) {
            String[] options = new String[tranquilityProfiles.length];
            for (int i = 0; i < tranquilityProfiles.length; i++) {
                options[i] = tranquilityProfiles[i].getName();
            }
            int responseIdx = JOptionPane.showOptionDialog(mainView,
                    "Multiple profiles available within EVE wineenv folder. Please select one.",
                    "Multiple profiles",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]);

            eveFolder = tranquilityProfiles[responseIdx];
        } else {
            eveFolder = tranquilityProfiles[0];
        }

        File[] listOfFiles = eveFolder.listFiles();

        // fill file arrays
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                SettingFile f = new SettingFile(listOfFiles[i]);
                if (f.isCharFile()) {
                    // get chars
                    charList.add(f);

                    // check if last modified
                    if (charLastModified == null || f.lastModified() > charLastModified.lastModified()) {
                        charLastModified = f;
                    }

                    // check if oldest
                    if (charOldest == null || f.getId() < charOldest.getId()) {
                        charOldest = f;
                    }
                } else if (f.isUserFile()) {
                    // get users
                    userList.add(f);

                    // check if last modified
                    if (userLastModified == null || f.lastModified() > userLastModified.lastModified()) {
                        userLastModified = f;
                    }

                    // check if oldest
                    if (userOldest == null || f.getId() < userOldest.getId()) {
                        userOldest = f;
                    }
                }
            }
        }

        if (userList.isEmpty() || charList.isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Missing user or char file !");
            System.exit(0);
        }

        initUI();
    }
}
