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

    private final File FOLDER = new File(System.getProperty("user.dir"));

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
        File[] listOfFiles;
        listOfFiles = FOLDER.listFiles();

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
