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

    private final File FOLDER = new File(System.getProperty("user.dir"));

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
        File[] listOfFiles;
        listOfFiles = FOLDER.listFiles();

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
