import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private JCheckBox copyCharacterSpecificSettingsCheckBox;
    private JCheckBox copyAccountSpecificSettingsCheckBox;
    private JPanel mainView;
    private JComboBox charSelectionCombo;
    private JComboBox userSelectionCombo;
    private ButtonGroup charSelect;

    private final File FOLDER = new File(System.getProperty("user.home"));

    private ArrayList<SettingFile> charList = new ArrayList<>();
    private ArrayList<SettingFile> userList = new ArrayList<>();


    private void updateOverwriteButtonState() {
        overwriteButton.setEnabled(copyAccountSpecificSettingsCheckBox.isSelected() || copyCharacterSpecificSettingsCheckBox.isSelected());
    }

    private void initUI() {

        for (SettingFile settingFile : charList) {
            charSelectionCombo.addItem(settingFile.toString());
        }

        for (SettingFile settingFile : userList) {
            userSelectionCombo.addItem(settingFile.toString());
        }

        copyCharacterSpecificSettingsCheckBox.addActionListener(actionEvent -> {
            updateOverwriteButtonState();
        });

        copyAccountSpecificSettingsCheckBox.addActionListener(actionEvent -> {
            updateOverwriteButtonState();
        });

        // Overwrite button
        overwriteButton.addActionListener(actionEvent -> {
            int selectedCharIdx = charSelectionCombo.getSelectedIndex();
            int selectedUserIdx = userSelectionCombo.getSelectedIndex();

            if(copyAccountSpecificSettingsCheckBox.isSelected()) {
                overwriteWith(charList.get(selectedCharIdx));
            }

            if(copyCharacterSpecificSettingsCheckBox.isSelected()) {
                overwriteWith(userList.get(selectedUserIdx));
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
                } else if (f.isUserFile()) {
                    // get users
                    userList.add(f);
                }
            }
        }

        if (userList.isEmpty() || charList.isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Missing user or char file !");
            System.exit(0);
        }

        Collections.sort(charList, Comparator.comparingLong(File::lastModified));
        Collections.sort(userList, Comparator.comparingLong(File::lastModified));

        Collections.reverse(charList);
        Collections.reverse(userList);

        initUI();
    }
}
